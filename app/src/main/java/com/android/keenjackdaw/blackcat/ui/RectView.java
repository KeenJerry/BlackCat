package com.android.keenjackdaw.blackcat.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.android.keenjackdaw.blackcat.Settings;

public class RectView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder = null;
    private Canvas canvas = null;

    private Paint paintFaceRect = null;
    private Paint paintAnchor   = null;

    private Rect faceRect = null;

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
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Complete definition.
        init();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        //TODO Complete definition.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //TODO Complete definition.
    }

    public void init(){
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

        setZOrderOnTop(true);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);

        canvas = surfaceHolder.lockCanvas();

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        paintAnchor = new Paint();
        paintFaceRect = new Paint();

        faceRect = new Rect();

        setPaintParam();

    }

    private void setPaintParam(){
        paintFaceRect.setStyle(Paint.Style.STROKE);
        paintFaceRect.setColor(Color.GREEN);
        paintFaceRect.setStrokeWidth(Settings.FACE_RECT_STROKE_WIDTH);
    }

    public void releaseCanvas(){
        if (canvas != null){
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
}
