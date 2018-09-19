package com.arraylist7.android.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

public final class ThrowableUtils {

    private ThrowableUtils() {
    }

    public static void setDefaultExceptionHandler(@NonNull final Context context){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                synchronized (context) {
                    throwable.printStackTrace();
                    String appName = DeviceUtils.appName(context);
                    ThrowableUtils.saveThrowable(context, appName, "log", throwable);
                }
            }
        });
    }

    public static void saveThrowable(Context context, String dirctory, String logDir, Throwable ex) {
        File logFile = new File(CacheUtils.createAppOtherDir(dirctory, logDir), StringUtils.getDateTimeNow("yyyy-MM-dd") + ".log");
        if (!logFile.getParentFile().exists()) {
            logFile.getParentFile().mkdirs();
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(logFile, true);
            fw.write("\n---------------------" + StringUtils.getDateTimeNow("yyyy-MM-dd HH:mm:ss") + "------------------------\n");
            fw.write(ex.getLocalizedMessage());
            fw.write("\n#########################################################\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fw);
        }
    }
}
