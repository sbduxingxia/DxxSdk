package com.zhp.sdk.loading;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhp.sdk.R;

/**
 * 加载框
 * Created by zhp on 2016/11/29.
 */

public class LoadingDialog extends AlertDialog {
    protected ILoadingListener iLoadingListener;
    protected ILoadingInfo iLoadingInfo;
    protected TextView loadingTips;
    private final String 正在加载="正在加载";

    public LoadingDialog(Context context,ILoadingListener iLoadingListener){
        this(context);
        this.iLoadingListener = iLoadingListener;
    }

    public LoadingDialog(Context context) {
        super(context,R.style.LoadingDialog);
        setCanceledOnTouchOutside(false);
    }

    public LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setCanceledOnTouchOutside(false);
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }
    @Override
    public void show() {
        if(this.iLoadingListener!=null){
            this.iLoadingListener.loadingStart();
        }
        super.show();
    }
    public void show(ILoadingInfo iLoadingInfo){
        show();
        if(loadingTips!=null&&iLoadingInfo!=null&&!TextUtils.isEmpty(iLoadingInfo.getLoadingTips())){
            loadingTips.setText(iLoadingInfo.getLoadingTips());
        }else if(loadingTips!=null){
            loadingTips.setText(正在加载);
        }
        this.iLoadingInfo = iLoadingInfo;
    }


    @Override
    public void dismiss() {
        if(this.iLoadingListener!=null){
            this.iLoadingListener.loadingEnd();
        }
        super.dismiss();
    }

    private void initViews(){

        setContentView(R.layout.zp_dialog_loading);
        WindowManager windowManager = getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int)(display.getWidth()*0.3); //设置宽度
        lp.height = lp.width;
        getWindow().setAttributes(lp);
        loadingTips = (TextView) findViewById(R.id.loading_tips);
    }

    @Override
    public void onBackPressed() {
        if(iLoadingInfo!=null&&iLoadingInfo.isBackPressClose()){
            LoadingManager.instance().hide();
        }
    }
}
