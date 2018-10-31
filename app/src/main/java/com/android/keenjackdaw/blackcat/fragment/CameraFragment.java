package com.android.keenjackdaw.blackcat.fragment;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.android.keenjackdaw.blackcat.Settings;
import com.android.keenjackdaw.blackcat.controller.CameraNew;
import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.controller.CameraOld;
import com.android.keenjackdaw.blackcat.controller.CitrusFaceManager;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.ui.Camera2View;
import com.android.keenjackdaw.blackcat.ui.CameraView;
import com.android.keenjackdaw.blackcat.ui.RectView;
import com.android.keenjackdaw.blackcat.utils.BlackCatRunnable;

public class CameraFragment extends Fragment {

    CameraNew cameraNew = CameraNew.getInstance();
    CameraOld cameraOld = CameraOld.getInstance();

    Camera2View camera2View = null;
    CameraView cameraView = null;
    RectView rectView = null;
    CitrusFaceManager citrusFaceManager = null;

    private BlackCatRunnable detectionRunnable = null;
    private BlackCatRunnable recognitionRunnable = null;

    private int faceNumUnrecognized = 0;
    // Handler cameraHandler = null;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v;
        citrusFaceManager = CitrusFaceManager.getInstance();

        if(Settings.IS_USING_CAMERA2){
            v = inflater.inflate(R.layout.fragment_camera2, container, false);
            camera2View = v.findViewById(R.id.camera2_view);
            camera2View.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
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
                        new Thread(recognitionRunnable).start();
                        // TODO Uncomment after camera2 is fully supported.
                        // new Thread(recognitionRunnable).start();
                    }

                }
            });
        }
        else{
            v = inflater.inflate(R.layout.fragment_camera, container, false);
            cameraView = v.findViewById(R.id.camera_view);
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    cameraOld.setUpAppInfo();
                    try {
                        cameraOld.initCamera();
                        cameraOld.openBackCamera();
                    } catch (BlackCatException e) {
                        e.printStackTrace();
                    }

                    citrusFaceManager.setUpAppInfo();
                    try {
                        citrusFaceManager.initCitrusFaceSDK(cameraOld.getPreviewSize().width, cameraOld.getPreviewSize().height);
                    } catch (BlackCatException e) {
                        e.printStackTrace();
                    }

                    Camera.PreviewCallback callback = new Camera.PreviewCallback() {
                        @Override
                        public void onPreviewFrame(byte[] data, Camera camera) {

                            if(camera != null){

                                if(!detectionRunnable.isRunning() && !recognitionRunnable.isRunning()){
                                    detectionRunnable.setRunning(true);
                                    recognitionRunnable.setRunning(true);
                                    new Thread(detectionRunnable).start();
                                }

                                citrusFaceManager.doFaceTrack();

                                camera.addCallbackBuffer(data);
                            }
                        }
                    };

                    cameraOld.setPreviewCallback(callback);
                    cameraOld.startPreview(citrusFaceManager.getByteBuffers(), holder);
                    try{
                        cameraOld.startFaceDetection();
                    }catch (BlackCatException e){
                        e.printStackTrace();
                    }

                    setDetectionRunnable();
                    setRecognitionRunnable();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraOld.stopPreview();
                }
            });
        }
        rectView = v.findViewById(R.id.rect_view);

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
                        Log.i(Settings.TAG, "in onPreviewFrame.");
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
                        for(int i = 0; i < faceNumUnrecognized; i++){
                            Pair<Integer, Integer> result = citrusFaceManager.doFaceRecognition(i);
                            if (result.first == 0) {
                                Log.i(Settings.TAG, "Recog " + i + ", id = " + result.second);
                            }
                        }
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
