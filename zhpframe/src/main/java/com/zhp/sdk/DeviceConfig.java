package com.zhp.sdk;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhp.dts on 2017/3/4.
 */

public class DeviceConfig implements IConstKey {
    public String deviceId;
    public String deviceSoftwareVersion;
    public String networkOperatorName;
    public final String versionName;
    public final int versionCode;
    public final int sdkVersion;
    public final String model = android.os.Build.MODEL;
    boolean isInited = false;
    public Map<String, String> mFixedParams = new HashMap<String, String>();

    public DeviceConfig(Application application) {


        PackageInfo pkgInfo = null;
        try {
            pkgInfo = application.getPackageManager().getPackageInfo(application.getPackageName(), PackageManager.GET_CONFIGURATIONS);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        versionName = pkgInfo.versionName;
        versionCode = pkgInfo.versionCode;
        sdkVersion = Build.VERSION.SDK_INT;
        mFixedParams.put(DEVICE_SYSTEM_VERSION, deviceSoftwareVersion);
        mFixedParams.put(APP_VERSION, versionName);
        mFixedParams.put(APP_VERSION_CODE, versionCode + "");
        mFixedParams.put(DEVICE_IMEI, deviceId);
        mFixedParams.put(SDK_VERSION, sdkVersion + "");

    }
    public void initDeivceConfig() {
        if (isInited) {
            return;
        }

        try {
            TelephonyManager tm = (TelephonyManager) BaseApp.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            StringBuilder sb = new StringBuilder();
            deviceId = tm.getDeviceId();
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = "Haier" + System.currentTimeMillis();
            }
            deviceSoftwareVersion = tm.getDeviceSoftwareVersion();
            networkOperatorName = tm.getNetworkOperatorName();
            isInited = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
