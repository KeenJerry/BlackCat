package com.android.keenjackdaw.blackcat.controller;

import android.content.Context;
import android.os.Environment;
import android.util.Pair;

import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.Settings;
import com.android.keenjackdaw.blackcat.BlackCatApplication;
import com.android.keenjackdaw.blackcat.activity.CameraActivity;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.ui.RectView;
import com.rokid.citrus.citrusfacesdk.CitrusFaceSDK;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.List;

public class CitrusFaceManager {
    private CitrusFaceManager() {}
    // FIXME It's not an error!!!!!
    private static CitrusFaceManager instance = new CitrusFaceManager();
    private byte[][] byteBuffers = null;
    private CameraActivity cameraActivity = null;
    private Context appContext = null;
    private CitrusFaceSDK citrusFaceSDK = null;
    private List<String> userProfile = null;
    private static final TimeRange timeRange = new TimeRange();

    @Contract(pure = true)
    public static CitrusFaceManager getInstance() {
        if(instance == null){
            instance = new CitrusFaceManager();
            return instance;
        }
        return instance;
    }

    public void setByteBuffers(byte[][] byteBuffers) {
        this.byteBuffers = byteBuffers;
    }

    public void setUpAppInfo(){
        cameraActivity = (CameraActivity) BlackCatApplication.getCurrentActivity().get();
        appContext = cameraActivity.getApplicationContext();

    }

    public void initCitrusFaceSDK(int width, int height) throws BlackCatException{
        citrusFaceSDK = new CitrusFaceSDK();
        if(!auth()){
            throw new BlackCatException("SDK auth failed.");
        }
        citrusFaceSDK.Init(appContext);

        if(Settings.IS_USING_CAMERA2){
            // FIXME It's not an error too... Because CameraView has already initialized before calling findViewBtId
            setBuffer(width, height);

            citrusFaceSDK.SetSizeROIWithBuffer(
                    width,
                    height,
                    Settings.IMAGE_CHANNEL_NUM,
                    Settings.FRAME_NUM_IN_CACHE,
                    byteBuffers,
                    (int)Settings.ROI_X * width,
                    (int)Settings.ROI_Y * height,
                    (int)Settings.ROI_W * width,
                    (int)Settings.ROI_H * height,
                    Settings.SCALE);
        }
        else{
            // FIXME It's not an error too... Because CameraView has already initialized before calling findViewBtId
            setBuffer(width, height);

            citrusFaceSDK.SetSizeROIWithBuffer(
                    width,
                    height,
                    Settings.IMAGE_CHANNEL_NUM,
                    Settings.FRAME_NUM_IN_CACHE,
                    byteBuffers,
                    (int)Settings.ROI_X * width,
                    (int)Settings.ROI_Y * height,
                    (int)Settings.ROI_W * width,
                    (int)Settings.ROI_H * height,
                    Settings.SCALE);
        }

        // FIXME it's not an error orz...
        String dbPath = cameraActivity.getString(R.string.db_path);
        Settings.ExternalStorageState state = checkExternalStorage();
        if(state != Settings.ExternalStorageState.All_ALLOWED){
            throw new BlackCatException("External storage access not allowed or only allow read.");
        }
        citrusFaceSDK.DBSet(dbPath);

    }

    private Settings.ExternalStorageState checkExternalStorage(){
        String state = Environment.getExternalStorageState();

        switch (state){
            case Environment.MEDIA_MOUNTED:
                return Settings.ExternalStorageState.All_ALLOWED;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                return Settings.ExternalStorageState.ONLY_READ;
            default:
                return Settings.ExternalStorageState.NEITHER_ALLOWED;
        }
    }

    private boolean auth(){
        return citrusFaceSDK.Authenic(Settings.KEY, Settings.SECRET, Settings.DEVICE_ID, Settings.DEVICE_TYPE_ID);
    }

    private void setBuffer(int width, int height){
        byteBuffers = new byte[Settings.IMAGE_CHANNEL_NUM][];

        for(int i = 0; i < Settings.IMAGE_CHANNEL_NUM; i++){
            byteBuffers[i] = new byte[width * height * 3 / 2];
        }
        for (int i = 0; i < Settings.IMAGE_CHANNEL_NUM; i++) {
            Arrays.fill(byteBuffers[i], (byte) 0);
        }
    }

    public void doFaceTrack(){

        timeRange.frameCount++;
        timeRange.frameCurTime = System.currentTimeMillis();
        timeRange.calculateFPS();
        citrusFaceSDK.SetImageInd((int)timeRange.frameCount - 1);
        citrusFaceSDK.FaceTrack();
    }

    public int getFaceNum(){
        return citrusFaceSDK.FaceDetect();
    }

    public int getResFaceNum() {
        return citrusFaceSDK.GetResFaceNum();
    }

    public byte[][] getByteBuffers() {
        return byteBuffers;
    }

    public Pair<Integer, Integer> doFaceRecognition(int index){
        if(citrusFaceSDK.GetResIsused(index)){

            int recognizeResult;

            if(citrusFaceSDK.GetResRecogTryCnt(index) == 0){
                recognizeResult = citrusFaceSDK.FaceRecogWithAttribute(index);
            }
            else
            {
                recognizeResult = citrusFaceSDK.FaceRecog(index);
            }

            int id = citrusFaceSDK.GetResId(index);

            return new Pair<>(recognizeResult, id);
        }
        return new Pair<>(null, null);
    }

    public void drawRect(RectView rectView){

        rectView.lockCanvas();

        if(citrusFaceSDK.GetResFaceNum() > 0) {
            int faceNumRecognized = citrusFaceSDK.GetResListNum();
            for (int i = 0; i < faceNumRecognized; ++i) {
                if (citrusFaceSDK.GetResIsused(i)) {
                    int trackId = citrusFaceSDK.GetResTrackid(i);
                    float[] rectBox = citrusFaceSDK.GetResBBox(i);
                    // TODO Add frontend and backend judgement.

                    float left = 1.0f - rectBox[2];
                    float right = 1.0f - rectBox[0];
                    rectBox[0] = left;
                    rectBox[2] = right;

                    int id = citrusFaceSDK.GetResId(i);
                    int isNewOne = citrusFaceSDK.GetResIsNewone(i);
                    float score = (int) (citrusFaceSDK.GetResScore(i) * 100) / 100.f;
                    int age = (int) citrusFaceSDK.GetResAge(i);
                    int childMark = citrusFaceSDK.GetResChild(i);
                    int genderMark = citrusFaceSDK.GetResGender(i);

                    String child = "Unknown";
                    String gender = "Unknown";

                    if(childMark == 0){
                        child = "adult";
                    }
                    else{
                        if(childMark == 1) {
                            child = "child";
                        }
                    }

                    if(genderMark == 0){
                        gender = "adult";
                    }
                    else{
                        if(genderMark == 1) {
                            gender = "child";
                        }
                    }

                    String result;

                    if (id >= 0) {
                        if(userProfile == null)
                            result = isNewOne + "[" + trackId + "]:id" + id + "-s:" + score + "-[" + (int) ((rectBox[2] - rectBox[0]) * CameraOld.getInstance().getPreviewSize().width) + "x" + (int) ((rectBox[3] - rectBox[1]) * CameraOld.getInstance().getPreviewSize().height) + "]";
                        else
                            result = trackId + ":" + userProfile.get(id);
                    } else {
                        result = isNewOne + "[" + trackId + "]:[" + gender + "," + child + "," + age + "]-s:" + score + "-[" + (int) ((rectBox[2] - rectBox[0]) * CameraOld.getInstance().getPreviewSize().width) + "x" + (int) ((rectBox[3] - rectBox[1]) * CameraOld.getInstance().getPreviewSize().height) + "]";
                    }

                    // TODO Complete draw rect
                    rectView.drawRect(rectBox, result);
                }
            }
        }
        rectView.releaseCanvas();
    }

    static class TimeRange {
        private long frameCount;
        private long frameCurTime;
        private long frameForTime;
        private long frameStep;
        private float frameFPS;

        void reset() {
            frameCount = 0;
            frameCurTime = 0;
            frameForTime = 0;
            frameStep = 100;
            frameFPS = 30.f;
        }

        float calculateFPS() {
            frameFPS = 100000.0f / (frameCurTime - frameForTime);
            frameForTime = frameCurTime;
            return frameFPS;
        }

        boolean updateFPS() {
            return (frameCount % frameStep == 0);
        }
    }
}
