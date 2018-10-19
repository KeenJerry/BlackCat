package com.android.keenjackdaw.blackcat;

public class BaseInfo {
    private BaseInfo(){}

    public final static int standardDensity = 160;

    public enum ExternalStorageState{
        All_ALLOWED,
        ONLY_READ,
        NEITHER_ALLOWED
    }
}
