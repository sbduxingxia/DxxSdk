package com.zhp.sdkdemo.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.zhp.sdk.BaseApp;
import com.zhp.sdk.utils.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhp.dts on 2017/3/3.
 */

public class RecordCallService extends Service {
    private final static String Call_Out_Number = "call_out";
    private final static String Sava_File_Path = "save_file";
    protected PhoneListener phoneListener;
    protected TelephonyManager telephonyManager;
    protected String saveFile;
    protected String callNumber = "12312312312";

    /**
     * 服务启动
     *
     * @author zhp.dts
     * @time 2017/3/3 17:36
     * listenerNumber , 用户拨打的电话号码
     * saveFilePath , 文件路径，全路径
     */
    public static void start(String listenerNumber, String saveFilePath) {
        Intent service = new Intent(BaseApp.getInstance(), RecordCallService.class);
        service.putExtra(Call_Out_Number, listenerNumber);
        service.putExtra(Sava_File_Path, saveFilePath);
        BaseApp.getInstance().startService(service);   //启动服务
    }

    /**
     * 服务关闭
     *
     * @author zhp.dts
     * @time 2017/3/3 17:37
     */
    public static void stop() {
        Intent service = new Intent(BaseApp.getInstance(), RecordCallService.class);
        BaseApp.getInstance().stopService(service);   //启动服务
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        phoneListener = new PhoneListener();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);  //注册监听器 监听电话状态
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (phoneListener != null) {
            phoneListener = null;
        }
    }

    private final class PhoneListener extends PhoneStateListener {
        private String incomeNumber;   //来电号码
        private MediaRecorder mediaRecorder;
        private File file;

        /**
         * 释放资源
         *
         * @author zhp.dts
         * @time 2017/3/3 17:47
         */
        public void recycle() {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
            }
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:   //无操作
                        this.incomeNumber = incomingNumber;
                        Log.d(getClass().getSimpleName(), "onCallStateChanged:无操作");
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:   //接通电话
                        Log.d(getClass().getSimpleName(), "onCallStateChanged:接通电话");
                        if (!StringUtils.isEmpty(saveFile)) {
                            file = new File(saveFile);
                        } else {
                            file = new File(Environment.getExternalStorageDirectory(), callNumber + System.currentTimeMillis() + ".3gp");
                        }
                        mediaRecorder = new MediaRecorder();
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);   //获得声音数据源
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);   // 按3gp格式输出
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        mediaRecorder.setOutputFile(file.getAbsolutePath());   //输出文件
                        Log.d(getClass().getSimpleName(), "onCallStateChanged: file->" + file.getAbsolutePath());
                        mediaRecorder.prepare();    //准备
                        mediaRecorder.start();
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:  //挂掉电话
                        Log.d(getClass().getSimpleName(), "onCallStateChanged:挂掉电话");
                        recycle();
                        RecordCallService.stop();
                        break;
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
                RecordCallService.stop();
            } catch (IOException e) {
                e.printStackTrace();
                RecordCallService.stop();
            }
        }
    }
}

