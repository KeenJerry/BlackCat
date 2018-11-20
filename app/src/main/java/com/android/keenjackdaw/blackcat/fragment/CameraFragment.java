package com.android.keenjackdaw.blackcat.fragment;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.keenjackdaw.blackcat.Settings;
import com.android.keenjackdaw.blackcat.activity.DataCenterActivity;
import com.android.keenjackdaw.blackcat.controller.CameraNew;
import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.controller.CameraOld;
import com.android.keenjackdaw.blackcat.controller.CitrusFaceManager;
import com.android.keenjackdaw.blackcat.controller.Speeker;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.ui.Camera2View;
import com.android.keenjackdaw.blackcat.ui.CameraView;
import com.android.keenjackdaw.blackcat.ui.RectView;
import com.android.keenjackdaw.blackcat.utils.BlackCatRunnable;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;

public class CameraFragment extends Fragment {

    CameraNew cameraNew = CameraNew.getInstance();
    CameraOld cameraOld = CameraOld.getInstance();

    Camera2View camera2View = null;
    CameraView cameraView = null;
    RectView rectView = null;
    ImageButton addPictureButton = null;
    CitrusFaceManager citrusFaceManager = null;

    // Context context = null;


    private BlackCatRunnable detectionRunnable = null;
    private BlackCatRunnable recognitionRunnable = null;

    private Thread detectThread = null;
    private Thread recognitionThread = null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        // citrusFaceManager
        // TODO Delete after debug
        Log.i(Settings.TAG, "camera fragment destroyed.");

        super.onDestroy();
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
                        // TODO Delete this todo task until camera2 is fully supported.
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

                    citrusFaceManager.setUpAppInfo(getActivity());
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
                                    detectThread = new Thread(detectionRunnable);
                                    recognitionThread = new Thread(recognitionRunnable);

                                    detectThread.start();
                                    recognitionThread.start();
                                }

                                citrusFaceManager.doFaceTrack();

                                citrusFaceManager.drawRect(rectView);

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

                }
            });

            addPictureButton = v.findViewById(R.id.add_picture_button);
            addPictureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    detectionRunnable.setRunning(false);
                    recognitionRunnable.setRunning(false);
                    detectThread.interrupt();
                    recognitionThread.interrupt();

                    try{
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }

                    Log.i(Settings.TAG, "thread stopped.");
                    cameraOld.closeCamera();
                    citrusFaceManager.destroySDK();

                    Log.i(Settings.TAG, "SDK and camera closed.");

                    Intent intent = new Intent(getActivity(), DataCenterActivity.class);
                    Log.i(Settings.TAG, "intent inited.");
                    startActivity(intent);
                }
            });
        }
        rectView = v.findViewById(R.id.rect_view);
        rectView.init();

        return v;
    }

    public void setDetectionRunnable(){
        detectionRunnable = new BlackCatRunnable() {
            @Override
            protected void blackCatRun() {

                try {
                    Thread.sleep(2000);
                    while (isRunning()) {

                        Log.i(Settings.TAG, "in onPreviewFrame.");
                        setCurrentTime(System.currentTimeMillis());
                        int faceNum = 0;
                        if(isRunning()){
                            faceNum = citrusFaceManager.getFaceNum();
                        }
                        // TODO Delete below after debug
                        Log.i(Settings.TAG, "face detected:" + faceNum);
                        setCurrentTime(System.currentTimeMillis() - getCurrentTime());
                        if (faceNum == 5) {
                            while (citrusFaceManager.getResFaceNum() == 5) {
                                Thread.sleep(500);
                            }
                        }
                    }
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

        };
    }

    public void setRecognitionRunnable(){
        recognitionRunnable = new BlackCatRunnable() {
            @Override
            protected void blackCatRun() {
                while (isRunning()) {

                    int faceNum = 0;
                    if(isRunning()){
                        faceNum = citrusFaceManager.getResFaceNum();
                    }
                    // TODO Delete after debug
                    Log.i(Settings.TAG, "in recognition , faca Num");
                    // Thread.sleep(2000);
                    for (int i = 0; i < faceNum; i++) {
                        citrusFaceManager.doFaceRecognition(i);
                        // TODO Need refactor
//                        if(result != null){
//                            if (result.first == 0) {
//                                Log.i(Settings.TAG, "Recog " + i + ", id = " + result.second);
//                            }
//                        }

                    }
                }
            }
        };
    }


}
