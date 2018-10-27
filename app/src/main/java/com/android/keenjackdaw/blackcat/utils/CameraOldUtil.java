package com.android.keenjackdaw.blackcat.utils;

import android.util.Size;

import java.util.Comparator;
import android.hardware.Camera;


public class CameraOldUtil {
    public static Size getSuitableCameraSize(){
        // TODO Complete definition
        return new Size(1, 1);
    }

    private class CameraOldComparator implements Comparator<Camera.Size>{
        @Override
        public int compare(Camera.Size o1, Camera.Size o2) {
            return 0;
        }
    }
}
