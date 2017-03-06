package com.zhp.sdk;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.zhp.sdk.tiptool.ITipDialogListener;
import com.zhp.sdk.tiptool.TipAlertDialog;
import com.zhp.sdk.utils.Common;

/**
 * Created by zhp on 2016/11/30.
 */

public class BaseApp extends Application{
    private final String  TAG = "MyApp";
    private static BaseApp instance;
    public static boolean isInBackground=false;
    private static Handler mHandler;
    private Toast appToast;
    private DeviceConfig deviceConfig;
    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        //注册activity生命周期监听
        registActivityAction();
        initRunOnUI();
        initToast();
        initDeviceConf();
    }
    private void initDeviceConf(){
        deviceConfig = new DeviceConfig(this);
        deviceConfig.initDeivceConfig();
    }
    private void initToast(){
        appToast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT);
    }
    private void initRunOnUI(){
        mHandler=new Handler(getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case Toast.LENGTH_SHORT:
                    case Toast.LENGTH_LONG:
                        showToast(msg.what,msg.arg1,msg.obj.toString());
                        /*if(msg.arg1==Tip.CENTER){
                            Toast toast = Toast.makeText(getApplicationContext(),msg.obj.toString(),msg.what);
                            toast.setGravity(msg.arg1,0,0);
                            toast.show();
                        }else {
                            showToast(msg.arg1,msg.obj.toString());
                        }*/
                        break;
                    case RunOnUI.RUN_ON_UI:
                        Runnable runnable = (Runnable) msg.obj;
                        runnable.run();
                        break;
                    default:
                        return;

                }
            }
        };
    }

    private void showToast(int tipType,int showType,String msg){
        if(appToast!=null){
            appToast.setText(msg);
            appToast.setDuration(tipType);
            appToast.setGravity(showType,0,0);
            appToast.show();
        }
    }
    private void registActivityAction(){
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if(isInBackground){
                    isInBackground=false;
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    @Override
    public void onTrimMemory(int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            //. Logger.d(TAG, "app went to background");
            isInBackground = true;
            Log.d(TAG, "app went to background");
        }
        super.onTrimMemory(level);
    }

    public static BaseApp getInstance(){
        return instance;
    }
    public static Handler getMainUIHandler(){
        return mHandler;
    }
    public DeviceConfig getDeviceConfig(){
        return deviceConfig;
    }


}
