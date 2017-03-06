package com.zhp.sdk.update;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhp.sdk.R;
import com.zhp.sdk.utils.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * 升级提示及其升级对话框
 * Created by zhp on 2016/11/26.
 */

public class UpdateDialog extends AlertDialog implements View.OnClickListener {
    TextView updateTitle;
    TextView updateTips;
    TextView updateProgressTips;
    ProgressBar updateProgress;
    LinearLayout updateProgressLnly;
    Button updateCancel;
    Button updateOk;

    private IUpdateListener iUpdateListener;
    private IUpdateInfo updateInfo;
    private DownLoadApk downAPK;
    private String path;
    private final String
            STR_INSTALL="安  装",
            STR_OK="确  定",
            STR_RETRY="重  试",
            STR_CANCEL="取  消",
            STR_ERROR="文件下载失败！",
            STR_TO_INSTALL="文件已下载完成",
            STR_NEED_UPDATE="有新版本，是否更新？",
            STR_APK_DOWNLOADING="文件正在下载。";
    private final int
            下载提醒=1,
            正在下载=2,
            下载完成=3,
            错误提醒=-1;

    private int stepType=1;
    public UpdateDialog(Context context,IUpdateListener iUpdateListener){
        super(context, R.style.Dialog);
        this.iUpdateListener = iUpdateListener;
//        initViews();
    }

    public UpdateDialog(Context context) {
        super(context,R.style.Dialog);
//        initViews();
    }
    public UpdateDialog(Context context, int themeResId) {
        super(context, themeResId);
//        initViews();
    }

    protected UpdateDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
//        initViews();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("dialog","onStop");

    }

    protected void initViews(){
        getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#5f2f2f2f")));
        setContentView(R.layout.zp_dialog_update);
        updateOk = (Button) findViewById(R.id.zp_update_ok);
        updateCancel= (Button) findViewById(R.id.zp_update_cancel);

        updateProgress = (ProgressBar) findViewById(R.id.zp_update_progress);
        updateProgressLnly = (LinearLayout) findViewById(R.id.zp_update_progress_lnly);
        updateProgressTips = (TextView) findViewById(R.id.zp_update_progress_tips);

        updateTips = (TextView) findViewById(R.id.zp_update_tips);
        updateTitle = (TextView) findViewById(R.id.zp_update_title);

        updateOk.setOnClickListener(this);
        updateCancel.setOnClickListener(this);

    }



    public void onClick(View view) {
        if(view.getId()==R.id.zp_update_ok){
            //开始下载
            if(stepType==下载提醒||stepType==错误提醒){
                //下载
                gotoDownLoading();
                downAPK = new DownLoadApk();
                downAPK.execute();
            }else if(stepType==正在下载) {

            }else{
                File file = new File(path, updateInfo.getAppName());
                if(file.exists()){
                    AppUtils.installApk(getContext(),file);
                }else{
                    gotoError();
                }

            }
        }
        else if(view.getId()==R.id.zp_update_cancel){
            if(stepType==正在下载){
                //下载
               if(downAPK!=null&&!downAPK.isCancelled()){
                   downAPK.cancel(false);
               }
                gotoError();
                if(iUpdateListener!=null){
                    iUpdateListener.updateDestroy();
                }
            }else{
                //取消
                UpdateManager.instance().hide();
            }
        }
    }
    private void progressNormal(){
        updateProgress.setMax(100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateProgress.setProgress(0,true);
        }else{
            updateProgress.setProgress(0);
        }
        updateProgress.setSecondaryProgress(0);
    }

    /**
     * 开始下载
     */
    private void gotoDownLoading(){
        stepType=正在下载;
        updateProgressLnly.setVisibility(View.VISIBLE);
        progressNormal();
        updateOk.setText(STR_INSTALL);
        updateOk.setEnabled(false);
        updateTips.setTextColor(getContext().getResources().getColor(R.color.progressbar_tips));
        updateTips.setText(STR_APK_DOWNLOADING);
        updateProgressTips.setText("0%");
    }

    /**
     * 默认状态
     */
    private void gotoNormal(){
        stepType=下载提醒;
        updateProgressLnly.setVisibility(View.GONE);
        updateOk.setText(STR_OK);
        updateOk.setEnabled(true);
        updateTips.setTextColor(getContext().getResources().getColor(R.color.progressbar_tips));
        updateTitle.setText(updateInfo.getTitle()==null?STR_NEED_UPDATE:updateInfo.getTitle());
        if(!TextUtils.isEmpty(updateInfo.getTitle())){
            updateTips.setText(updateInfo.getContentTips());
        }
    }

    /**
     * 安装软件
     */
    private void gotoInstallAPK(){
        stepType=下载完成;
        updateProgressLnly.setVisibility(View.VISIBLE);
        updateOk.setText(STR_INSTALL);
        updateOk.setEnabled(true);
        updateTips.setTextColor(getContext().getResources().getColor(R.color.progressbar_tips));
        updateTips.setText(STR_TO_INSTALL);
    }

    /**
     * 报错
     */
    private void gotoError(){
        stepType=错误提醒;
        updateProgressLnly.setVisibility(View.GONE);
        updateOk.setText(STR_RETRY);
        updateOk.setEnabled(true);
        updateTips.setText(STR_ERROR);
        updateTips.setTextColor(Color.RED);
        if(downAPK!=null){
            if(!downAPK.isCancelled()){
                downAPK.cancel(false);
            }
        }
    }

    /**
     * 校验参数是否正常
     * @param iUpdateInfo
     * @return
     */
    private boolean checkUpdateInfo(IUpdateInfo iUpdateInfo){
        if(iUpdateInfo==null) return false;
        if(TextUtils.isEmpty(iUpdateInfo.getDownUrls())
                ||TextUtils.isEmpty(iUpdateInfo.getAppName())
//                ||TextUtils.isEmpty(iUpdateInfo.getTitle())
//                ||TextUtils.isEmpty(iUpdateInfo.getContentTips())
                ){
            return false;
        }

        return true;
    }
    public void show(IUpdateInfo iUpdateInfo){
        show();
        updateInfo = iUpdateInfo;
        if(!checkUpdateInfo(updateInfo)){
            return;
        }


        gotoNormal();

        WindowManager windowManager = getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int)(display.getWidth()*0.9); //设置宽度
        getWindow().setAttributes(lp);
        /*try{
            show();
        }catch (Exception e){

        }*/

    }
    class DownLoadApk extends AsyncTask<Void, Integer, Integer>{


        public DownLoadApk(){

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
            File file = new File(path);
            if (!file.exists()) { file.mkdir(); }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if(values[0]<0){
                gotoError();
                if(iUpdateListener!=null){
                    iUpdateListener.updateDestroy();
                }
                return;
            }
            if(updateProgress!=null&&values[0]!=null){
                updateProgress.setProgress(values[0]);
                updateProgress.setSecondaryProgress(values[0]+1>100?100:(values[0]+1));
                updateProgressTips.setText(values[0]+"%");
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            Integer lengthOfFile = 0;

            try {
                URL url = new URL(updateInfo.getDownUrls());

                connection = (HttpURLConnection) url.openConnection();
//                connection.setReadTimeout(30*1000);
                connection.connect();
                // Getting file lenght
                lengthOfFile = connection.getContentLength();
                // Read file
                input = connection.getInputStream();
                // Where to write file
                output = new FileOutputStream(new File(path, updateInfo.getAppName()));

                byte data[] = new byte[4096];
                long total = 0;
                int count;

                while ((count = input.read(data)) != -1) {
                    // Close input if download has been cancelled
                    if (isCancelled()) {
                        input.close();
//                        publishProgress(-1);
                        return -1;
                    }
                    total += count;
                    // Updating download progress
                    if (lengthOfFile > 0) {
                        publishProgress((int) ((total * 100) / lengthOfFile));
                    }
                    output.write(data, 0, count);
                }

            } catch (Exception e) {
                e.printStackTrace();

                return null;
            } finally {
                try {
                    if (output != null) { output.close(); }
                    if (input != null) { input.close(); }
                } catch (IOException ignored) {}

                if (connection != null) {
                    connection.disconnect();
                }
            }

            return lengthOfFile;
        }
        @Override
        protected void onPostExecute(Integer file_length) {

            File file = new File(path, updateInfo.getAppName());
            if (file_length != null && file.length() == file_length) {
                // File download: OK
//                stepType=下载完成;
                if(iUpdateListener!=null){
                    iUpdateListener.updateSuccess();
                }
                AppUtils.installApk(getContext(),file);
                gotoInstallAPK();
            } else{
                // File download: FAILED
//                stepType=错误提醒;
                gotoError();
                if(iUpdateListener!=null){
                    iUpdateListener.updateError(-1,STR_ERROR);
                }
            }
        }


    }


    @Override
    public void onBackPressed() {
        if(updateInfo!=null&&updateInfo.isBackPressClose()){
            super.onBackPressed();
        }
    }
}
