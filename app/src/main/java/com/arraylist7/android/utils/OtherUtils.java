package com.arraylist7.android.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static android.content.pm.PackageManager.GET_ACTIVITIES;
import static android.content.pm.PackageManager.NameNotFoundException;

/**
 * 功能：一些其他工具包<br>
 * 时间：2015年10月26日<br>
 * 备注：<br>
 *
 * @author ke
 */
public final class OtherUtils {
    OtherUtils() {
    }


    public static PackageInfo getPackageInfo(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            return packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getAppVersionName(Context context, String defaultVal) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return defaultVal;
    }

    /**
     * 当前应用程序的名称
     */
    public static String getAppName(Context context) {
        String appName = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appName = info.applicationInfo.loadLabel(context.getPackageManager()).toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }


    public static int getAppVersionCode(Context context, int defaultVal) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return defaultVal;
    }


    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    LogUtils.i(String.format("Background App:", appProcess.processName));
                    return true;
                } else {
                    LogUtils.i(String.format("Foreground App:", appProcess.processName));
                    return false;
                }
            }
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public static void vibrate(Context context, long milliseconds) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(Manifest.permission.VIBRATE)) {
                Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                vib.vibrate(milliseconds);
            }
        }
    }

    /**
     * 兼容各个版本的安装app方法，需要在AndroidManifest.xml添加REQUEST_INSTALL_PACKAGES权限，REQUEST_INSTALL_PACKAGES权限不能通过动态判断
     *
     * @param context
     * @param file
     */
    public static void install(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void uninstall(Context context, String packageName) {
        if (isInstall(context, packageName)) {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            context.startActivity(uninstallIntent);
        }
    }

    public static boolean isInstall(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> packs = packageManager.getInstalledApplications(GET_ACTIVITIES);
        for (ApplicationInfo info : packs) {
            if (info.packageName.equals(packageName))
                return true;
        }
        return false;
    }

    public static Drawable getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static void hideKeyboard(Activity activity) {
        if (null != activity) {
            InputMethodManager im = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showKeyboard(Activity activity) {
        if (null != activity) {
            InputMethodManager im = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED);
        }
    }

    public static void hideKeyboard(Context context, View view) {
        if (null != view) {
            IBinder iBinder = view.getWindowToken();
            if (null != iBinder) {
                InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static void showKeyboard(Context context, View view) {
        if (null != view) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }


    public static void setFocus(View view) {
        if (null != view) {
            view.setFocusableInTouchMode(true);
            view.requestFocus();
        }
    }

    public static void setFocusAndSelectAll(EditText view) {
        if (null != view) {
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.setSelectAllOnFocus(true);
            view.selectAll();
        }
    }

    /**
     * 获取app签名md5值
     */
    public static String getAppSign(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(sign.toByteArray());
            byte[] byteArray = messageDigest.digest();
            StringBuffer sgin = new StringBuffer();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    sgin.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    sgin.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
            return sgin.toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
