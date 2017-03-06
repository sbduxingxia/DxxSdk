package com.zhp.sdk.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.zhp.sdk.R;
import com.zhp.sdk.utils.StringUtils;
import com.zhp.sdk.webplus.IJsPlus;
import com.zhp.sdk.webplus.ZhpJsPlus;

import java.util.ArrayList;

/**
 * Created by zhp.dts on 2017/3/4.
 */

public class WebActivity extends BaseActivity {
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_URL = "arg_url";
    private static final String ARG_PLUS="arg_plus";
    WebView webView;
    String url = null;
    String title = null;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<IJsPlus> jsPlus;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zp_layout_web);
        initViews();
        restoreFromIntent(getIntent());
        setTitle(title);
        if (TextUtils.isEmpty(url) || "http://#".equals(url)) {
            finish();
            return;
        }
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
//		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

                return super.onJsAlert(view,title,message,result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (swipeRefreshLayout != null) {
                    if (swipeRefreshLayout.isRefreshing() && newProgress == 100) {
                        swipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }

                    if (!swipeRefreshLayout.isRefreshing() && newProgress != 100) {
                        swipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(true);
                            }
                        });
                    }
                }

                if (newProgress == 100){
                    newProgress = 0;
                    progressBar.setVisibility(View.INVISIBLE);
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                }

                progressBar.setProgress(newProgress);

            }

            @Override
            public void onReceivedTitle(WebView view, String mTitle) {
                super.onReceivedTitle(view, mTitle);
                if(StringUtils.isEmpty(title)&&!StringUtils.isEmpty(mTitle)){
                    setTitle(title);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.endsWith(".mp4")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "video/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                    // If we return true, onPageStarted, onPageFinished won't be called.
                    return true;
                } else if (url.startsWith("tel:") || url.startsWith("sms:") || url.startsWith("smsto:") || url.startsWith("mms:") || url.startsWith("mmsto:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                    // If we return true, onPageStarted, onPageFinished won't be called.
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (url.contains(".apk")) {
                    //通过uri与Intent来调用系统通知，查看进度
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    //	new DownloadThread(url).start();
                }
            }
        });
        ZhpJsPlus.addPlusToWeb(webView);
        //注入js插件
        if(jsPlus!=null&&jsPlus.size()>0){
            for(IJsPlus iJsPlus:jsPlus){
                webView.addJavascriptInterface(iJsPlus.getPlusObject(),iJsPlus.getPlusName());
            }
        }
        webView.loadUrl(url);

    }


    private void initViews(){
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.web_swipeRefreshLayout);
        webView = (WebView) findViewById(R.id.web_webv);
        progressBar = (ProgressBar) findViewById(R.id.web_load_progress);
    }

    @Override
    protected void onDestroy() {
        webView.loadUrl("about:blank");//显示空白页
        super.onDestroy();
    }

    public static Intent createIntent(Context context, String url, String title) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(ARG_URL, url);
        intent.putExtra(ARG_TITLE, title);
        return intent;
    }

    public static Intent createIntent(Context context, String url, String title, ArrayList<IJsPlus> plus) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(ARG_URL, url);
        intent.putExtra(ARG_TITLE, title);
        intent.putExtra(ARG_PLUS,plus);
        return intent;
    }
    private void restoreFromIntent(Intent intent) {
        this.url = intent.getStringExtra(ARG_URL);
        this.title = intent.getStringExtra(ARG_TITLE);
        this.jsPlus = (ArrayList<IJsPlus>) intent.getSerializableExtra(ARG_PLUS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
