package com.android.keenjackdaw.blackcat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.keenjackdaw.blackcat.ui.CameraView;

public class MainActivity extends AppCompatActivity {

    CameraView cameraView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        cameraView = findViewById(R.id.camera_view);

    }
}
