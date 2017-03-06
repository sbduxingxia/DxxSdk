package com.zhp.sdk.update;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * 传入数据时继承的接口
 * Created by zhp on 2016/11/26.
 */

public interface ICheckUpdate {
    String getChcekUrl();//检测的url
    Integer getAppCode();//当前app的code
    Integer getCheckOnceTime();//检测一次的事件间隔
}
