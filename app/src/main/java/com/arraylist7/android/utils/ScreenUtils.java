package com.arraylist7.android.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.arraylist7.android.utils.listener.ActivityLifecycleAdapter;

public final class ScreenUtils {
    ScreenUtils() {
    }

    private static float appDensity;
    private static float appScaledDensity;
    private static DisplayMetrics appDisplayMetrics;
    /**
     * 用来参照的的width
     */
    private static float WIDTH;


    /**
     * 完全今日头条适配方案<br/>
     * https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA
     * @param activity activity
     * @param inches 设计图英寸
     */
    public static void initAdaptScreen(Activity activity, float inches) {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = activity.getApplication().getResources().getDisplayMetrics();
        final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
        float dpi = (float)Math.ceil(Math.sqrt(Math.pow(systemDm.widthPixels, 2) + Math.pow(systemDm.heightPixels, 2)) / inches);
        if ((activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)) {
            activityDm.density = activityDm.widthPixels / dpi;
        } else {
            activityDm.density = activityDm.heightPixels / dpi;
        }
        activityDm.scaledDensity = activityDm.density * (systemDm.scaledDensity / systemDm.density);
        activityDm.densityDpi = (int) (160 * activityDm.density + 0.5);
        appDm.density = activityDm.density;
        appDm.scaledDensity = activityDm.scaledDensity;
        appDm.densityDpi = activityDm.densityDpi;
    }

    /**
     * Cancel adapt the screen.
     *
     * @param activity The activity.
     */
    public static void cancelAdaptScreen(final Activity activity) {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = activity.getApplication().getResources().getDisplayMetrics();
        final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
        activityDm.density = systemDm.density;
        activityDm.scaledDensity = systemDm.scaledDensity;
        activityDm.densityDpi = systemDm.densityDpi;
        appDm.density = systemDm.density;
        appDm.scaledDensity = systemDm.scaledDensity;
        appDm.densityDpi = systemDm.densityDpi;
    }

    /**
     * Return whether adapt screen.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAdaptScreen(Activity activity) {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = activity.getApplication().getResources().getDisplayMetrics();
        return systemDm.density != appDm.density;
    }


    /**
     * 使用今日头条+蓝湖方式设置适配<br/>
     * https://note.youdao.com/share/?id=66cb07073329d19245b6e3aea9595269&type=note#/
     *
     * @param application
     * @param width       设计图的宽度换算成dp（
     *                    Android 	单位：dp
     *                    MDPI    	1px=1dp
     *                    HDPI    	1.5px=1dp
     *                    XHDPI   	2px=1dp
     *                    XXHDPI  	3px=1dp
     *                    XXXHDPI 	4px=1dp）
     */
    public static void init(@NonNull final Application application, float width) {
        appDisplayMetrics = application.getResources().getDisplayMetrics();
        WIDTH = width;
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleAdapter(){
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                setAppOrientation(activity);
            }
        });
        if (appDensity == 0) {
            appDensity = appDisplayMetrics.density;
            appScaledDensity = appDisplayMetrics.scaledDensity;
            //添加字体变化的监听
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    //字体改变后,将appScaledDensity重新赋值
                    if (newConfig != null && newConfig.fontScale > 0) {
                        appScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {
                }
            });
        }
    }

    private static void setAppOrientation(@Nullable Activity activity) {
        float targetDensity = 0;
        try {
            targetDensity = appDisplayMetrics.widthPixels / WIDTH;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        float targetScaledDensity = targetDensity * (appScaledDensity / appDensity);
        int targetDensityDpi = (int) (160 * targetDensity);
        /**
         *
         * 最后在这里将修改过后的值赋给系统参数
         *
         * 只修改Activity的density值
         */
        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }

}
