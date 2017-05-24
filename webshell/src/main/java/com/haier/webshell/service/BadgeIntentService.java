package com.haier.webshell.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.haier.webshell.MainActivity;
import com.haier.webshell.R;

import me.leolin.shortcutbadger.ShortcutBadger;

public class BadgeIntentService extends IntentService {

    private int notificationId = 1000;
    private Bitmap largeIcon;

    public static Intent createIntent(Context context, int num) {
        Intent i = new Intent(context, BadgeIntentService.class);
        i.putExtra("badgeCount", num);
        return i;
    }

    public BadgeIntentService() {
        super("BadgeIntentService");
    }

    private NotificationManager mNotificationManager;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int badgeCount = intent.getIntExtra("badgeCount", 0);
            if (mNotificationManager == null) {
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            try {
                mNotificationManager.cancel(notificationId);
                notificationId++;
                Notification.Builder builder = new Notification.Builder(getApplicationContext());

                builder.setContentTitle("SpiderMan")//设置通知栏标题
                        .setContentText("You have " + badgeCount + " unprocessed transactions") //<span style="font-family: Arial;">/设置通知栏显示内容</span>
                        .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
//                  .setNumber(5) //设置通知集合的数量
//                        .setTicker("You have a message.") //通知首次出现在通知栏，带上升动画效果的
                        .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                        .setPriority(Notification.PRIORITY_MAX) //设置该通知优先级
                        .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                        .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                        .setDefaults(Notification.DEFAULT_VIBRATE);//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission

                if (largeIcon == null) {
                    largeIcon = BitmapFactory.decodeResource(
                            getResources(), R.drawable.icon_notifaction);
                }
                builder.setLargeIcon(largeIcon)
                        .setSmallIcon(R.drawable.icon_notifaction);//设置通知小ICON
                Notification notification = builder.build();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);
                builder.setContentIntent(pendingIntent);
                ShortcutBadger.applyNotification(getApplicationContext(), notification, badgeCount);
                mNotificationManager.notify(notificationId, notification);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onDestroy() {
        if (largeIcon != null && !largeIcon.isRecycled()) {
            largeIcon.recycle();
            largeIcon = null;
        }
        super.onDestroy();
    }

    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }
}
