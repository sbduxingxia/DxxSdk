package com.zhp.sdk.loading;

import android.content.Context;

/**
 * 加载条管理类
 * Created by zhp on 2016/11/29.
 */

public class LoadingManager {
    protected static LoadingManager instance;
    protected LoadingDialog loadingDialog;
    protected ILoadingListener iLoadingListener;
    public static LoadingManager instance(){
        if(instance==null){
            instance = new LoadingManager();
        }
        return instance;
    }
    public static void destroy(){
        if(instance!=null){
            instance.freeAll();
            instance=null;
        }
    }

    public LoadingManager(){
    }
    public void bind(ILoadingListener iLoadingListener){this.iLoadingListener = iLoadingListener;}

    public void show(ILoadingInfo iLoadingInfo){
        if(iLoadingInfo==null||iLoadingInfo.getActivity()==null){
            return;
        }
        hide();
        if(loadingDialog==null){
            if(iLoadingListener==null){
                loadingDialog = new LoadingDialog(iLoadingInfo.getActivity());
            }else{
                loadingDialog = new LoadingDialog(iLoadingInfo.getActivity(),iLoadingListener);
            }
        }
        loadingDialog.show(iLoadingInfo);
    }
    public void show(ILoadingInfo iLoadingInfo,ILoadingListener iLoadingListener){
        if(iLoadingInfo==null||iLoadingInfo.getActivity()==null){
            return;
        }
        hide();
        if(loadingDialog==null){
            loadingDialog = new LoadingDialog(iLoadingInfo.getActivity(),iLoadingListener);
        }
        loadingDialog.show(iLoadingInfo);

    }
    public void hide(){
        if(loadingDialog!=null){
            loadingDialog.dismiss();
            loadingDialog=null;
        }
    }
    private void freeAll(){
        if(loadingDialog!=null){
            loadingDialog.dismiss();
            loadingDialog=null;
        }

    }


}
