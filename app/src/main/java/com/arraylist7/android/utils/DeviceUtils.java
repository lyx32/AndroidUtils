package com.arraylist7.android.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
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
                                serial =  ClassUtils.getValue(Build.class,null,"SERIAL").toString();
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
                serial =  ClassUtils.getValue(Build.class,null,"SERIAL").toString();
                return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
            } catch (Exception e) {
                serial = "serial";
            }
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        }
        return deviceId;
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
                    String path1 = CacheUtils.createAppOtherDir(".#%/\\au", ".*%^~a");
                    String path2 = CacheUtils.createAppOtherDir("au", "uuid");
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


    /**
     * 判断是否是刘海屏
     *
     * @return
     */
    public static boolean hasNotchScreen(Context context) {
        if (getInt("ro.miui.notch", context) == 1 || hasNotchAtHuawei(context) || hasNotchAtOPPO(context) || hasNotchAtVivo(context)) {
            return true;
        }
        return false;
    }

    /**
     * 小米刘海屏判断.
     *
     * @return 0 if it is not notch ; return 1 means notch
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    private static int getInt(String key, Context context) {
        int result = 0;
        if (isXiaomi()) {
            try {
                ClassLoader classLoader = context.getClassLoader();
                Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
                Class[] paramTypes = new Class[2];
                paramTypes[0] = String.class;
                paramTypes[1] = int.class;
                Method getInt = SystemProperties.getMethod("getInt", paramTypes);
                Object[] params = new Object[2];
                params[0] = new String(key);
                params[1] = new Integer(0);
                result = (Integer) getInt.invoke(SystemProperties, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 是否是小米手机
    private static boolean isXiaomi() {
        return "Xiaomi".equals(Build.MANUFACTURER);
    }

    /**
     * 华为刘海屏判断
     *
     * @return
     */
    private static boolean hasNotchAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (Exception e) {
        } finally {
            return ret;
        }
    }

    private static final int VIVO_NOTCH = 0x00000020;//是否有刘海
    private static final int VIVO_FILLET = 0x00000008;//是否有圆角

    /**
     * VIVO刘海屏判断
     *
     * @return
     */
    private static boolean hasNotchAtVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
        } catch (Exception e) {
        } finally {
            return ret;
        }
    }

    /**
     * OPPO刘海屏判断
     *
     * @return
     */
    private static boolean hasNotchAtOPPO(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

}
