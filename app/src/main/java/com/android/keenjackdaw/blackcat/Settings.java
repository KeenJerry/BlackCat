package com.android.keenjackdaw.blackcat;

public class Settings {
    private Settings(){}

    public final static int standardDensity = 160;

    public enum ExternalStorageState{
        All_ALLOWED,
        ONLY_READ,
        NEITHER_ALLOWED
    }

    public static final String key = "rokid_test_key";
    public static final String secret = "rokid_test_secret";
    public static final String deviceTypeId = "rokid_test_device_type_id";
    public static final String deviceId = "rokid_test_device_id";

    public static final int imageChannelNum = 3;
    public static final int frameNumInCache = 3;

    public static final float roiX = 0.0f;
    public static final float roiY = 0.0f;
    public static final float roiH = 1.0f;
    public static final float roiW = 1.0f;
    public static final float scale = 1.0f;
}
