package com.arraylist7.android.utils;

import android.content.Context;
import android.util.Log;

public class LogUtils {

    private static boolean DEBUG = true;
    private static String TAG = "androidUtils.LogUtils";


    public static boolean isDebug() {
        return DEBUG;
    }

    public static void setTAG(String TAG) {
        LogUtils.TAG = TAG;
    }

    public static void setDebug(boolean debug) {
        LogUtils.DEBUG = debug;
    }

    private static String getTag() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        return TAG + " --> " + callerClazzName + "." + caller.getMethodName() + "-L:" + caller.getLineNumber();
    }

    public static void e(String message) {
        e(getTag(), message, null);
    }

    public static void e(String message, Throwable tr) {
        e(getTag(), message, tr);
    }

    public static void e(String tag, String message) {
        e(tag, message, null);
    }

    public static void e(String tag, String message, Throwable tr) {
        if (DEBUG) Log.e(tag, message, tr);
    }

    public static void d(String message) {
        d(getTag(), message, null);
    }

    public static void d(String message, Throwable tr) {
        d(getTag(), message, tr);
    }

    public static void d(String tag, String message) {
        d(getTag(), message, null);
    }

    public static void d(String tag, String message, Throwable tr) {
        if (DEBUG) Log.d(tag, message, tr);
    }

    public static void i(String message) {
        i(getTag(), message, null);
    }

    public static void i(String tag, String message) {
        i(tag, message, null);
    }

    public static void i(String message, Throwable tr) {
        i(getTag(), message, tr);
    }

    public static void i(String tag, String message, Throwable tr) {
        if (DEBUG) Log.i(tag, message, tr);
    }

    public static void v(String message) {
        v(getTag(), message);
    }

    public static void v(String message, Throwable tr) {
        v(getTag(), message, tr);
    }

    public static void v(String tag, String message) {
        v(tag, message, null);
    }

    public static void v(String tag, String message, Throwable tr) {
        if (DEBUG) Log.v(tag, message, tr);
    }

    /**
     * 保存日志
     *
     * @param message          要保存的内容
     * @param fileAbsolutePath 要保存的文件绝对路径
     */
    public static void file(String message, String fileAbsolutePath) {
        LogUtils.e(message);
        try {
            FileUtils.writerFile(fileAbsolutePath, message + "\n\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存日志
     *
     * @param context
     * @param message      要保存的内容
     * @param rootDirctory 要保存的sd卡根目录
     * @param fileName     要保存的文件名
     */
    public static void file(Context context, String message, String rootDirctory, String fileName) {
        file(message, CacheUtils.getPublicFilePath(context,rootDirctory) + "/" + fileName);
    }

    /**
     * 保存日志
     *
     * @param context
     * @param message      要保存的内容
     * @param rootDirctory 要保存的sd卡根目录
     * @param dirctory     要保存的二级目录
     * @param fileName     要保存的文件名
     */
    public static void file(Context context, String message, String rootDirctory, String dirctory, String fileName) {
        file(message, CacheUtils.getPublicFilePath(context,rootDirctory)+"/"+dirctory + "/" + fileName);
    }
}
