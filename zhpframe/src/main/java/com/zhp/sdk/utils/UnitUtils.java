package com.zhp.sdk.utils;

import android.content.Context;

/**
 * Created by 01432709 on 2018/6/6.
 */

public class UnitUtils {
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
