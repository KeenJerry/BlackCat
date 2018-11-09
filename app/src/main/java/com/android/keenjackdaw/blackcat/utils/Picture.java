package com.android.keenjackdaw.blackcat.utils;

public class Picture {
    private String pictureId = null;
    private boolean isSelected = false;
    private String picturePath = null;
    private PictureBucket pictureBucket = null;

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

    public Picture(String pictureId, String picturePath, PictureBucket pictureBucket) {
        this.pictureId = pictureId;
        this.picturePath = picturePath;
        this.pictureBucket = pictureBucket;
    }
}
