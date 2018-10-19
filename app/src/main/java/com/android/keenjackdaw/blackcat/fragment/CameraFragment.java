package com.android.keenjackdaw.blackcat.fragment;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.android.keenjackdaw.blackcat.Controller.CameraNew;
import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.ui.CameraView;

public class CameraFragment extends Fragment {

    CameraNew cameraNew = CameraNew.getInstance();
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
       cameraView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
           @Override
           public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
               cameraNew.setUpAppInfo();
               try {
                   cameraNew.initCamera();
               }
               catch (BlackCatException e){
                   e.printStackTrace();
               }
           }

           @Override
           public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

           }

           @Override
           public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
               return false;
           }

           @Override
           public void onSurfaceTextureUpdated(SurfaceTexture surface) {

           }
       });
       return v;
    }

}
