package com.android.keenjackdaw.blackcat.controller;

import android.content.Context;
import android.os.Environment;

import com.android.keenjackdaw.blackcat.BaseInfo;
import com.android.keenjackdaw.blackcat.BlackCatApplication;
import com.android.keenjackdaw.blackcat.activity.CameraActivity;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.rokid.citrus.citrusfacesdk.CitrusFaceSDK;

public class CitrusFaceManager {
    private CitrusFaceManager() {}

    private static CitrusFaceManager instance = new CitrusFaceManager();
    private CameraActivity cameraActivity = null;
    private Context appContext = null;

    private CitrusFaceSDK citrusFaceSDK = null;

    public void setUpAppInfo(){
        cameraActivity = (CameraActivity) BlackCatApplication.getCurrentActivity().get();
        appContext = cameraActivity.getApplicationContext();

    }

    public void initCitrusFaceSDK() throws BlackCatException{
        citrusFaceSDK.Init(appContext);

        String dbPath = Environment.getExternalStorageDirectory().getPath() + "face.db";
        BaseInfo.ExternalStorageState state = checkExternalStorage(dbPath);
        if(state != BaseInfo.ExternalStorageState.All_ALLOWED){
            throw new BlackCatException("External storage access not allowed or only allow read.");
        }
        citrusFaceSDK.DBSet(Environment.getExternalStorageDirectory().getPath() + "face.db");

    }

    private BaseInfo.ExternalStorageState checkExternalStorage(String path){
        String state = Environment.getExternalStorageState();

        switch (state){
            case Environment.MEDIA_MOUNTED:
                return BaseInfo.ExternalStorageState.All_ALLOWED;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                return BaseInfo.ExternalStorageState.ONLY_READ;
            default:
                return BaseInfo.ExternalStorageState.NEITHER_ALLOWED;
        }
    }

}
