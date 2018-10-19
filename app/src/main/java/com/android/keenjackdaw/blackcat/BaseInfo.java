package com.android.keenjackdaw.blackcat;

public class BaseInfo {
    private BaseInfo(){}

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
}
