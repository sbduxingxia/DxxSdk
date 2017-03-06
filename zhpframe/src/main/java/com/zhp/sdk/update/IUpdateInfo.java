package com.zhp.sdk.update;

import android.content.Context;

import com.zhp.sdk.IDialogBaseInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 升级基础信息接口
 * Created by zhp on 2016/11/26.
 */

public interface IUpdateInfo extends IDialogBaseInfo {
//    String id;//升级信息id
//    List<String> downUrls;//可选下载地址
//    String appVersion;//当前最新版本
//    Integer appCode;//当前最新版本代码编号
//    List<String> hosts;
//    Long lastPublishTime;//最后发布的时间戳
//    boolean isForceUpdate;//是否强制升级
//    Integer minCode;//最小可不升级的版本号

    String getId();
    String getDownUrls();//
    String getAppVersion();
    Integer getAppCode();
    ArrayList<String> getHosts();
    Long getLastPublishTime();
    boolean isForceUpdate();
    Integer getMinCode();
    String getTitle();
    String getContentTips();
    String getAppName();
}
