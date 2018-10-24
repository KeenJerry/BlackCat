package com.android.keenjackdaw.blackcat.fragment;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.android.keenjackdaw.blackcat.Settings;
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

               rectView.init();

               citrusFaceManager.setUpAppInfo();

               try{
                   citrusFaceManager.initCitrusFaceSDK();
               }
               catch (BlackCatException e){
                   e.printStackTrace();
               }

               setDetectionRunnable();
               setRecognitionRunnable();

           }

           @Override
           public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

           }

           @Override
           public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
               detectionRunnable.setRunning(false);
               recognitionRunnable.setRunning(false);
               return false;
           }

           @Override
           public void onSurfaceTextureUpdated(SurfaceTexture surface) {
               citrusFaceManager.doFaceTrack();
               if(!detectionRunnable.isRunning() && !recognitionRunnable.isRunning()){
                   detectionRunnable.setRunning(true);
                   recognitionRunnable.setRunning(true);
                   new Thread(detectionRunnable).start();
                   // TODO Uncomment after completed.
                   // new Thread(recognitionRunnable).start();
               }

               // TODO Delete below after debug
               // Log.i(Settings.TAG, Arrays.toString(citrusFaceManager.getByteBuffers()[0]));

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
                        // TEST
                        // Thread.sleep(2000);

                        setCurrentTime(System.currentTimeMillis());
                        int faceNum = citrusFaceManager.getFaceNum();
                        // TODO Delete below after debug
                        Log.i(Settings.TAG,  "face detected:" + faceNum);
                        setCurrentTime(System.currentTimeMillis() - getCurrentTime());
                        if (faceNum == 5){
                            while(citrusFaceManager.getResFaceNum() == 5){
                                Thread.sleep(1000);
                            }
                        }
                        else{
                            Thread.sleep(500);
                        }
                        // TODO Delete below after debug
                        Log.i(Settings.TAG, "buffer changed:");
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
                while(isRunning()){
                    try{

                        // Thread.sleep(2000);
                        // TODO Delete the above code before implementing any further, it's a trick to make it correct.
                        throw new InterruptedException();
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };
    }

}
