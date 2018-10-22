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
import com.android.keenjackdaw.blackcat.utils.BlackCatRunnable;

public class CameraFragment extends Fragment {

    CameraNew cameraNew = CameraNew.getInstance();
    CameraView cameraView = null;
    RectView rectView = null;
    CitrusFaceManager citrusFaceManager = null;

    private BlackCatRunnable detectionRunnable = null;
    private BlackCatRunnable recognitionRunnable = null;
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

               setDetectionRunnable();
               setRecognitionRunnable();
               detectionRunnable.setRunning(true);
               recognitionRunnable.setRunning(true);
               new Thread(detectionRunnable).start();
               new Thread(recognitionRunnable).start();
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
               citrusFaceManager.doFaceTrack();

           }
       });

        return v;
    }

    public void setDetectionRunnable(){
        detectionRunnable = new BlackCatRunnable() {
            @Override
            protected void blackCatRun() {

                while (isRunning()){
                    try{
                        setCurrentTime(System.currentTimeMillis());
                        int faceNum = citrusFaceManager.getFaceNum();
                        setCurrentTime(System.currentTimeMillis() - getCurrentTime());
                        if (faceNum == 5){
                            while(citrusFaceManager.getResFaceNum() == 5){
                                Thread.sleep(1000);
                            }
                        }
                        else{
                            Thread.sleep(500);
                        }
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void setRecognitionRunnable(){
        recognitionRunnable = new BlackCatRunnable() {
            @Override
            protected void blackCatRun() {

            }
        };
    }

}
