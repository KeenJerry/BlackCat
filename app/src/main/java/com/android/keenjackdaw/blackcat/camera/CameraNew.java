package com.android.keenjackdaw.blackcat.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.support.annotation.NonNull;

import com.android.keenjackdaw.blackcat.exception.BlackCatException;

public class CameraNew {
    private CameraNew(){ }

    private static CameraNew instance = new CameraNew();
    private static CameraManager cameraManager = null;
    private static String[] cameraIds = null;
    private static CameraCharacteristics cameraCharacteristics = null;
    private static CameraDevice cameraDevice = null;
    private static CameraDevice.StateCallback callback = null;


    public CameraNew getInstance() {
        if(instance == null){
            instance = new CameraNew();
            return instance;
        }
        return instance;
    }

    public static void init(Activity activity) throws BlackCatException{
        cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        if(cameraManager == null){
            throw new BlackCatException("Init failed, camera not supported.");
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
    }

    public static void startPreview(){

    }

    private static void getAvailableCameraInfo(){
        for(String id : cameraIds){
            try{
                cameraCharacteristics = cameraManager.getCameraCharacteristics(id);

            }
            catch (CameraAccessException e){
                e.printStackTrace();
            }
        }
    }

    private static void setCallback(){
        callback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                cameraDevice = camera;
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                camera.close();
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                camera.close();
            }
        };
    }
}
