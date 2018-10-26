package com.android.keenjackdaw.blackcat.controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Size;

import com.android.keenjackdaw.blackcat.activity.CameraActivity;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.Settings;

public class CameraOld {
    public CameraOld() { }

    private CameraActivity cameraActivity = null;
    private Context appContext = null;
    private Size suitablePreviewSize = null;
    // FIXME It's not an error!!!!!
    private CameraOld instance = new CameraOld();

    // FIXME I know it is deprecated, but I have to use this
    private Camera camera = null;
    private Camera.PreviewCallback previewCallback = null;
    private int numOfCameras = -1;

    public CameraOld getInstance(){
        if(instance == null){
            instance = new CameraOld();
        }
        return instance;
    }

    public void setPreviewCallback() {

    }

    public void openFrontCamera(int frontCameraId) throws BlackCatException{
        camera = Camera.open(frontCameraId);
        if(camera == null){
            throw new BlackCatException("Open camera failed.");
        }
    }

    public void startPreview(){

    }

    public void closeCamera(){

    }


}
