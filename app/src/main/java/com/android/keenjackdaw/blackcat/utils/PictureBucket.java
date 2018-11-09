package com.android.keenjackdaw.blackcat.utils;

import java.util.List;

public class PictureBucket {
    private String bucketId = null;
    private String bucketName = null;
    private List<Picture> pictureList = null;
    private List<String> selectedPictureList = null;

    public PictureBucket(String bucketId, String bucketName) {
        this.bucketId = bucketId;
        this.bucketName = bucketName;
        this.pictureList = pictureList;
    }

    public String getBucketName() {
        return bucketName;
    }

    public List<Picture> getPictureList() {
        return pictureList;
    }

    public List<String> getSelectedPictureList() {
        return selectedPictureList;
    }

    public void AddToPictureList(Picture picture){
        pictureList.add(picture);
    }

    public void setPictureList(List<Picture> pictureList) {
        this.pictureList = pictureList;
    }
}
