package com.android.keenjackdaw.blackcat.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class CameraView extends SurfaceView {
    public CameraView(Context context) {
        super(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
