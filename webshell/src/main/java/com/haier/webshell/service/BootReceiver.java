package com.haier.webshell.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by zhp.dts on 2017/5/18.
 */

public class BootReceiver extends BroadcastReceiver {
    public static final String ACTION_SERVICE_DESTORY = "com.haier.webshell.service.destory";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_SERVICE_DESTORY)
                || Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())
                || Intent.ACTION_USER_PRESENT.equals(intent.getAction())
                || Intent.ACTION_PACKAGE_RESTARTED.equals(intent.getAction())) {
            //TODO
            //在这里写重新启动service的相关操作

        }
        Log.e("BootReceiver", "onRecevie:" + intent.getAction());
        BaseService.startBaseService(context);
    }

    public static Intent createBroadcastIntent() {
        Intent i = new Intent(ACTION_SERVICE_DESTORY);
        return i;
    }

    public static BootReceiver createReceiver(Context context) {
        BootReceiver bootReceiver = new BootReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SERVICE_DESTORY);
        context.registerReceiver(bootReceiver, intentFilter);
        return bootReceiver;
    }
}
