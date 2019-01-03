package com.android.keenjackdaw.blackcat.controller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.keenjackdaw.blackcat.Settings;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;

public class Speeker {
    private SpeechSynthesizer speeker = null;
    private InitListener initListener = null;
    private SynthesizerListener synthesizerListener = null;
    private String enginTpye = SpeechConstant.TYPE_LOCAL;

    public Speeker(Activity activity){
        init(activity);
    }

    private void init(Activity activity){

        SpeechUtility.createUtility(activity, SpeechConstant.APPID + Settings.IFLYTEK_APP_ID + ","
                + SpeechConstant.ENGINE_MODE + Settings.ENGINE_MODE + ","
                + SpeechConstant.FORCE_LOGIN + "=true"
        );
        setInitListener(activity);
        speeker = SpeechSynthesizer.createSynthesizer(activity, initListener);
        setSpeechParam(activity);
        setSynthesizerListener();
    }

    private void setInitListener(final Activity activity){
        initListener = new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    Log.i(Settings.TAG, "Create speeker failed.");
                    Toast.makeText(activity,"初始化失败,错误码："+code, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void setSynthesizerListener(){
        synthesizerListener = new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {

            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {

            }

            @Override
            public void onSpeakPaused() {

            }

            @Override
            public void onSpeakResumed() {

            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {

            }

            @Override
            public void onCompleted(SpeechError speechError) {

            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        };
    }

    private void setSpeechParam(Activity activity){
        speeker.setParameter(SpeechConstant.PARAMS, null);
        speeker.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        //设置发音人资源路径
        speeker.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath(activity));
        //设置发音人
        speeker.setParameter(SpeechConstant.VOICE_NAME, Settings.DEFAULT_SPEEKER);

        //设置合成语速
        speeker.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        speeker.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        speeker.setParameter(SpeechConstant.VOLUME, "30");
        //设置播放器音频流类型
        speeker.setParameter(SpeechConstant.STREAM_TYPE, "3");

        // 设置播放合成音频打断音乐播放，默认为true
        speeker.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");
    }

    private String getResourcePath(Activity activity){
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(activity, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(activity, ResourceUtil.RESOURCE_TYPE.assets, "tts/" + Settings.DEFAULT_SPEEKER + ".jet"));
        return tempBuffer.toString();
    }

    public void speek(String text){
        int code = speeker.startSpeaking(text, synthesizerListener);
        Log.i(Settings.TAG, "code is " + code);
    }
}
