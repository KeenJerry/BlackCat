package com.android.keenjackdaw.blackcat.utils;

public class Picture {
    private String pictureId;
    private boolean isSelected = false;
    private String picturePath;
    private PictureBucket pictureBucket;
    private String thumbnail;
    private String pictureName;

    public String getPictureId() {
        return pictureId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public PictureBucket getPictureBucket() {
        return pictureBucket;
    }

    public Picture(String pictureId, String picturePath, PictureBucket pictureBucket, String thumbnail, String pictureName) {
        this.pictureId = pictureId;
        this.picturePath = picturePath;
        this.pictureBucket = pictureBucket;
        this.thumbnail = thumbnail;
        this.pictureName = pictureName;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
