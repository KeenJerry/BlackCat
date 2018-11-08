package com.android.keenjackdaw.blackcat.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.fragment.CameraFragment;

public class DataCenterActivity extends AppCompatActivity {

    Fragment fragmentContainer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_center);

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentContainer = fragmentManager.findFragmentById(R.id.camera_fragment_container);
        if(fragmentContainer == null){
            fragmentContainer = new CameraFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragmentContainer)
                    .commit();
        }
    }


}
