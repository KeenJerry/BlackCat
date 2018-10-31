package com.android.keenjackdaw.blackcat.controller;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;

import com.android.keenjackdaw.blackcat.BlackCatApplication;
import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.Settings;
import com.android.keenjackdaw.blackcat.activity.CameraActivity;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.ui.CameraView;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraOld {
    private CameraOld() { }

    private CameraActivity cameraActivity = null;
    private Context appContext = null;
    // FIXME It's not an error!!!!!
    private static CameraOld instance = new CameraOld();

    // FIXME I know it is deprecated, but I have to use this
    private Camera camera = null;
    private Camera.Parameters cameraParam = null;
    private Camera.Size previewSize = null;
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

    public void openBackCamera() throws BlackCatException{
        camera = Camera.open(backCameraId);
        if(camera == null){
            throw new BlackCatException("Front camera not available");
        }
        setCameraParam();
    }

    public void setPreviewCallback(Camera.PreviewCallback callback) {
        camera.setPreviewCallbackWithBuffer(callback);
    }

    private void setCameraParam(){
        cameraParam = camera.getParameters();
        cameraParam.setPictureFormat(PixelFormat.JPEG);
        Size suitablePreviewSize = getSuitablePreviewSize();
        cameraParam.setPreviewSize(suitablePreviewSize.width, suitablePreviewSize.height);
        previewSize =suitablePreviewSize;

        List<String> availableFocusMode = cameraParam.getSupportedFocusModes();
        if(availableFocusMode.contains("continuous-video")){
            cameraParam.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        else{
            cameraParam.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        cameraParam.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
        camera.setParameters(cameraParam);
        camera.setDisplayOrientation(90);
    }

    public void startPreview(byte[][] buffer, SurfaceHolder holder){
        try{
            camera.setPreviewDisplay(holder);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        int bufferNum = buffer.length;
        for(int i = 0; i < bufferNum; i++){
            camera.addCallbackBuffer(buffer[i]);
        }

        camera.startPreview();
    }

    public void stopPreview(){
        camera.stopPreview();
    }

    public void startFaceDetection() throws BlackCatException{
        if(cameraParam.getMaxNumDetectedFaces() <= 0){
            throw new BlackCatException("Face detection not supported.");
        }
        else
        {
            camera.startFaceDetection();
        }
    }

    public void closeCamera(){
        // TODO Complete definition
    }

    public Size getPreviewSize() {
        return previewSize;
    }

    private Size getSuitablePreviewSize(){
        List<Size> supportedPreviewSizes = cameraParam.getSupportedPreviewSizes();
        Collections.sort(supportedPreviewSizes, new CameraOldComparator());
        int i = 0;
        // FIXME It's not an error too... Because CameraView has already initialized before calling findViewBtId
        CameraView cameraView = cameraActivity.getFragmentContainer().getView().findViewById(R.id.camera_view);
        for(Size s: supportedPreviewSizes){
            if(s.width >= cameraView.getHeight() && isInTolerance(s, Settings.ASPECT_RATIO)){
                break;
            }
            i++;
        }

        if(i == supportedPreviewSizes.size()){
            i = 0;
            for(Size s: supportedPreviewSizes){
                if(s.height >= cameraView.getHeight()){
                    break;
                }
                i++;
            }
        }
        Log.i(Settings.TAG, "preview size width:" + supportedPreviewSizes.get(i).width + " height:" + supportedPreviewSizes.get(i).height);
        return supportedPreviewSizes.get(i);
    }

    private boolean isInTolerance(@NonNull Size size, float aspectRatio){
        float r = (float)size.height/ (float)size.width;
        return Math.abs(r - aspectRatio) <= Settings.ASPECT_TOLERANCE;
    }

    private class CameraOldComparator implements Comparator<Camera.Size> {
        @Override
        public int compare(Camera.Size o1, Camera.Size o2) {
            return Integer.compare(o1.width, o2.width);
        }
    }

    public int getBackCameraId() {
        return backCameraId;
    }
}
