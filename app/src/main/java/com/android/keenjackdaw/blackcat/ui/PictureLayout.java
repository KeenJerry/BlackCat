package com.android.keenjackdaw.blackcat.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.keenjackdaw.blackcat.R;

public class PictureLayout extends RelativeLayout {

    private ImageView pictureView = null;
    private ImageView iconView = null;
    private String id = null;

    public PictureLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public PictureLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PictureLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr){
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PictureLayout, defStyleAttr, 0);
        a.recycle();
    }

    public void setPictureView(ImageView pictureView) {
        this.pictureView = pictureView;
    }

    public void setIconView(ImageView iconView) {
        this.iconView = iconView;
    }

    public void setId(String id) {
        this.id = id;
    }
}
