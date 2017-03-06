package com.zhp.sdk.update;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

/**
 *
 * Created by zhp on 2016/11/26.
 */

public class UpdateManager {
    private static UpdateManager instance;
    protected IUpdateListener iListener;
    UpdateDialog updateDialog;
    /**
     * @return
     */
    public static UpdateManager instance(){
        if(instance==null){
            instance = new UpdateManager();
        }
        return instance;
    }
    /**
     * @return
     */
    public static UpdateManager instance(IUpdateListener iUpdateListener){
        if(instance==null){
            instance = new UpdateManager(iUpdateListener);
        }
        return instance;
    }
    /**
     * 注销update管理器
     */
    public static void destroy(){
        if(instance!=null){
            instance.freeALl();
            instance=null;
        }
    }
    /**
     * 绑定activity
     */
    public void bind(IUpdateListener iUpdateListener){
        setiListener(iUpdateListener);
    }
    /**
     * 显示窗口
     */
    public void show(IUpdateInfo iUpdateInfo){
        if(iUpdateInfo==null||iUpdateInfo.getActivity()==null){
            return;
        }
        hide();
        if(updateDialog==null){
            updateDialog = new UpdateDialog(iUpdateInfo.getActivity(),iListener);
        }
        if(updateDialog!=null){
            updateDialog.show(iUpdateInfo);
        }
    }
    public void hide(){
        if(updateDialog!=null){
            updateDialog.dismiss();
            updateDialog=null;
        }
    }


    protected void freeALl(){
        if(updateDialog!=null){
            updateDialog.dismiss();
            updateDialog=null;
        }
        if(iListener!=null){
            iListener.updateDestroy();
        }
    }
    private UpdateManager(){
//        initUpdateDialog(context);
    }
    private UpdateManager(IUpdateListener iUpdateListener){
        setiListener(iUpdateListener);
//        initUpdateDialog(context);
    }
    public void setiListener(IUpdateListener iListener) {
        this.iListener = iListener;
    }
}
