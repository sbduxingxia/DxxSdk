package com.zhp.sdk;

import android.os.Message;

/**
 * Created by zhp on 2016/11/30.
 */

public class RunOnUI {
    public static final int RUN_ON_UI=2;
    public static void runOnUIThread(Runnable runnable){
        if(runnable==null) return;
        Message msg = BaseApp.getMainUIHandler().obtainMessage(RUN_ON_UI);
        msg.obj=runnable;
        BaseApp.getMainUIHandler().sendMessage(msg);
    }

    /**
     * 延迟加载
     * @param runnable
     * @param waitTime
     */
    public static void runOnUIDelayed(Runnable runnable,int waitTime){
        if(runnable==null) return;
        BaseApp.getMainUIHandler().postDelayed(runnable,waitTime);
    }
}
