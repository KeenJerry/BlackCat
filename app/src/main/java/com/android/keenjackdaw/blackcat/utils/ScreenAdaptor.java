package com.android.keenjackdaw.blackcat.utils;

import android.content.Context;

public class ScreenAdaptor {
    private ScreenAdaptor(){}

    public static int px2dp(Context context, int px){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale);
    }

    public static int dp2px(Context context, int dp){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale);
    }


}
