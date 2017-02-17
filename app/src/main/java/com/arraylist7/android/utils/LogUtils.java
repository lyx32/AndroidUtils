package com.arraylist7.android.utils;

import android.util.Log;

public class LogUtils {

    private static boolean debug = true;


    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        LogUtils.debug = debug;
    }

    private static String getTag() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        return callerClazzName + "." + caller.getMethodName() + "-L:" + caller.getLineNumber();
    }

    public static void e(String message) {
        e(getTag(), message, null);
    }

    public static void e(String message, Throwable tr) {
        e(getTag(), message, tr);
    }

    public static void e(String tag, String message) {
        e(tag, message,null);
    }

    public static void e(String tag, String message, Throwable tr) {
        if (debug) Log.e(tag, message, tr);
    }

    public static void d(String message) {
        d(getTag(), message, null);
    }

    public static void d(String message, Throwable tr) {
        d(getTag(), message, tr);
    }

    public static void d(String tag, String message, Throwable tr) {
        if (debug) Log.d(tag, message,tr);
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
        if (debug) Log.i(tag, message, tr);
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
        if (debug) Log.v(tag, message, tr);
    }

    /**
     * 保存日志
     * @param message 要保存的内容
     * @param fileAbsolutePath 要保存的文件绝对路径
     */
    public static void file(String message, String fileAbsolutePath) {
        LogUtils.e(message);
        try {
            FileUtils.writeFile(fileAbsolutePath, message+"\n\n", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存日志
     * @param message 要保存的内容
     * @param rootDirctory 要保存的sd卡根目录
     * @param fileName 要保存的文件名
     */
    public static void file(String message, String rootDirctory, String fileName) {
        file(message, CacheUtils.createAppRootDir(rootDirctory)+"/" + fileName);
    }
    /**
     * 保存日志
     * @param message 要保存的内容
     * @param rootDirctory 要保存的sd卡根目录
     * @param dirctory 要保存的二级目录
     * @param fileName 要保存的文件名
     */
    public static void file(String message, String rootDirctory,String dirctory, String fileName) {
        file(message, CacheUtils.createAppOtherDir(rootDirctory, dirctory)+"/" + fileName);
    }
}