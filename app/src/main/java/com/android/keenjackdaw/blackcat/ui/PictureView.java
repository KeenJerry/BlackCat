package com.android.keenjackdaw.blackcat.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.android.keenjackdaw.blackcat.R;

public class PictureView extends View {

    private Drawable activatedIcon = null;
    private Drawable inactivatedIcon = null;
    private String pictureUrl = null;
    private Bitmap picture = null;

    public PictureView(Context context) {
        super(context);
        init(null, 0);
    }

    public PictureView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PictureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr){
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PictureView, defStyleAttr, 0);
        activatedIcon = a.getDrawable(R.styleable.PictureView_activated_icon);
        inactivatedIcon = a.getDrawable(R.styleable.PictureView_unactivated_icon);
        a.recycle();
    }

    public void setActivatedIcon(Drawable activatedIcon) {
        this.activatedIcon = activatedIcon;
    }

    public void setUnactivatedIcon(Drawable unactivatedIcon) {
        this.inactivatedIcon = unactivatedIcon;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
        
    }

    public void show(){
        // TODO Complete
    }
}
