package com.zhp.sdk.tiptool;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhp.sdk.R;

/**
 * Created by zhp.dts on 2017/3/4.
 */

public class TipAlertDialog extends AlertDialog {
    protected TextView titleTxt;
    protected TextView contentTxt;
    protected Button okBtn,cancelBtn;
    protected Context mContext;
    protected ITipDialogListener iTipDialogListener;
    public TipAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        setContentView(R.layout.zp_dialog_update);
        initViews();
        initListener();
    }
    public TipAlertDialog(Context context, int themeResId,ITipDialogListener iTipDialogListener) {
        super(context, themeResId);
        this.mContext = context;
        this.iTipDialogListener =iTipDialogListener;
        setContentView(R.layout.zp_dialog_update);
        initViews();
        initListener();
    }
    private void initViews(){
        titleTxt= (TextView) findViewById(R.id.zp_update_title);
        contentTxt= (TextView) findViewById(R.id.zp_update_tips);
        okBtn = (Button) findViewById(R.id.zp_update_ok);
        cancelBtn = (Button) findViewById(R.id.zp_update_cancel);
    }
    private void initListener(){
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iTipDialogListener!=null){
                    if(iTipDialogListener.confirm()){
                        hide();
                    }
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iTipDialogListener!=null){
                    if(iTipDialogListener.cancel()){
                        hide();
                    }
                }
            }
        });
    }
    public void setTitle(String title){
        if(titleTxt!=null){
            titleTxt.setText(title);
        }
    }
    public void setContentTxt(String content){
        if(contentTxt!=null){
            contentTxt.setText(content);
        }
    }
    public Button getOkBtn(){
        return okBtn;
    }
    public Button getCancelBtn(){
        return cancelBtn;
    }

    public void setTipListener(ITipDialogListener tipListener) {
        this.iTipDialogListener = tipListener;
    }
}
