package com.android.keenjackdaw.blackcat;

public class Settings {
    private Settings(){}

    public enum ExternalStorageState{
        All_ALLOWED,
        ONLY_READ,
        NEITHER_ALLOWED
    }

    public static final String KEY = "rokid_test_key";
    public static final String SECRET = "rokid_test_secret";
    public static final String DEVICE_TYPE_ID = "rokid_test_device_type_id";
    public static final String DEVICE_ID = "rokid_test_device_id";

    public static final int IMAGE_CHANNEL_NUM = 3;
    public static final int FRAME_NUM_IN_CACHE = 3;

    public static final float ROI_X = 0.0f;
    public static final float ROI_Y = 0.0f;
    public static final float ROI_H = 1.0f;
    public static final float ROI_W = 1.0f;
    public static final float SCALE = 1.0f;

    public static final int FACE_RECT_STROKE_WIDTH = 5;
    public static final String TAG = "BlackCatTAG";
    public static final Boolean IS_USING_CAMERA2 = false;

    public static final int CAMERA_REQUEST = 10086;
    public static final int CAMERA_NOT_AVAILABLE = -1;
    public static final int PREVIEW_FPS_MIN = 60;
    public static final int PREVIEW_FPS_MAX = 90;
    public static final float ASPECT_TOLERANCE = 0.2f;
    public static final float ASPECT_RATIO = 0.75f;
}
