package com.android.keenjackdaw.blackcat.controller;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import com.android.keenjackdaw.blackcat.R;
import com.android.keenjackdaw.blackcat.Settings;
import com.android.keenjackdaw.blackcat.BlackCatApplication;
import com.android.keenjackdaw.blackcat.exception.BlackCatException;
import com.android.keenjackdaw.blackcat.ui.RectView;
import com.rokid.facelib.ImageRokidFace;
import com.rokid.facelib.VideoRokidFace;
import com.rokid.facelib.api.IImageRokidFace;
import com.rokid.facelib.api.IVideoRokidFace;
import com.rokid.facelib.conf.DFaceConf;
import com.rokid.facelib.conf.SFaceConf;
import com.rokid.facelib.db.UserInfo;
import com.rokid.facelib.engine.DataFormat;
import com.rokid.facelib.engine.FaceDbEngine;
import com.rokid.facelib.face.FaceDbHelper;

import org.jetbrains.annotations.Contract;
import java.util.Arrays;

public class CitrusFaceManager {
    private CitrusFaceManager() {}

    private static CitrusFaceManager instance = new CitrusFaceManager();
    private byte[][] byteBuffers = null;
    private Activity activity = null;
    private Context appContext = null;
    private Speeker speeker = null;

    // facelib
    private IVideoRokidFace videoFace;
    private IImageRokidFace imageFace;

    FaceDbEngine dbFace;
    private DFaceConf dFaceConf;
    private SFaceConf sFaceConf;

    @Contract(pure = true)
    public static CitrusFaceManager getInstance() {
        if(instance == null){
            // TODO Delete after debug
            Log.i(Settings.TAG, "SDK instance is null.");
            instance = new CitrusFaceManager();
            return instance;
        }
        Log.i(Settings.TAG, "SDK instance is not null.");
        return instance;
    }

    public void setByteBuffers(byte[][] byteBuffers) {
        this.byteBuffers = byteBuffers;
    }

    public void setUpAppInfo(Activity activity){
        this.activity = activity;
        appContext = activity.getApplicationContext();
    }

    public void destory(){
        videoFace.destroy();
    }

    public void initCitrusFaceSDK(int width, int height) throws BlackCatException{
        speeker = new Speeker(activity);
        setBuffer(width, height);
        dFaceConf = new DFaceConf();
        sFaceConf = new SFaceConf();


        dFaceConf.setSize(width, height);
        dFaceConf.setRoi(new Rect(
                (int)Settings.ROI_X * width,
                (int)Settings.ROI_Y * height,
                (int)Settings.ROI_W * width,
                (int)Settings.ROI_H * height
        ));
        dFaceConf.setDataType(DataFormat.DATA_BITMAP);

        sFaceConf.setRecog(true, "/sdcard/facesdk/");
        sFaceConf.setAutoRecog(true);

        videoFace = VideoRokidFace.create(appContext, dFaceConf);
        videoFace.sconfig(sFaceConf);

        imageFace = ImageRokidFace.create(appContext);
        imageFace.sconfig(sFaceConf);

    }

    private void setBuffer(int width, int height){
        byteBuffers = new byte[Settings.IMAGE_CHANNEL_NUM][];

        for(int i = 0; i < Settings.IMAGE_CHANNEL_NUM; i++){
            byteBuffers[i] = new byte[width * height * 3 / 2];
        }
        for (int i = 0; i < Settings.IMAGE_CHANNEL_NUM; i++) {
            Arrays.fill(byteBuffers[i], (byte) 0);
        }
    }

    public IVideoRokidFace getVideoFace() {
        return videoFace;
    }

    public IImageRokidFace getImageFace() {
        return imageFace;
    }

    public byte[][] getByteBuffers() {
        return byteBuffers;
    }

}
