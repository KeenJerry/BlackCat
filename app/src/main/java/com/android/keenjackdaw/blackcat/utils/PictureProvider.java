package com.android.keenjackdaw.blackcat.utils;

import android.content.Context;
import android.database.Cursor;

import com.android.keenjackdaw.blackcat.Settings;

public class PictureProvider {
    private static Cursor cursor = null;

    private static void getCurosr(Context context){
      cursor = context.getContentResolver().query(Settings.QUERY_URI, Settings.PROJECTION, Settings.SELECTION, Settings.SELECTION_ARGS, Settings.QUERY_ORDER);
    }

    public static void getPictureData(){}
}
