package com.android.keenjackdaw.blackcat.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class RectView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    public RectView(Context context) {
        super(context);
    }

    public RectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) { //
        //TODO Complete definition.
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        //TODO Complete definition.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //TODO Complete definition.
    }

    @Override
    public void run() {
        //TODO Complete definition.
    }
}
