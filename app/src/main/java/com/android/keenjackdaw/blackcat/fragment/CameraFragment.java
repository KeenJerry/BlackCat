package com.android.keenjackdaw.blackcat.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.ui.CameraView;

public class CameraFragment extends Fragment {

    CameraView cameraView = null;
    Handler cameraHandler = null;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
       View v = inflater.inflate(R.layout.fragment_camera, container, false);

       cameraView = v.findViewById(R.id.camera_view);

       return v;
    }

}
