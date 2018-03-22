package com.example.dengtakaoshi;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.zhp.sdk.Tip;
import com.zhp.sdk.loading.ILoadingInfo;
import com.zhp.sdk.loading.ILoadingListener;
import com.zhp.sdk.loading.LoadingManager;
import com.zhp.sdk.utils.HtmlUtils;
import com.zhp.sdk.utils.SharedPreferencesUtils;
import com.zhp.sdk.utils.StringUtils;

public class MainActivity extends AppCompatActivity implements ILoadingInfo {
    private final String TAG = this.getClass().getSimpleName();
    private final static String APP_CACAHE_DIRNAME = "kaoshiCache";
    private WebView webView;
    private final String title  =  "【辅助】灯塔考试";
    private final String login = "https://sso.dtdjzx.gov.cn/sso/login";
    private final String loginSuccess = "https://www.dtdjzx.gov.cn/member";
    private final String url1 = "http://xxjs.dtdjzx.gov.cn/index.html";
    private String addJsStr1 = "";
    private final String url2 = "http://xxjs.dtdjzx.gov.cn/kaishijingsai.html";
    private final String urlAnswer ="answers";
    private String addJsStr2 = "";
    private String addAnswers = "";
    private final String update1 = "lastUpdate1";
    private final String update2 = "lastUpdate2";
    private final String updateAnswer = "lastUpdateAnswer";
    private long maxUpdateTime = 60*60*1000;
    ProgressBar progressBar;
    private Button autoAnswer,getAnswer;
    private LinearLayout bottomlnly;
    private long startTime=System.currentTimeMillis();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.kaoshi_web);
        progressBar = (ProgressBar) findViewById(R.id.web_load_progress);
        autoAnswer = (Button) findViewById(R.id.auto_answer);
        getAnswer = (Button) findViewById(R.id.auto_get_answer);
        bottomlnly = (LinearLayout) findViewById(R.id.bottom_lnly);
        addJsStr1 = (String) SharedPreferencesUtils.getParam(getApplicationContext(),url1,"");
        addJsStr2 = (String) SharedPreferencesUtils.getParam(getApplicationContext(),url2,"");
        addAnswers = (String) SharedPreferencesUtils.getParam(getApplicationContext(),urlAnswer,"");
        initLoad();
        initWebSetting();
        initAutoAnswer();
        getJsStr1(false);
        getJsStr2(false);
        getJsAnswerStr(false);
//        getJsAnswerStr();
        webView.loadUrl(url1);
        bottomlnly.setVisibility(View.GONE);
    }
    private void initWebSetting(){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheMaxSize(1024*1024*8);
        String cacheDirPath = getFilesDir().getAbsolutePath();
        //设置数据库缓存路径
        webSettings.setDatabaseEnabled(true);
        webSettings.setDatabasePath(cacheDirPath);
        //开启 Application Caches 功能
        webSettings.setAppCachePath(cacheDirPath);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

                return super.onJsAlert(view, title, message, result);
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

                /*if (swipeRefreshLayout != null) {
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
                }*/

                if (newProgress == 100) {
                    LoadingManager.instance().hide();
                    newProgress = 0;
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.i(TAG,"webView.getUrl()="+webView.getUrl());
                    if(webView.getUrl().startsWith(url1)) {
                        getJsStr1(true);
                    }else if(webView.getUrl().startsWith(url2)) {
                        getJsAnswerStr(true);
                        startTime = System.currentTimeMillis();
                    }
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }

                progressBar.setProgress(newProgress);

            }

            @Override
            public void onReceivedTitle(WebView view, String mTitle) {
                super.onReceivedTitle(view, mTitle);
                if (StringUtils.isEmpty(title) && !StringUtils.isEmpty(mTitle)) {
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
                } else if (url.endsWith(".apk")) {
                    //通过uri与Intent来调用系统通知，查看进度
                    /*Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);*/
                    return false;
                    //	new DownloadThread(url).start();
                }else if(url.startsWith(loginSuccess)){
                    LoadingManager.instance().show(MainActivity.this);
                    return super.shouldOverrideUrlLoading(view, url2);
                }else if(url.startsWith(url1)){
                    bottomlnly.setVisibility(View.GONE);
                }else if(url.startsWith(url2)){
                    bottomlnly.setVisibility(View.VISIBLE);
                }else{
                    bottomlnly.setVisibility(View.GONE);
                    Log.e(TAG,"override-url:"+url);
//                    webView.clearCache(true);
//                    webView.clearHistory();
                }
                LoadingManager.instance().show(MainActivity.this);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        /*if(swipeRefreshLayout!=null){
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    webView.reload();
                }
            });
        }*/


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
    }
    private void initAutoAnswer(){
        autoAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webView.getUrl().startsWith(url2)){
                    if(System.currentTimeMillis()-startTime<30*1000){
                        Tip.show("时间太短不支持自动提交");
                    }else if(TextUtils.isEmpty(addAnswers)||TextUtils.isEmpty(addJsStr2)){
                        Tip.show("请重新获取答案");
                    }else{
                        loadJsData("javascript:autoAnswer()");
                    }
                }else{
                    Tip.show("该界面不支持自动答题");
                }
            }
        });
        getAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJsAnswerStr(true);

            }
        });
    }
    private void initLoad(){
        LoadingManager.instance().bind(new ILoadingListener() {
            @Override
            public void loadingStart() {
                Log.d(TAG, "iLoadingListener -> loadingStart");
            }

            @Override
            public void loadingEnd() {
                Log.d(TAG, "iLoadingListener -> loadingEnd");
            }
        });
    }
    //答题首页
    private void getJsStr1(final boolean isRun){
        if(!TextUtils.isEmpty(addJsStr1)&&System.currentTimeMillis()-(Long)SharedPreferencesUtils.getParam(getApplicationContext(),update1,0l)<maxUpdateTime){//更新
            if(isRun){
                loadJsData("javascript:"+addJsStr1);
            }
            return;
        }
        final String getJsUrl = "https://note.youdao.com/yws/public/note/b5bee0f37da83223308a5a88112ef39e?editorType=0&cstk=4nvuzE-E";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getJsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject obj  = JSON.parseObject(response);
                String addJsStr = HtmlUtils.html2Text(obj.getString("content"));
                Log.i(TAG, addJsStr);
                SharedPreferencesUtils.setParam(getApplicationContext(),update1,System.currentTimeMillis());
                SharedPreferencesUtils.setParam(getApplicationContext(),url1,addJsStr);
                addJsStr1 = addJsStr;
                if(isRun){
                    loadJsData("javascript:"+addJsStr);
                }

            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    //自动答题
    private void getJsStr2(final boolean isRun){
        if(!TextUtils.isEmpty(addJsStr2)&&System.currentTimeMillis()-(Long)SharedPreferencesUtils.getParam(getApplicationContext(),update2,0l)<maxUpdateTime){//更新
            if(isRun){
                loadJsData("javascript:function autoAnswer(){"+addJsStr2+"};");
            }
            return;
        }
        String getJsUrl = "https://note.youdao.com/yws/public/note/78032f9cb68ebcc9459a199986abb95e?editorType=0&cstk=4nvuzE-E";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getJsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject obj  = JSON.parseObject(response);
                String addJsStr = HtmlUtils.html2Text(obj.getString("content"));
                Log.i(TAG, addJsStr);
                SharedPreferencesUtils.setParam(getApplicationContext(),update2,System.currentTimeMillis());
                SharedPreferencesUtils.setParam(getApplicationContext(),url2,addJsStr);
                addJsStr2 = addJsStr;
                if(isRun){
                    loadJsData("javascript:function autoAnswer(){"+addJsStr2+"};");
                }

            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    //填充答案
    private void getJsAnswerStr(final boolean isRun){
        if(!TextUtils.isEmpty(addAnswers)&&System.currentTimeMillis()-(Long)SharedPreferencesUtils.getParam(getApplicationContext(),updateAnswer,0l)<maxUpdateTime){//更新
            if(isRun){
                loadJsData("javascript:"+addAnswers);
                getJsStr2(isRun);
            }
            return;
        }
        getAnswer.setText("正在获取答案");
        String getJsUrl = "https://note.youdao.com/yws/public/note/30d76e281f4c260effe3b498bc390cc8?editorType=0&cstk=4nvuzE-E";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getJsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject obj  = JSON.parseObject(response);
                String addJsStr = HtmlUtils.html2Text(obj.getString("content"));
                Log.i(TAG, addJsStr);
                SharedPreferencesUtils.setParam(getApplicationContext(),updateAnswer,System.currentTimeMillis());
                SharedPreferencesUtils.setParam(getApplicationContext(),urlAnswer,addJsStr);
                addAnswers = addJsStr;
                getAnswer.setText("获取答案");
                Tip.show("答案获取成功");
                if(isRun){
                    loadJsData("javascript:"+addAnswers);
                    getJsStr2(isRun);
                }

            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    /**
     * 监听Back键按下事件,方法1:
     * 注意:
     * super.onBackPressed()会自动调用finish()方法,关闭
     * 当前Activity.
     * 若要屏蔽Back键盘,注释该行代码即可
     */
    @Override
    public void onBackPressed() {
        if(webView.getUrl().startsWith(url2)){
            Tip.show("正在答题，无法退出");
            return;
        }
        backNum++;
        if(backNum>=2){
            super.onBackPressed();
            return;
        }
        Tip.show("再单击一次，返回");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000*10);
                    backNum=0;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private int backNum=0;
    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(webView.getUrl().startsWith(url2)){
                Tip.show("正在答题，无法退出");
                return true;
            }
            backNum++;
            if(backNum>=2){
                return super.onKeyDown(keyCode, event);
            }
            Tip.show("再单击一次，返回");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000*10);
                        backNum=0;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void loadJsData(String jsStr){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(jsStr, null);
        } else {
            webView.loadUrl(jsStr);
//            webView.reload();
        }
    }
    @Override
    public Activity getActivity() {
        return MainActivity.this;
    }

    @Override
    public boolean isBackPressClose() {
        return false;
    }

    @Override
    public String getLoadingTips() {
        return "加载中";
    }
    public void addLocalStorage(String key,String value){
        loadJsData("javascript:localStorage.setItem('"+key+"','"+value+"');");
    }
    public void getLocalStorage(String key){
        loadJsData("javascript:alert(localStorage.getItem('"+key+"'));");
    }
}
