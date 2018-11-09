package com.android.keenjackdaw.blackcat.utils;

import java.util.List;

public class PictureBucket {
    private String bucketName = null;
    private List<String> pictureList = null;
    private List<String> selectedPictureList = null;

    public String getBucketName() {
        return bucketName;
    }

    public List<String> getPictureList() {
        return pictureList;
    }

    public List<String> getSelectedPictureList() {
        return selectedPictureList;
    }
}
