package com.zhp.sdk.update;

/**
 * 升级信息回调
 * Created by zhp on 2016/11/26.
 */

public interface IUpdateListener {
    void updateSuccess();
    void updateError(int code,String msg);
    void updateDestroy();
}
