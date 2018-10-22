package com.android.keenjackdaw.blackcat.controller;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.android.keenjackdaw.blackcat.Settings;
import com.android.keenjackdaw.blackcat.BlackCatApplication;
import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.activity.CameraActivity;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.ui.CameraView;
import com.rokid.citrus.citrusfacesdk.CitrusFaceSDK;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;

public class CitrusFaceManager {
    private CitrusFaceManager() {}
    // FIXME It's not an error!!!!!
    private static CitrusFaceManager instance = new CitrusFaceManager();
    private byte[][] byteBuffers = null;
    private CameraActivity cameraActivity = null;
    private Context appContext = null;
    private CitrusFaceSDK citrusFaceSDK = null;
    private static final TimeRange timeRange = new TimeRange();

    @Contract(pure = true)
    public static CitrusFaceManager getInstance() {
        if(instance == null){
            instance = new CitrusFaceManager();
            return instance;
        }
        return instance;
    }

    public void setUpAppInfo(){
        cameraActivity = (CameraActivity) BlackCatApplication.getCurrentActivity().get();
        appContext = cameraActivity.getApplicationContext();

    }

    public void initCitrusFaceSDK() throws BlackCatException{
        citrusFaceSDK = new CitrusFaceSDK();
        if(!auth()){
            throw new BlackCatException("SDK auth failed.");
        }
        citrusFaceSDK.Init(appContext);

        // FIXME It's not an error too... Because CameraView has already initialized before calling findViewBtId
        CameraView cameraView = cameraActivity.getFragmentContainer().getView().findViewById(R.id.camera_view);
        setBuffer(cameraView.getWidth(), cameraView.getHeight());
        Log.i(Settings.TAG,  "cameraView width:" + cameraView.getWidth() + " cameraView width:" + cameraView.getHeight());

        citrusFaceSDK.SetSizeROIWithBuffer(
                cameraView.getWidth(),
                cameraView.getHeight(),
                Settings.IMAGE_CHANNEL_NUM,
                Settings.FRAME_NUM_IN_CACHE,
                byteBuffers,
                (int)Settings.ROI_X * cameraView.getWidth(),
                (int)Settings.ROI_Y * cameraView.getHeight(),
                (int)Settings.ROI_W * cameraView.getWidth(),
                (int)Settings.ROI_H * cameraView.getHeight(),
                Settings.SCALE);

        String dbPath = Environment.getExternalStorageDirectory().getPath() + "face.db";
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

    public int getResFaceNum(){
        return citrusFaceSDK.GetResFaceNum();
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
