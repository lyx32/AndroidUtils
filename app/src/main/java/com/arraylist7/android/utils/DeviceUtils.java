package com.arraylist7.android.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DeviceUtils {

    DeviceUtils() {
    }

    /**
     * 得到手机型号 如 X909，i2899
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 得到手机品牌 coolpad
     */
    public static String getPhoneBrand() {
        return Build.BRAND;
    }

    /**
     * 得到手机生产商 如（Samsung，OPPO，）
     */
    public static String getPhoneMade() {
        return Build.MANUFACTURER;
    }

    /**
     * 得到android当前系统版本级别
     */
    public static int getSDKLevel() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 得到手机android系统版本
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 当前应用程序的名称
     */
    public static String appName(Context context) {
        String appName = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appName = info.applicationInfo.loadLabel(context.getPackageManager()).toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }

    /**
     * 当前应用程序的版本号
     */
    public static String appVerName(Context context) {
        String appVerName = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVerName = info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVerName;
    }

    /**
     * 当前应用程序的版本号
     */
    public static int appVerCode(Context context) {
        int appVerCode = 0;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVerCode = info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVerCode;
    }


    /**
     * 得到手机唯一设备号
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        String deviceId = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getSubscriberId();
            if (StringUtils.isNullOrEmpty(deviceId)) {
                deviceId = tm.getDeviceId();
                if (StringUtils.isNullOrEmpty(deviceId)) {
                    deviceId = tm.getLine1Number();
                    if (StringUtils.isNullOrEmpty(deviceId)) {
                        deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
                        if ("9774d56d682e549c".equalsIgnoreCase(deviceId)) {
                            String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
                            String serial = "";
                            try {
                                serial = Build.class.getField("SERIAL").get(null).toString();
                                return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
                            } catch (Exception e) {
                                serial = "serial";
                            }
                            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
                        }
                    }
                }
            }
        } catch (Exception ee) {
            String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
            String serial = "";
            try {
                serial = Build.class.getField("SERIAL").get(null).toString();
                return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
            } catch (Exception e) {
                serial = "serial";
            }
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        }
        return deviceId;
    }


    /**
     * 得到手机唯一设备号
     *
     * @param context
     * @return
     */
    public static String getAllDeviceId(Context context) {
        StringBuffer str = new StringBuffer();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getSubscriberId();
        str.append(deviceId + "|");
        deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        str.append(deviceId + "|");
        deviceId = tm.getDeviceId();
        str.append(deviceId + "|");
        while (str.toString().endsWith("|")) {
            str.setLength(str.toString().length() - 1);
        }
        return str.toString();
    }

    /**
     * 获取标识手机的唯一序列号
     *
     * @param context
     * @return
     */
    public static String getPhoneDeviceId(Context context) {
        String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        if (StringUtils.isNullOrEmpty(deviceId)) {
            if (StringUtils.isNullOrEmpty(deviceId)) {
                if (StringUtils.isNullOrEmpty(deviceId)) {
                    String path1 = CacheUtils.createAppOtherDir(".#%/\\hqls", ".*%^~a");
                    String path2 = CacheUtils.createAppOtherDir("hqls", "uuid");
                    File deviceFile = new File(path1, "device");
                    if (!deviceFile.getParentFile().exists()) {
                        if (!deviceFile.mkdirs()) {
                            deviceFile = new File(path2, "device");
                            if (!deviceFile.getParentFile().exists()) {
                                deviceFile.mkdirs();
                            }
                        }
                    }
                    if (!deviceFile.exists()) {
                        deviceId = UUID.randomUUID().toString().replace("-", "");
                        try {
                            FileUtils.writeFile(deviceFile.getAbsolutePath(), deviceId, "UTF-8");
                        } catch (Exception e) {
                            LogUtils.e("创建唯一序列号UUID失败", e);
                        }
                    } else {
                        try {
                            deviceId = FileUtils.readerFile(deviceFile.getAbsolutePath());
                        } catch (IOException e) {
                            LogUtils.e("创建唯一序列号UUID失败", e);
                        }
                    }
                }
            }
        }
        return deviceId;
    }
}
