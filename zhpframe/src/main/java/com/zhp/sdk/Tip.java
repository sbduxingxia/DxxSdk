package com.zhp.sdk;

import android.content.Context;
import android.os.Message;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by zhp on 2016/11/30.
 */

public class Tip {
    public static final int LONG=Toast.LENGTH_LONG;
    public static final int SHORT=Toast.LENGTH_SHORT;
    public static final int CENTER = Gravity.CENTER;
    public static final int BOTTOM = Gravity.BOTTOM;

    /**
     * 显示提示
     * @param notice
     * 默认时间短
     */
    public static void show(String notice){
        Message msg = BaseApp.getMainUIHandler().obtainMessage(SHORT,0,0);
        msg.obj = notice;
        BaseApp.getMainUIHandler().sendMessage(msg);
    }
    /**
     * 显示提示
     * @param resId
     * 默认时间短
     */
    public static void show(int resId){
        Message msg = BaseApp.getMainUIHandler().obtainMessage(SHORT,BOTTOM,0);
        msg.obj = BaseApp.getInstance().getString(resId);
        BaseApp.getMainUIHandler().sendMessage(msg);
    }
    /**
     * 显示提示
     * @param notice
     * 默认时间短
     */
    public static void showCenter(String notice){
        Message msg = BaseApp.getMainUIHandler().obtainMessage(SHORT,CENTER,0);
        msg.obj = notice;
        BaseApp.getMainUIHandler().sendMessage(msg);
    }
    /**
     * 显示提示
     * @param resId
     * 默认时间短
     */
    public static void showCenter(int resId){
        Message msg = BaseApp.getMainUIHandler().obtainMessage(SHORT,CENTER,0);
        msg.obj = BaseApp.getInstance().getString(resId);
        BaseApp.getMainUIHandler().sendMessage(msg);
    }
    /**
     * 显示提示
     * @param notice
     * @param tipType
     */
    public static void show(String notice,int tipType){
        Message msg = BaseApp.getMainUIHandler().obtainMessage(tipType,BOTTOM,0);
        msg.obj = notice;
        BaseApp.getMainUIHandler().sendMessage(msg);
    }
    /**
     * 显示提示
     * @param resId
     * @param tipType
     */
    public static void show(int resId,int tipType){
        Message msg = BaseApp.getMainUIHandler().obtainMessage(tipType,BOTTOM,0);
        msg.obj = BaseApp.getInstance().getString(resId);
        BaseApp.getMainUIHandler().sendMessage(msg);
    }
    /**
     * 显示提示
     * @param notice
     * 默认时间短
     */
    public static void showCenter(String notice,int tipType){
        Message msg = BaseApp.getMainUIHandler().obtainMessage(tipType,CENTER,0);
        msg.obj = notice;
        BaseApp.getMainUIHandler().sendMessage(msg);
    }
    /**
     * 显示提示
     * @param resId
     * 默认时间短
     */
    public static void showCenter(int resId,int tipType){
        Message msg = BaseApp.getMainUIHandler().obtainMessage(tipType,CENTER,0);
        msg.obj = BaseApp.getInstance().getString(resId);
        BaseApp.getMainUIHandler().sendMessage(msg);
    }
}
