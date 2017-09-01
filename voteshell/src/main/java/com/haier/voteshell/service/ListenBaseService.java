package com.haier.voteshell.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by zhp.dts on 2017/5/18.
 */

public class ListenBaseService extends Service {
    protected Thread saveThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
//        saveRun();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static Intent creatIntent(Context context) {
        Intent i = new Intent(context, ListenBaseService.class);
        return i;
    }

    private void saveRun() {
        saveThread = new Thread(new StartServiceRunnable(this, BaseService.creatIntent(getApplicationContext())));
        saveThread.start();
    }
}
