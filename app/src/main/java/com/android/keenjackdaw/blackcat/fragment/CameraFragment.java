package com.android.keenjackdaw.blackcat.fragment;

import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.android.keenjackdaw.blackcat.controller.CameraNew;
import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.controller.CitrusFaceManager;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.ui.CameraView;
import com.android.keenjackdaw.blackcat.ui.RectView;

public class CameraFragment extends Fragment {

    CameraNew cameraNew = CameraNew.getInstance();
    CameraView cameraView = null;
    RectView rectView = null;
    CitrusFaceManager citrusFaceManager = null;
    Canvas canvas = null;
    Handler cameraHandler = null;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
       View v = inflater.inflate(R.layout.fragment_camera, container, false);

       citrusFaceManager = CitrusFaceManager.getInstance();

       cameraView = v.findViewById(R.id.camera_view);
       rectView = v.findViewById(R.id.rect_view);
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

               citrusFaceManager.setUpAppInfo();
               try{
                   citrusFaceManager.initCitrusFaceSDK();
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
               citrusFaceManager.doFaceDetection();

           }
       });
       return v;
    }

}
