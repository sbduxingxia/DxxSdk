package com.zhp.sdk.utils;

/**
 * Created by zhp.dts on 2017/3/3.
 */

public class StringUtils {
    /**
     * 判断字符串是否为空
     * isEmpty(null) true
     * isEmpty("") true
     * isEmpty("null") true
     * isEmpty(" ") false
     * isEmpty("abc") false
     *@author zhp.dts
     *@time 2017/3/3 17:17
     */
    public static Boolean isEmpty(String opt){
        if(opt==null||"".equals(opt)||"null".equals(opt)){
            return true;
        }
        return false;
    }

    /**
     * 判断字符是否为空
     * isNotEmpty(null) false
     * isNotEmpty("") false
     * isNotEmpty("null") false
     * isNotEmpty(" ") true
     * isNotEmpty("abc") true
     *
     * @param opt
     * @return
     */
    public static Boolean isNotEmpty(String opt) {
        return !isEmpty(opt);
    }

    /**
     * 判断字符是否为无意义字符串
     * isBlank(null) true
     * isBlank("") true
     * isBlank("null") true
     * isBlank(" ") true
     * isBlank("abc") false
     *
     * @param opt
     * @return
     */
    public static Boolean isBlank(String opt) {
        if (isEmpty(opt) || opt.trim().isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串不是无意义字符串
     * isNotBlank(null) false
     * isNotBlank("") false
     * isNotBlank("null") false
     * isNotBlank(" ") false
     * isNotBlank("abc") true
     *
     * @param opt
     * @return
     */
    public static Boolean isNotBlank(String opt) {
        return !isBlank(opt);
    }

}
