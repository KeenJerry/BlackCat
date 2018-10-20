package com.android.keenjackdaw.blackcat.controller;

import android.content.Context;
import android.os.Environment;

import com.android.keenjackdaw.blackcat.Settings;
import com.android.keenjackdaw.blackcat.BlackCatApplication;
import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.activity.CameraActivity;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.ui.CameraView;
import com.rokid.citrus.citrusfacesdk.CitrusFaceSDK;

import java.util.Arrays;

public class CitrusFaceManager {
    private CitrusFaceManager() {}
    // FIXME It's not an error!!!!!
    private static CitrusFaceManager instance = new CitrusFaceManager();
    private byte[][] byteBuffers = null;
    private CameraActivity cameraActivity = null;
    private Context appContext = null;
    private CitrusFaceSDK citrusFaceSDK = null;

    public static CitrusFaceManager getInstance() {
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

        citrusFaceSDK.SetSizeROIWithBuffer(
                cameraView.getWidth(),
                cameraView.getHeight(),
                Settings.imageChannelNum,
                Settings.frameNumInCache,
                byteBuffers,
                (int)Settings.roiX * cameraView.getWidth(),
                (int)Settings.roiY * cameraView.getHeight(),
                (int)Settings.roiW * cameraView.getWidth(),
                (int)Settings.roiH * cameraView.getHeight(),
                Settings.scale);

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
        return citrusFaceSDK.Authenic(Settings.key, Settings.secret, Settings.deviceId, Settings.deviceTypeId);
    }

    private void setBuffer(int width, int height){
        byteBuffers = new byte[Settings.imageChannelNum][];

        for(int i = 0; i < Settings.imageChannelNum; i++){
            byteBuffers[i] = new byte[width * height * 3 / 2];
        }
        for (int i = 0; i < Settings.imageChannelNum; i++) {
            Arrays.fill(byteBuffers[i], (byte) 0);
        }
    }

    public void startFaceDetection(){

    }

}
