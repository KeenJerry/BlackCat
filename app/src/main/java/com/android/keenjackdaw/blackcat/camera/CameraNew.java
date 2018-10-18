package com.android.keenjackdaw.blackcat.camera;

import android.hardware.camera2.CameraManager;

public class CameraNew {
    private CameraNew(){ }

    private static CameraNew instance = new CameraNew();
    private CameraManager cameraManager = null;

    public CameraNew getInstance() {
        if(instance == null){
            instance = new CameraNew();
            return instance;
        }
        return instance;
    }


}
