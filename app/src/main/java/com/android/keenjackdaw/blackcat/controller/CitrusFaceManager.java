package com.android.keenjackdaw.blackcat.controller;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.Settings;
import com.android.keenjackdaw.blackcat.BlackCatApplication;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.ui.RectView;
import com.rokid.citrus.citrusfacesdk.CitrusFaceSDK;

import org.jetbrains.annotations.Contract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CitrusFaceManager {
    private CitrusFaceManager() {}
    // FIXME It's not an error!!!!!
    private static CitrusFaceManager instance = new CitrusFaceManager();
    private byte[][] byteBuffers = null;
    private Activity activity = null;
    private Context appContext = null;
    private CitrusFaceSDK citrusFaceSDK = null;
    private List<String> userProfile = null;
    private static final TimeRange timeRange = new TimeRange();
    private String nameFilePath = null;
    private Speeker speeker = null;
    private int time = (int) (System.currentTimeMillis() / 1000);

    @Contract(pure = true)
    public static CitrusFaceManager getInstance() {
        if(instance == null){
            // TODO Delete after debug
            Log.i(Settings.TAG, "SDK instance is null.");
            instance = new CitrusFaceManager();
            return instance;
        }
        Log.i(Settings.TAG, "SDK instance is not null.");
        return instance;
    }

    public void setByteBuffers(byte[][] byteBuffers) {
        this.byteBuffers = byteBuffers;
    }

    public void setUpAppInfo(Activity activity){
        this.activity = activity;
        appContext = activity.getApplicationContext();
    }

    public void initCitrusFaceSDK(int width, int height) throws BlackCatException{
        speeker = new Speeker(activity);

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
        String dbPath = activity.getString(R.string.db_path);
        Settings.ExternalStorageState state = checkExternalStorage();
        if(state != Settings.ExternalStorageState.All_ALLOWED){
            throw new BlackCatException("External storage access not allowed or only allow read.");
        }
        citrusFaceSDK.DBSet(dbPath);
        nameFilePath = citrusFaceSDK.DBName() + ".name";
        userProfile = readNames(nameFilePath);

    }

    public void initCitrusFaceSDKWithoutBuffer() throws BlackCatException{
        citrusFaceSDK = new CitrusFaceSDK();
        if(!auth()){
            throw new BlackCatException("SDK auth failed.");
        }
        // TODO Delete below after debug
        Log.i(Settings.TAG, "SDK app context is " + appContext);
        int i = citrusFaceSDK.Init(appContext);
        Log.i(Settings.TAG, "SDK init return " + i);

        String dbPath = activity.getString(R.string.db_path);
        Settings.ExternalStorageState state = checkExternalStorage();
        if(state != Settings.ExternalStorageState.All_ALLOWED){
            throw new BlackCatException("External storage access not allowed or only allow read.");
        }
        int k =  citrusFaceSDK.DBSet(dbPath);

        // TODO Delete below after debug
        Log.i(Settings.TAG, "DBSet return " + k);
        Log.i(Settings.TAG, "DB path is " + dbPath);
        nameFilePath = citrusFaceSDK.DBName() + ".name";
        userProfile = readNames(nameFilePath);

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

    public int getResListNum() {
        return citrusFaceSDK.GetResListNum();
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

    public void faceRecognition(int index){
        citrusFaceSDK.FaceRecog(index);
    }

    public void drawRect(RectView rectView){
        // TODO Delete after debug
        // Log.i(Settings.TAG, "userProfile");

        rectView.lockCanvas();
        rectView.clearCanvas();

        if(citrusFaceSDK.GetResFaceNum() > 0) {
            int faceNumRecognized = citrusFaceSDK.GetResListNum();
            // TODO Delete below after debug
            Log.i(Settings.TAG, "faceNumRecognized:" + faceNumRecognized);
            for (int i = 0; i < faceNumRecognized; ++i) {
                if (citrusFaceSDK.GetResIsused(i)) {
                    int trackId = citrusFaceSDK.GetResTrackid(i);
                    float[] rectBox = citrusFaceSDK.GetResBBox(i);
                    // TODO Add frontend and backend judgement.

                    // float left = 1.0f - rectBox[2];
                    // float right = 1.0f - rectBox[0];
                    float left = rectBox[2];
                    float right = rectBox[0];
                    rectBox[0] = left;
                    rectBox[2] = right;

                    int id = citrusFaceSDK.GetResId(i);
                    // TODO Delete after debug
                    Log.i(Settings.TAG, "id is " + id);
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
                        gender = "female";
                    }
                    else{
                        if(genderMark == 1) {
                            gender = "male";
                        }
                    }

                    String result;

                    // userProfile = readNames(nameFilePath);
                    // TODO Delete after debug
                    Log.i(Settings.TAG, "user profile is " + userProfile.toString());
                    Log.i(Settings.TAG, "id is " + id);
                    int timeNow = (int)(System.currentTimeMillis() / 1000);
                    if (id >= 0) {
                        if(userProfile == null){

//                            if((timeNow - time) > 4){
//                                time = timeNow;
//                                // speeker.speek("滴");
//                            }
                            result = isNewOne + "[" + trackId + "]:id" + id + "-s:" + score + "-[" + (int) ((rectBox[2] - rectBox[0]) * CameraOld.getInstance().getPreviewSize().width) + "x" + (int) ((rectBox[3] - rectBox[1]) * CameraOld.getInstance().getPreviewSize().height) + "]";
                        }
                        else {
                            if((timeNow - time) > 0.5){
                                time = timeNow;
                                speeker.speek(userProfile.get(id / 2));
                            }
                            result = trackId + ":" + userProfile.get(id / 2);
                        }
                    }
                    else {
//                        if((timeNow - time) > 1.5){
//                            time = timeNow;
//                            // speeker.speek("滴");
//                        }
                        result = isNewOne + "[" + trackId + "]:[" + gender + "," + child + "," + age + "]-s:" + score + "-[" + (int) ((rectBox[2] - rectBox[0]) * CameraOld.getInstance().getPreviewSize().width) + "x" + (int) ((rectBox[3] - rectBox[1]) * CameraOld.getInstance().getPreviewSize().height) + "]";
                    }

                    rectView.drawRect(rectBox, result);
                }
            }
        }
        rectView.releaseCanvas();
    }

    public void destroySDK(){
        if(citrusFaceSDK != null){
            citrusFaceSDK.Destroy();
            appContext = null;
            activity = null;
        }
    }

    public void reset(){
        citrusFaceSDK.Reset();
    }

    public String getNameFilePath() {
        return nameFilePath;
    }

    public List<String> readNames(String nameFilePath){

        List<String> list = new ArrayList<>();
        BufferedReader reader = null;
        File nameFile = null;
        FileReader fileReader = null;
        // TODO Delete below after debug
        Log.i(Settings.TAG, "name file path is " + nameFilePath);
        nameFile = new File(nameFilePath);


        if(nameFile.exists()){
            try{
                fileReader = new FileReader(nameFile);
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            }

            if(fileReader == null){
                return list;
            }
            reader = new BufferedReader(fileReader);
        }
        else{
            return list;
        }
        String line;

        while(true) {
            try{
                line = reader.readLine();
                if(line == null){
                    break;
                }
                list.add(line);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        try{
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return list;

    }

    public void detectWithImage(Bitmap bitmap, int pass){
        citrusFaceSDK.Reset();
        citrusFaceSDK.ImgSetSize(bitmap.getWidth(), bitmap.getHeight(), pass);
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getRowBytes() * bitmap.getHeight());
        bitmap.copyPixelsToBuffer(buffer);
        citrusFaceSDK.SetImage(buffer.array(), pass);
        citrusFaceSDK.FaceDetect();
    }

    public void writeNames(String nameFilePath, List<String>nameList) throws BlackCatException{
        File nameFile = new File(nameFilePath);
        // TODO Delete after debug
        if(nameFile.exists()){
            Log.i(Settings.TAG, "name list 0:" + nameList.get(0));
        }
        if(!nameFile.exists()) {

            if (!nameFile.getParentFile().exists()) {
                if (!nameFile.getParentFile().mkdirs()) {
                    throw new BlackCatException("create name file parent failed.");
                }
            }

            try {
                if (nameFile.setReadable(true) && nameFile.setWritable(true)) {
                    //TODO Delete after debug
                    Log.i(Settings.TAG, "create file.");
                    if (!nameFile.createNewFile()) {
                        Log.i(Settings.TAG, "create file failed.");
                        throw new BlackCatException("Create new file failed.");
                    }
                }
            } catch (IOException e) {
                throw new BlackCatException("Create new file failed.");
            }
        }
        try{
            FileWriter fileWriter = new FileWriter(nameFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (int i = 0; i < nameList.size(); ++i) {
                bufferedWriter.write((String)(nameList.get(i)));
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
            fileWriter.close();

        }catch (IOException e){
            throw new BlackCatException("Create file writer failed.");
        }

    }

    public int addToDB(int id){
        return citrusFaceSDK.DBAdd(id);
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
