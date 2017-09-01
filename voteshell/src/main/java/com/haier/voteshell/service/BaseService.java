package com.haier.voteshell.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.haier.voteshell.MainActivity;
import com.zhp.sdk.utils.SharedPreferencesUtils;

import java.util.ArrayList;

import me.leolin.shortcutbadger.ShortcutBadger;


/**
 * Created by zhp.dts on 2017/5/18.
 */

public class BaseService extends Service implements Runnable {
    private final String TAG = "BaseService";
    protected Thread runThread;
    protected Thread timerThread;
    protected boolean isRunning = false;
    protected String 获取未读消息数量 = "http://lapp.haier.net:8090/russiaFactory/factoryAction/getMessageNum?userid=";
    protected int lastUnreadNum = 0;
    protected int internalTime = 5;//单位秒
    protected static ArrayList<Long> waitToRunList = new ArrayList<>();
    protected boolean isTest = false;
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    notifactionToTop(msg.arg1);
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, START_FLAG_RETRY, startId);
        try {
            if (Build.VERSION.SDK_INT < 18) {
                Log.v(TAG, "startForgroundCompat");
                startForeground(1120, new Notification());
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "-------------onCreate------------");
        if (!isRunning) {
            isRunning = true;
            threadRun();
            timerRun();
        }
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "-------------onDestroy------------");
        isRunning = true;
        stopForeground(true);
        sendBroadcast(BootReceiver.createBroadcastIntent());
        super.onDestroy();
    }

    public static void startBaseService(Context context) {
        Intent i = new Intent(context, BaseService.class);
        context.startService(i);
    }

    public static Intent creatIntent(Context context) {
        Intent i = new Intent(context, BaseService.class);
        return i;
    }

    @Override
    public void run() {
        while (isRunning) {
            Long one = 0L;
            synchronized (waitToRunList) {
                if (waitToRunList.size() > 0) {
                    one = waitToRunList.get(0);
                    waitToRunList.remove(0);
                }
            }
            if (one > 0) {
                getMessageUnread();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        sendBroadcast(BootReceiver.createBroadcastIntent());
    }

    public static void addOneCommand(Long key) {
        synchronized (waitToRunList) {
            waitToRunList.add(key);
        }
    }

    private void threadRun() {
        runThread = new Thread(this);
        runThread.start();
    }

    private void timerRun() {
        timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    if (internalTime < 5) {
                        internalTime = 5;
                    }
                    try {
                        Thread.sleep(internalTime * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    addOneCommand(System.currentTimeMillis());
                }
            }
        });
        timerThread.start();
    }

    private void getMessageUnread() {
        String uid = (String) SharedPreferencesUtils.getParam(getApplicationContext(), MainActivity.ARG_UID, "");
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, 获取未读消息数量 + uid
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String data) {
                Log.e(TAG, data);
                try {
                    JSONObject obj = JSON.parseObject(data);
                    int interval = obj.getInteger("interval");
                    int num = obj.getInteger("num");
                    if (interval <= 0) {
                        interval = 5;
                    }
                    //回传间隔生效
                    if (!isTest) {
                        internalTime = interval;
                    }
                    Message msg = mHandler.obtainMessage();
                    msg.what = 0;
                    msg.arg1 = num;
                    mHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e(TAG, error.getLocalizedMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        requestQueue.add(stringRequest);
    }

    private void notifactionToTop(int num) {
        if (num <= 0 || (lastUnreadNum == num)) {
            if (isTest) {
                num = lastUnreadNum + 1;
            } else {
                return;
            }
        }
        lastUnreadNum = num;
        updateBadgeNum(num);
    }

    private void updateBadgeNum(int num) {
        if (num <= 0) {
            boolean success = ShortcutBadger.removeCount(this);
            return;
        }
        if (num > 99) {
            num = 99;
        }
        boolean firseSuccess = false;
        try {
            firseSuccess = ShortcutBadger.applyCount(BaseService.this, num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!firseSuccess) {
            try {
                startService(BadgeIntentService.createIntent(getApplicationContext(), num));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}
