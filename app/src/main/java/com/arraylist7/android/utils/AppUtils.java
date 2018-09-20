package com.arraylist7.android.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.arraylist7.android.utils.broadcast.ActivityReceiver;
import com.arraylist7.android.utils.broadcast.BaseBroadcastReceiver;
import com.arraylist7.android.utils.broadcast.NetReceiver;
import com.arraylist7.android.utils.broadcast.ScreenReceiver;
import com.arraylist7.android.utils.inter.INetChange;
import com.arraylist7.android.utils.inter.IOperator;
import com.arraylist7.android.utils.inter.IScreen;
import com.arraylist7.android.utils.listener.ActivityLifecycleAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AppUtils {

    private static Map<String, List<BaseBroadcastReceiver>> activityMap = new HashMap<>();
    private static NetReceiver appNetReceiver = new NetReceiver();

    AppUtils() {
    }

    public static void init(@NonNull Application app) {
        init(app, null);
    }

    public static void init(@NonNull final Application app, final ActivityLifecycleAdapter adapter) {

        ThrowableUtils.setDefaultExceptionHandler(app.getApplicationContext());

        appNetReceiver.registerNetReceiver(app.getApplicationContext(), new INetChange() {
            @Override
            public void onNetChange(NetState state) {
                Collection<List<BaseBroadcastReceiver>> allList = activityMap.values();
                for (List<BaseBroadcastReceiver> list : allList) {
                    for (BaseBroadcastReceiver receiver : list) {
                        if (receiver instanceof INetChange)
                            ((INetChange) receiver).onNetChange(state);
                    }
                }
            }
        });

        final ActivityLifecycleAdapter finalAdapter = null == adapter ? new ActivityLifecycleAdapter() : adapter;
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                LogUtils.d(activity.getComponentName().toShortString() + " onCreate");
                synchronized (activity) {
                    String key = activity.getPackageName() + "." + activity.getLocalClassName();
                    if (!activityMap.containsKey(key)) {
                        activityMap.put(key, new ArrayList<BaseBroadcastReceiver>());
                    }
                    List<BaseBroadcastReceiver> list = activityMap.get(key);
                    // 绑定activity广播
                    ActivityReceiver activityReceiver = new ActivityReceiver();
                    activityReceiver.registerActivityReceiver(activity, (IOperator) activity);
                    list.add(activityReceiver);
                    // 绑定网络改变广播
                    NetReceiver netReceiver = new NetReceiver();
                    netReceiver.registerNetReceiver(activity, (INetChange) activity);
                    list.add(netReceiver);
                    // 绑定屏幕锁定广播
                    ScreenReceiver screenReceiver = new ScreenReceiver();
                    screenReceiver.registerScreenReceiver(activity, (IScreen) activity);
                    list.add(screenReceiver);

                    activityMap.put(key, list);
                }
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
                String key = activity.getPackageName() + "." + activity.getLocalClassName();
                if (activityMap.containsKey(key)) {
                    List<BaseBroadcastReceiver> list = activityMap.get(key);
                    for (BaseBroadcastReceiver receiver : list)
                        receiver.unRegisterReceiver(activity);
                    activityMap.remove(key);
                }
                finalAdapter.onActivityDestroyed(activity);
            }
        });
    }

    public static void onTerminate(Application app) {
        appNetReceiver.unRegisterReceiver(app.getApplicationContext());
    }

    public static void log(Context context, String msg) {
        LogUtils.file(StringUtils.getDateTimeNow("yyyy-MM-dd HH:mm:ss") + " # " + msg, DeviceUtils.appName(context), "log", StringUtils.getDateTimeNow("yyyy-MM") + ".log");
    }
}
