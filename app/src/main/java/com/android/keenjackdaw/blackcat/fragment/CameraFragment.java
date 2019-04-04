package com.android.keenjackdaw.blackcat.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.rokid.facelib.api.RokidFaceCallback;
import com.rokid.facelib.face.FaceDbHelper;
import com.rokid.facelib.input.VideoInput;
import com.rokid.facelib.model.FaceDO;
import com.rokid.facelib.model.FaceModel;
import com.rokid.facelib.view.InjectFaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class CameraFragment extends Fragment {

    CameraNew cameraNew = CameraNew.getInstance();
    CameraOld cameraOld = CameraOld.getInstance();

    Camera2View camera2View = null;
    CameraView cameraView = null;
    RectView rectView = null;
    ImageButton addPictureButton = null;
    CitrusFaceManager citrusFaceManager = null;

    String faceInfo = null;
    InjectFaceView injectFaceView = null;

    // Context context = null;


    private BlackCatRunnable detectionRunnable = null;
    private BlackCatRunnable recognitionRunnable = null;
    private BlackCatRunnable aggRunnable = null;

    private Thread detectThread = null;
    private Thread recognitionThread = null;
    private Thread aggThread = null;


    private Handler mH = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) { }
    };

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

        v = inflater.inflate(R.layout.fragment_camera, container, false);
        cameraView = v.findViewById(R.id.camera_view);
        injectFaceView = v.findViewById(R.id.inject_view);
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
                            camera.addCallbackBuffer(data);
                            citrusFaceManager.getVideoFace().setData(new VideoInput(data));
                            citrusFaceManager.getVideoFace().startTrack(model ->{
                                injectFaceView.drawRects(model.getFaceList(),cameraView.getWidth(),cameraView.getHeight(),false);
                            });
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

                citrusFaceManager.getVideoFace().destroy();
                cameraOld.closeCamera();

                Intent intent = new Intent(getActivity(), DataCenterActivity.class);
                Log.i(Settings.TAG, "intent inited.");
                startActivity(intent);
            }
        });

        return v;
    }

    public void setAggRunnable() {
        aggRunnable = new BlackCatRunnable() {
            @Override
            protected void blackCatRun() {

                try {
                    Thread.sleep(2000);
                    while (isRunning()) {

                    }
                }
                catch(Exception e){

                }
            }

        };
    }
}
