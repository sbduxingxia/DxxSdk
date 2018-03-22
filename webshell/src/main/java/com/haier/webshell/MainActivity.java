package com.haier.webshell;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.haier.webshell.service.BaseService;
import com.zhp.sdk.BaseApp;
import com.zhp.sdk.Tip;
import com.zhp.sdk.utils.FileUtils;
import com.zhp.sdk.utils.SharedPreferencesUtils;
import com.zhp.sdk.utils.StringUtils;
import com.zhp.sdk.webplus.IJsPlus;
import com.zhp.sdk.webplus.ZhpJsPlus;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by zhp.dts on 2017/3/4.
 */

public class MainActivity extends Activity {
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_URL = "arg_url";
    private static final String ARG_PLUS = "arg_plus";
    public static final String ARG_UID = "arg_uid";
    public static final String ARG_TERMINAL = "arg_terminal";//时间间隔
    private final static int FILECHOOSER_RESULTCODE = 1;
    private final static int FILESCHOOSER_RESULTCODE = 2;
    private final static String IphoneUserAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1";
    private final static String WeChatUserAgent = "MicroMessenger/5.0.1";
    WebView webView;
    protected String url = "https://bank.wenjuan.com/s2/59ad0fe7c976d857cb4121f8?from=timeline";//http://lb.haier.net/votelist";
           // "http://b2eapp.speedws.com:8090/russiaFactory/factory/login.jsp";// "http://123.103.113.194:8090/russiaFactory/factory/login.jsp";
    //http://123.103.113.194:8090/russiaFactory/factory/login.jsp";//http://lapp.haier.net:8090/russiaFactory/factory/login.jsp";//"file:///android_asset/jsplus.html";//
    String title = null;


    private ValueCallback<Uri> mUploadMessage;

    private ValueCallback<Uri[]> mUploadMessages;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<IJsPlus> jsPlus = new ArrayList<>();
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zp_layout_web);
        initViews();
        if (TextUtils.isEmpty(url)) {
            this.url = (String) SharedPreferencesUtils.getParam(getApplicationContext(), ARG_URL, "");
        }
        if (TextUtils.isEmpty(url)) {
            inputUrlDialog();
        }
        //长按更换地址
//        longClickListener();
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUserAgentString(webSettings.getUserAgentString()+" "+WeChatUserAgent);
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

                if (newProgress == 100) {
                    newProgress = 0;
                    progressBar.setVisibility(View.INVISIBLE);
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

            //-------------------文件上传支持
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
//                CLog.i("UPFILE", "in openFile Uri Callback");
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
//                CLog.i("UPFILE", "in openFile Uri Callback has accept Type" + acceptType);
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                String type = TextUtils.isEmpty(acceptType) ? "*/*" : acceptType;
                i.setType(type);
                startActivityForResult(Intent.createChooser(i, "File Chooser"),
                        FILECHOOSER_RESULTCODE);
            }

            // For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
//                CLog.i("UPFILE", "in openFile Uri Callback has accept Type" + acceptType + "has capture" + capture);
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                String type = TextUtils.isEmpty(acceptType) ? "*/*" : acceptType;
                i.setType(type);
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

            //Android 5.0+
            @Override
            @SuppressLint("NewApi")
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mUploadMessages != null) {
                    mUploadMessages.onReceiveValue(null);
                }
//                CLog.i("UPFILE", "file chooser params：" + fileChooserParams.toString());
                mUploadMessages = filePathCallback;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                if (fileChooserParams != null && fileChooserParams.getAcceptTypes() != null
                        && fileChooserParams.getAcceptTypes().length > 0) {
                    i.setType(fileChooserParams.getAcceptTypes()[0]);
                } else {
                    i.setType("*/*");
                }
                if (TextUtils.isEmpty(i.getType())) {
                    i.setType("*/*");
                }
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILESCHOOSER_RESULTCODE);
                return true;
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
        ZhpJsPlus.addPlusToWeb(webView, "HaierWeb");
        //注入js插件
        if (jsPlus != null && jsPlus.size() > 0) {
            for (IJsPlus iJsPlus : jsPlus) {
                webView.addJavascriptInterface(iJsPlus.getPlusObject(), iJsPlus.getPlusName());
            }
        }
        initLocalJSInterface();
        loadUrl(url+"?imei="+ BaseApp.getInstance().getDeviceConfig().deviceId);
        BaseService.startBaseService(getApplicationContext());
//        testShortCut();
    }

    private void testShortCut() {
        BaseService.startBaseService(getApplicationContext());
        SharedPreferencesUtils.setParam(getApplicationContext(), ARG_UID, "9992");
    }

    private void initLocalJSInterface() {
        if (webView != null) {
            webView.addJavascriptInterface(new HLocalJs(), "HLocalJs");

        }
    }

    class HLocalJs {
        public HLocalJs() {

        }

        @JavascriptInterface
        public boolean saveUid(String uid) {
            SharedPreferencesUtils.setParam(getApplicationContext(), ARG_UID, uid);
//            Tip.show("已将用户ID:"+uid+" 存到本地");
            return true;
        }

        @JavascriptInterface
        public boolean freshUnreadMessage() {
            BaseService.addOneCommand(System.currentTimeMillis());
//            Tip.show("已收到刷新未读消息的请求，请求用户ID:"+SharedPreferencesUtils.getParam(getApplicationContext(), ARG_UID,""));
            return true;
        }
    }
    private void loadUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            webView.loadUrl("about:blank");
        } else {
            SharedPreferencesUtils.setParam(getApplicationContext(), ARG_URL, url);
            if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file:///")) {
                webView.loadUrl(url);
            } else {
                webView.loadUrl("http://" + url);
            }
        }
    }

    private void initViews() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.web_swipeRefreshLayout);
        webView = (WebView) findViewById(R.id.web_webv);
        progressBar = (ProgressBar) findViewById(R.id.web_load_progress);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadUrl("about:blank");//显示空白页
        }
        super.onDestroy();
    }

    public static Intent createIntent(Context context, String url, String title) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(ARG_URL, url);
        intent.putExtra(ARG_TITLE, title);
        return intent;
    }

    public static Intent createIntent(Context context, String url, String title, ArrayList<IJsPlus> plus) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(ARG_URL, url);
        intent.putExtra(ARG_TITLE, title);
        intent.putExtra(ARG_PLUS, plus);
        return intent;
    }

    private void restoreFromIntent(Intent intent) {
        if (intent == null) return;
        if (TextUtils.isEmpty(this.url) && intent.hasExtra(ARG_URL)) {
            this.url = intent.getStringExtra(ARG_URL);
        }
        if (intent.hasExtra(ARG_TITLE))
            this.title = intent.getStringExtra(ARG_TITLE);
        if (intent.hasExtra(ARG_PLUS))
            this.jsPlus = (ArrayList<IJsPlus>) intent.getSerializableExtra(ARG_PLUS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addJsPlus(IJsPlus onePlus) {
        if (this.jsPlus == null) return;
        this.jsPlus.add(onePlus);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result == null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
                return;
            }
//            CLog.i("UPFILE", "onActivityResult" + result.toString());
            String path = FileUtils.getPath(this, result);
            if (TextUtils.isEmpty(path)) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
                return;
            }
            Uri uri = Uri.fromFile(new File(path));
//            CLog.i("UPFILE", "onActivityResult after parser uri:" + uri.toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            } else {
                mUploadMessage.onReceiveValue(uri);
            }

            mUploadMessage = null;
        } else if (requestCode == FILESCHOOSER_RESULTCODE) {
            if (null == mUploadMessages) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result == null) {
                mUploadMessages.onReceiveValue(null);
                mUploadMessages = null;
                return;
            }
//            CLog.i("UPFILE", "onActivityResult" + result.toString());
            String path = FileUtils.getPath(this, result);
            if (TextUtils.isEmpty(path)) {
                mUploadMessages.onReceiveValue(null);
                mUploadMessages = null;
                return;
            }
            Uri uri = Uri.fromFile(new File(path));
//            CLog.i("UPFILE", "onActivityResult after parser uri:" + uri.toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mUploadMessages.onReceiveValue(new Uri[]{uri});
            } else {
            }

            mUploadMessages = null;
        }
    }

    private void inputUrlDialog() {

        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);
        inputServer.setText(url);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("输入加载网址")
                .setView(inputServer).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("修改",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String strUrl = inputServer.getText().toString().trim();
                        setUrl(strUrl);
                        loadUrl(strUrl);
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    private void longClickListener() {
        if (webView == null) return;
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                inputUrlDialog();
                return true;
            }
        });
    }
}
