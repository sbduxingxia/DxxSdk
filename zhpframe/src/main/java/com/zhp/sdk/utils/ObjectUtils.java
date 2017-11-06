package com.zhp.sdk.utils;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 类转其他类型
 * Created by 01432709 on 2017/9/5.
 */

public class ObjectUtils {

    /**
     * 将javabean实体类转为map类型，然后返回一个map类型的值
     * 参数为负值或者为空时，不会作为参数返回
     */
    public static Map<String, Object> object2Map(Object thisObj) {
        if (thisObj == null) {
            return null;
        }
        Map map = new HashMap();
        Class c;
        try {
            c = Class.forName(thisObj.getClass().getName());
            Method[] m = c.getMethods();
            for (int i = 0; i < m.length; i++) {
                String method = m[i].getName();
                if (method.startsWith("get")) {
                    try {
                        Object value = m[i].invoke(thisObj);
                        if (value != null) {
                            String key = method.substring(3);
                            key = key.substring(0, 1).toLowerCase() + key.substring(1);
                            map.put(key, value);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        System.out.println("error:" + method);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 将java=实体类转为fastJSON类型，然后返回一个map类型的值
     * 参数为负值或者为空时，不会作为参数返回
     */
    public static JSONObject object2Json(Object thisObj) {
        JSONObject obj = new JSONObject();
        Class c;
        try {
            c = Class.forName(thisObj.getClass().getName());

            Method[] m = c.getMethods();
            for (int i = 0; i < m.length; i++) {
                String method = m[i].getName();
                if (method.startsWith("get")) {
                    try {
                        Object value = m[i].invoke(thisObj);
                        if (value != null) {
                            String key = method.substring(3);
                            key = key.substring(0, 1).toLowerCase() + key.substring(1);
                            obj.put(key, value);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        System.out.println("error:" + method);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 类转json字符串
     */
    public static String object2JsonString(Object obj) {

        return JSONObject.toJSONString(obj);
    }
}
