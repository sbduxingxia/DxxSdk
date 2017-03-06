package com.zhp.sdk.utils;

/**
 * Created by zhp.dts on 2017/3/3.
 */

public class StringUtils {
    /**
     * 判断字符串是否为空
     *@author zhp.dts
     *@time 2017/3/3 17:17
     */
    public static Boolean isEmpty(String opt){
        if(opt==null||"".equals(opt)||"null".equals(opt)){
            return true;
        }
        return false;
    }
}
