package com.android.keenjackdaw.blackcat.camera;

public class CameraNew {
    private CameraNew(){ }

    private static CameraNew instance = new CameraNew();

    public CameraNew getInstance() {
        if(instance == null){
            instance = new CameraNew();
            return instance;
        }
        return instance;
    }

}
