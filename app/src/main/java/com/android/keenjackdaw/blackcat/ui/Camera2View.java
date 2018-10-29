package com.android.keenjackdaw.blackcat.ui;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;

public class Camera2View extends TextureView {
    public Camera2View(Context context) {
        super(context);
    }

    public Camera2View(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Camera2View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return super.getSurfaceTexture();
    }
}
