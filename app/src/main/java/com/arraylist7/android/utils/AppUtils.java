package com.arraylist7.android.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.arraylist7.android.utils.listener.ActivityLifecycleAdapter;

public final class AppUtils {

    AppUtils() {
    }


    public static void init(@NonNull final Application app, final ActivityLifecycleAdapter adapter) {

       ThrowableUtils.setDefaultExceptionHandler(app.getApplicationContext());

        final ActivityLifecycleAdapter finalAdapter = null == adapter ? new ActivityLifecycleAdapter() : adapter;
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                LogUtils.d(activity.getComponentName().toShortString() + " onCreate");
                ViewUtils.inject(activity);
                finalAdapter.onActivityCreated(activity, savedInstanceState);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                LogUtils.d(activity.getComponentName().toShortString() + " onStart");
                finalAdapter.onActivityStarted(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                LogUtils.d(activity.getComponentName().toShortString() + " onResume");
                finalAdapter.onActivityResumed(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                LogUtils.d(activity.getComponentName().toShortString() + " onPause");
                finalAdapter.onActivityPaused(activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                LogUtils.d(activity.getComponentName().toShortString() + " onStop");
                finalAdapter.onActivityStopped(activity);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                LogUtils.d(activity.getComponentName().toShortString() + " onSaveInstanceState");
                finalAdapter.onActivitySaveInstanceState(activity, outState);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                LogUtils.d(activity.getComponentName().toShortString() + " onDestroy");
                finalAdapter.onActivityDestroyed(activity);
            }
        });
    }

    public static void log(Context context, String msg) {
        LogUtils.file(StringUtils.getDateTimeNow("yyyy-MM-dd HH:mm:ss") + " # " + msg, DeviceUtils.appName(context), "log", StringUtils.getDateTimeNow("yyyy-MM") + ".log");
    }
}
