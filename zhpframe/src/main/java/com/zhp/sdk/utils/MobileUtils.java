package com.zhp.sdk.utils;

import android.content.Intent;
import android.net.Uri;

import com.zhp.sdk.BaseApp;

/**
 * 手机自带功能
 * Created by zhp.dts on 2017/3/3.
 */

public class MobileUtils {
    public static boolean call(String number){
        if(StringUtils.isEmpty(number)) return false;
        Intent callIntent = new Intent(Intent.ACTION_CALL);//直接拨打电话
//        Intent callIntent = new Intent(Intent.ACTION_DIAL);//调用拨号页面
        callIntent.setData(Uri.parse("tel:"+number));
        //防止权限没有
        try {
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            BaseApp.getInstance().startActivity(callIntent);
        }catch(Exception e){
            return false;
        }
        return true;
    }

}
