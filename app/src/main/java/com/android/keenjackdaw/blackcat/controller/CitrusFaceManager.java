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
import com.rokid.facelib.input.VideoInput;

import org.jetbrains.annotations.Contract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CitrusFaceManager {
    private CitrusFaceManager() {}

    private static CitrusFaceManager instance = new CitrusFaceManager();
    private byte[][] byteBuffers = null;
    private Activity activity = null;
    private Context appContext = null;
    private Speeker speeker = null;

    // facelib
    private IVideoRokidFace videoFace;
    IImageRokidFace imageFace;

    FaceDbEngine dbFace;
    private DFaceConf dFaceConf;
    private SFaceConf sFaceConf;
    private FaceDbHelper dbCreator;

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

        dbCreator = new FaceDbHelper(appContext);

        dbCreator.configDb("user.db");

        UserInfo info = dbCreator.query("5d4e6666-8264-40b7-8ecc-137b4e01e493");

        dFaceConf = new DFaceConf();
        dFaceConf.setSize(width, height);
        dFaceConf.setRoi(new Rect(
                (int)Settings.ROI_X * width,
                (int)Settings.ROI_Y * height,
                (int)Settings.ROI_W * width,
                (int)Settings.ROI_H * height
        ));
        dFaceConf.setDataType(DataFormat.DATA_BITMAP);

        sFaceConf = new SFaceConf();
        sFaceConf.setRecog(true, "/sdcard/facesdk/user.db");
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

    public void addUser(Bitmap bm, UserInfo userInfo){
        dbCreator.add(bm, userInfo);
    }
}
