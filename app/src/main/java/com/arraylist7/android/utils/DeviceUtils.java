package com.arraylist7.android.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class DeviceUtils {
	
	DeviceUtils() {
	}

	/**
	 * 得到手机型号 如 X909，i2899
	 */
	public static String getPhoneModel() {
		return android.os.Build.MODEL;
	}

	/**
	 * 得到手机品牌 coolpad
	 */
	public static String getPhoneBrand() {
		return android.os.Build.BRAND;
	}

	/**
	 * 得到手机生产商 如（Samsung，OPPO，）
	 */
	public static String getPhoneMade() {
		return android.os.Build.MANUFACTURER;
	}

	/**
	 * 得到android当前系统版本级别
	 */
	public static int getSDKLevel() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/**
	 * 得到手机android系统版本
	 */
	public static String getSystemVersion() {
		return android.os.Build.VERSION.RELEASE;
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
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getSubscriberId();
		if (StringUtils.isNullOrEmpty(deviceId)) {
			deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
			if (StringUtils.isNullOrEmpty(deviceId)) {
				deviceId = tm.getDeviceId();
				if (StringUtils.isNullOrEmpty(deviceId)) {
					deviceId = tm.getLine1Number();
					if(StringUtils.isNullOrEmpty(deviceId)){
						deviceId = getMacAddress(context);
					}
				}
			}
		}
		return deviceId;
	}

	public static String getMacAddress(Context context){
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if(null == wifiInfo || null == wifiInfo.getMacAddress()){
			return "";
		}
		return wifiInfo.getMacAddress().replaceAll(":", "");
	}
}
