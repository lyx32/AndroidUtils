package com.arraylist7.android.utils;

import android.Manifest;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsProvider;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

import com.arraylist7.android.utils.model.SimInfo;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
     * 得到手机唯一设备号（取硬件信息）
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        if ("9774d56d682e549c".equalsIgnoreCase(deviceId)) {
            String m_szDevIDShort = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                    Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                    Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                    Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                    Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                    Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                    Build.USER.length() % 10; //13 位
            String serial = "";
            try {
                if (getSDKLevel() >= 26)
                    serial = ClassUtils.invoke(Build.class, "getSerial").toString();
                else
                    serial = ClassUtils.getValue(Build.class, "SERIAL").toString();
                return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
            } catch (Exception e) {
                serial = "serial";
            }
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        }
        return deviceId;
    }


    /**
     * 读取或写入以为设备码
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getUniqueSign(Context context) {
        String privatePath = CacheUtils.getPrivateDirectory(context);
        String fileName = "deviceId";
        String sign = FileUtils.readerFile(privatePath + File.separatorChar + fileName);
        if (StringUtils.isNullOrEmpty(sign)) {
            sign = getDeviceId(context);
            try {
                FileUtils.writerFile(privatePath, sign);
            } catch (Exception e) {
            }
        }
        return sign;
    }

    /**
     * 获取手机sim卡启用数量(android Q 已经不允许获取sim卡信息)
     *
     * @param context
     * @return
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    public static int getSimCount(Context context) {
        if (Build.VERSION.SDK_INT >= 29)
            return 0;
        int count = 0;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String iccid1 = tm.getSimSerialNumber();
        if (!StringUtils.isNullOrEmpty(iccid1)) {
            count++;
            try {
                String iccid2 = "";
                for (int i = 0; i < 100; i++) {
                    iccid2 = ClassUtils.invoke(tm, "getSimSerialNumber", new Object[]{i});
                    if (!iccid1.equals(iccid2) && !StringUtils.isNullOrEmpty(iccid2)) {
                        count++;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * 获取双卡sim的下标(android Q 已经不允许获取sim卡信息)
     *
     * @return
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    public static int[] getSimIndex(Context context) {
        if (0 == getSimCount(context)) return null;
        int[] indexs = new int[1];
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String back = "";
        for (int i = 0; i < 100; i++) {
            try {
                back = ClassUtils.invoke(tm, "getSimSerialNumber", new Object[]{i});
                if (!StringUtils.isNullOrEmpty(back)) {
                    indexs[0] = i;
                    break;
                }
            } catch (Exception e) {
            }
        }
        for (int i = indexs[0]; i < 100; i++) {
            try {
                String iccid = ClassUtils.invoke(tm, "getSimSerialNumber", new Object[]{i});
                if (!iccid.equals(back) && !StringUtils.isNullOrEmpty(iccid)) {
                    int sim1 = indexs[0];
                    indexs = new int[]{sim1, i};
                    break;
                }
            } catch (Exception e) {
            }
        }
        return indexs;
    }


    /**
     * 获取手机sim卡信息(android Q 已经不允许获取sim卡信息)
     *
     * @return
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    public static SimInfo getSimInfo(Context context) {
        if (Build.VERSION.SDK_INT >= 29)
            return null;
        SimInfo simInfo = new SimInfo();
        int[] indexs = getSimIndex(context);
        if (null == indexs) return null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // 卡1，当前拨号卡
        simInfo.iccid1 = tm.getSimSerialNumber();
        simInfo.imsi1 = tm.getSubscriberId();
        simInfo.line1Number1 = tm.getLine1Number();

        // 5.0以前、只上一张sim卡、只启用一张sim卡，默认拨号上网都是这张sim卡。
        if (getSDKLevel() < 21 || 1 == indexs.length) {
            simInfo.networkOperator1 = tm.getNetworkOperator();
            simInfo.networkOperatorName1 = tm.getNetworkOperatorName();
        } else {
            Class<?> parameterType = getSDKLevel() == 21 ? long.class : int.class;
            for (int i = 0; i < indexs.length; i++) {
                try {
                    String _temp = ClassUtils.invoke(tm, "getSimSerialNumber", parameterType, indexs[i]);
                    // 因为在部分手机上getNetworkOperator取到的是上网sim卡的运营商信息，所以这里要匹配一下取当前拨号sim卡的运营商信息
                    if (simInfo.iccid1.equals(_temp)) {
                        try {
                            simInfo.networkOperator1 = ClassUtils.invoke(tm, "getNetworkOperator", parameterType, indexs[i]);
                        } catch (Exception e) {
                            LogUtils.e("read sim index " + indexs[i] + " getNetworkOperator error", e);
                        }
                        simInfo.networkOperatorName1 = ClassUtils.invoke(tm, "getNetworkOperatorName", parameterType, indexs[i]);
                    } else {
                        simInfo.iccid2 = ClassUtils.invoke(tm, "getSimSerialNumber", parameterType, indexs[i]);
                        simInfo.imsi2 = ClassUtils.invoke(tm, "getSubscriberId", parameterType, indexs[i]);
                        // 5.1,6.0 取消了getLine1Number 和 getNetworkOperator 的(int subId)方法，所以要单独try
                        try {
                            simInfo.line1Number2 = ClassUtils.invoke(tm, "getLine1Number", parameterType, indexs[i]);
                            simInfo.networkOperator2 = ClassUtils.invoke(tm, "getNetworkOperator", parameterType, indexs[i]);
                        } catch (Exception e) {
                            LogUtils.e("read sim index " + indexs[i] + " getLine1Number and getNetworkOperator error", e);
                        }
                        simInfo.networkOperatorName2 = ClassUtils.invoke(tm, "getNetworkOperatorName", parameterType, indexs[i]);
                    }
                } catch (Exception e) {
                    LogUtils.e("read sim index " + indexs[i] + " error", e);
                }
            }
        }
        return simInfo;
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
