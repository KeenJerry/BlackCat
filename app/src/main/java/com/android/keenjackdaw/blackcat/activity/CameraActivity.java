package com.android.keenjackdaw.blackcat.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.fragment.CameraFragment;

public class CameraActivity extends AppCompatActivity {

    private final String activityTag = "Camera Activity";
    Fragment fragmentContainer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentContainer = fragmentManager.findFragmentById(R.id.camera_fragment_container);
        if(fragmentContainer == null){
            fragmentContainer = new CameraFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.camera_fragment_container, fragmentContainer)
                    .commit();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public Fragment getFragmentContainer() {
        return fragmentContainer;
    }
}
