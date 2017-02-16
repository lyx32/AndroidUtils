package com.arraylist7.android.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public final class ThrowableUtils {

    private ThrowableUtils() {
    }

    public static void saveThrowable(Context context, Throwable ex) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String appName = (String) packageManager.getApplicationLabel(applicationInfo);
        saveThrowable(context, appName, "Log", ex);
    }

    public static void saveThrowable(Context context, String dirctory, String logDir, Throwable ex) {
        String result = getThrowableString(ex);
        File logFile = new File(CacheUtils.createAppOtherDir(dirctory, logDir), StringUtils.getDateTimeNow("yyyy-MM-dd") + ".log");
        if (!logFile.getParentFile().exists()) {
            logFile.getParentFile().mkdirs();
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(logFile, true);
            fw.write("\n---------------------" + StringUtils.getDateTimeNow("yyyy-MM-dd HH:mm:ss") + "------------------------\n");
            fw.write(result);
            fw.write("\n#########################################################\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fw);
        }
    }

    public static String getThrowableString(Throwable ex) {
        Writer writer = null;
        PrintWriter printWriter = null;
        try {
            writer = new StringWriter();
            printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
        } catch (Throwable ee) {
            ee.printStackTrace();
        } finally {
            IOUtils.close(writer);
            IOUtils.close(printWriter);
        }
        if(null != writer)
            return writer.toString();
        return ex.getLocalizedMessage();
    }
}
