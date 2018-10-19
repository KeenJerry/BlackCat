package com.android.keenjackdaw.blackcat.controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Surface;

import com.android.keenjackdaw.blackcat.BlackCatApplication;
import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.activity.CameraActivity;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.ui.CameraView;

import java.util.Collections;

public class CameraNew {
    private CameraNew(){ }

    private CameraActivity cameraActivity = null;
    private Context appContext = null;
    // FIXME It's not an error!!!!!
    private static CameraNew instance = new CameraNew();
    private CameraManager cameraManager = null;
    private String[] cameraIds = null;
    private CameraCharacteristics cameraCharacteristics = null;
    private CameraDevice cameraDevice = null;
    private CameraDevice.StateCallback callback = null;
    private String currentCameraId = null;
    private final static int CAMERA_REQUEST = 10086;
    private CaptureRequest.Builder previewRequestBuilder = null;

    public static CameraNew getInstance() {
        if(instance == null){
            instance = new CameraNew();
            return instance;
        }
        return instance;
    }

    public void setUpAppInfo(){
        cameraActivity = (CameraActivity) BlackCatApplication.getCurrentActivity().get();
        appContext = cameraActivity.getApplicationContext();

    }

    public void initCamera() throws BlackCatException{

        cameraManager = (CameraManager) appContext.getSystemService(Context.CAMERA_SERVICE);
        if(cameraManager == null){
            throw new BlackCatException("Init failed, camera hardware not supported.");
        }
        try{
            cameraIds = cameraManager.getCameraIdList();
            if(cameraIds.length == 0){
                throw new BlackCatException("Init failed, no camera available.");
            }
        }
        catch (CameraAccessException e){
            e.printStackTrace();
            throw new BlackCatException("Init failed, camera access failed.");
        }

        getAvailableCameraInfo();

        setCallback();

        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(cameraActivity, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        }
        else {
            try{
                cameraManager.openCamera(currentCameraId, callback, null);
            }
            catch (CameraAccessException e){
                throw new BlackCatException("Init failed, camera access failed.");
            }
        }

    }

    private void startPreview(){
        try{
            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            CameraView cameraView = cameraActivity.getFragmentContainer().getView().findViewById(R.id.camera_view);
            SurfaceTexture surfaceTexture = cameraView.getSurfaceTexture();
            Surface workingSurface = new Surface(surfaceTexture);
            previewRequestBuilder.addTarget(workingSurface);
            cameraDevice.createCaptureSession(Collections.singletonList(workingSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try{
                        session.setRepeatingRequest(previewRequestBuilder.build(), null, null);
                    }
                    catch (CameraAccessException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, null);
        }
        catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    private void getAvailableCameraInfo(){
        for(String id : cameraIds){
            try{
                cameraCharacteristics = cameraManager.getCameraCharacteristics(id);
                currentCameraId = id;
            }
            catch (CameraAccessException e){
                e.printStackTrace();
            }
            if(cameraCharacteristics != null){
                break;
            }
        }
    }

    private void setCallback(){
        callback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                cameraDevice = camera;
                startPreview();
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                camera.close();
                cameraDevice = null;
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                camera.close();
                cameraDevice = null;
            }
        };
    }
}
