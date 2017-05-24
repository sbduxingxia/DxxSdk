package com.zhp.sdk.webplus;


import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.zhp.sdk.BaseApp;
import com.zhp.sdk.Tip;

/**
 * Created by zhp.dts on 2017/3/4.
 * js调用对象为"ZhpBase"
 * 调用方法有
 * getDeviceId():获取设备imei值
 * getDeviceSdk():获取设备sdk版本
 * canGoBack();是否可以返回
 * goBack();返回上一个页面
 * canGoForward();是否可以向前
 * goForward();向后一操作
 * toast(msg);手机样式提醒，不中断页面
 * html中调用方法
 * ZhpBase.getDeviceSdk();
 *
 */

public class ZhpJsPlus implements IJsPlus {

    public static void addPlusToWeb(WebView webView){
        if(webView!=null){
            ZhpJsPlus jsPlus = new ZhpJsPlus(webView);
            webView.addJavascriptInterface(jsPlus,jsPlus.getPlusName());
        }
    }

    public static void addPlusToWeb(WebView webView, String plusName) {
        if (webView != null) {
            ZhpJsPlus jsPlus = new ZhpJsPlus(webView);
            webView.addJavascriptInterface(jsPlus, plusName);
        }
    }
    protected WebView webView;
    public ZhpJsPlus(WebView obj){
        this.webView =obj;
    }
    @Override
    public String getPlusName() {
        return "ZhpBase";
    }

    @Override
    public Object getPlusObject() {
        return this;
    }
    /**
     * 获取设备imei值
     *@author zhp.dts
     *@time 2017/3/4 13:51
     */
    @JavascriptInterface
    public String getDeviceId(){
        return BaseApp.getInstance().getDeviceConfig().deviceId;
    }
    @JavascriptInterface
    public String getDeviceSdk(){
        return String.valueOf(BaseApp.getInstance().getDeviceConfig().sdkVersion);
    }
    @JavascriptInterface
    public String getVersion() {
        return String.valueOf(BaseApp.getInstance().getDeviceConfig().versionCode);
    }

    @JavascriptInterface
    public boolean canGoBack(){
        if(webView!=null){
            return webView.canGoBack();
        }
        return false;
    }
    @JavascriptInterface
    public void goBack(){
        if(webView!=null){
            webView.goBack();
        }
    }
    @JavascriptInterface
    public boolean canGoForward(){
        if(webView!=null){
           return webView.canGoForward();
        }
        return false;
    }
    @JavascriptInterface
    public void goForward(){
        if(webView!=null){
            webView.goForward();
        }
    }
    @JavascriptInterface
    public void toast(String msg){
        Tip.show(msg);
    }

}
