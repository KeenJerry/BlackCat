package com.android.keenjackdaw.blackcat.activity;

import android.nfc.Tag;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.fragment.CameraFragment;
import com.android.keenjackdaw.blackcat.ui.CameraView;

public class CameraActivity extends AppCompatActivity {

    private final String activityTag = "Camera Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragmentContainer = fragmentManager.findFragmentById(R.id.fragment_container);
        if(fragmentContainer == null){
            fragmentContainer = new CameraFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragmentContainer)
                    .commit();
        }

    }



}
