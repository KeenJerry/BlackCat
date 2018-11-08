package com.android.keenjackdaw.blackcat.controller;

import android.support.annotation.NonNull;
import java.io.File;

public class FacePictureManager {
    public FacePictureManager() {

    }

    private static FacePictureManager instance = null;

    public static FacePictureManager getInstance(){
        if(instance == null){
            return new FacePictureManager();
        }
        return instance;
    }

    public void uploadToSDcard(){

    }

    public void openCameraLib(){

    }

    public void closeCameraLib(){

    }
}
