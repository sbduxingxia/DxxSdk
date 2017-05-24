package com.haier.webshell.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

/**
 * Created by zhp.dts on 2017/5/18.
 */

public class StartServiceRunnable implements Runnable {
    protected Service service;
    protected Intent otherService;
    protected Handler mHnadler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            service.startService(otherService);
            super.handleMessage(msg);
        }
    };

    public StartServiceRunnable(Service service, Intent otherService) {
        this.service = service;
        this.otherService = otherService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mHnadler.sendEmptyMessage(0);
        }
    }
}
