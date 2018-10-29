package com.android.keenjackdaw.blackcat.controller;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Size;

import com.android.keenjackdaw.blackcat.BlackCatApplication;
import com.android.keenjackdaw.blackcat.Settings;
import com.android.keenjackdaw.blackcat.activity.CameraActivity;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.utils.CameraOldUtil;

import java.util.List;

public class CameraOld {
    private CameraOld() { }

    private CameraActivity cameraActivity = null;
    private Context appContext = null;
    private Size suitablePreviewSize = null;
    // FIXME It's not an error!!!!!
    private static CameraOld instance = new CameraOld();

    // FIXME I know it is deprecated, but I have to use this
    private Camera camera = null;
    private Camera.PreviewCallback previewCallback = null;
    private Camera.Parameters cameraParam = null;
    private int numOfCameras = -1;
    private int backCameraId = 0;
    private int frontCameraId = 0;
    private int backCameraOrientation = 0;
    private int frontCameraOrientation = 0;

    public static CameraOld getInstance(){
        if(instance == null){
            instance = new CameraOld();
        }
        return instance;
    }

    public void setUpAppInfo(){
        cameraActivity = (CameraActivity) BlackCatApplication.getCurrentActivity().get();
        appContext = cameraActivity.getApplicationContext();
    }

    public void initCamera() throws BlackCatException{

        numOfCameras = Camera.getNumberOfCameras();
        if(numOfCameras == Settings.CAMERA_NOT_AVAILABLE){
            throw new BlackCatException("Camera not available");
        }

        for(int i = 0; i < numOfCameras; i++){
            final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

            Camera.getCameraInfo(i, cameraInfo);

            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                backCameraId = i;
                backCameraOrientation = cameraInfo.orientation;
            }

            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                frontCameraId = i;
                frontCameraOrientation = cameraInfo.orientation;
            }
        }
    }

    public void openFrontCamera() throws BlackCatException{
        camera = Camera.open(frontCameraId);
        if(camera == null){
            throw new BlackCatException("Front camera not available");
        }
    }

    public void setPreviewCallback() {

    }

    private void setCameraParam(){
        cameraParam = camera.getParameters();
        cameraParam.setPictureFormat(PixelFormat.JPEG);
        suitablePreviewSize = CameraOldUtil.getSuitableCameraSize();
        cameraParam.setPictureSize(suitablePreviewSize.getWidth(), suitablePreviewSize.getHeight());

        List<String> availableFocusMode = cameraParam.getSupportedFocusModes();
        if(availableFocusMode.contains("continuous-video")){
            cameraParam.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

        camera.setParameters(cameraParam);
    }

    public void startPreview(){

    }

    public void closeCamera(){

    }


}
