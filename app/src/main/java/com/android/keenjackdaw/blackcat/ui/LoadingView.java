package com.android.keenjackdaw.blackcat.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

import com.android.keenjackdaw.blackcat.R;

public class LoadingView extends ConstraintLayout{
    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);

        
        a.recycle();
    }

    public void StartLoaiding(){

    }
    // TODO Complete LoadingView definition.
}
