package com.arraylist7.android.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.arraylist7.android.utils.base.BaseAppCompatActivity;
import com.arraylist7.android.utils.broadcast.ActivityBroadcast;
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

    private static NetReceiver appNetReceiver = new NetReceiver();
    private static Map<String, BaseAppCompatActivity> netMap = new HashMap<>();
    private static Map<String, List<BaseBroadcastReceiver>> activityMap = new HashMap<>();

    AppUtils() {
    }

    public static void init(@NonNull Application app) {
        init(app, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void init(@NonNull final Application app, final ActivityLifecycleAdapter adapter) {

        BitmapUtils.init(app.getApplicationContext());

        LogUtils.setTAG(app.getApplicationContext().getPackageName());

        ThrowableUtils.setDefaultExceptionHandler(app.getApplicationContext());

        appNetReceiver.registerNetReceiver(app.getApplicationContext(), new INetChange() {
            @Override
            public void onNetChange(NetState state) {
                Collection<BaseAppCompatActivity> allList = netMap.values();
                for (BaseAppCompatActivity activity : allList) {
                    activity.onNetChange(state);
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
                    if (activity instanceof IOperator) {
                        ActivityBroadcast activityReceiver = new ActivityBroadcast();
                        activityReceiver.registerActivityReceiver(activity, (IOperator) activity);
                        list.add(activityReceiver);
                    }
                    // 绑定网络改变广播
                    if (activity instanceof BaseAppCompatActivity) {
                        netMap.put(key, (BaseAppCompatActivity) activity);
                    }
                    // 绑定屏幕锁定广播
                    if (activity instanceof IScreen) {
                        ScreenReceiver screenReceiver = new ScreenReceiver();
                        screenReceiver.registerScreenReceiver(activity, (IScreen) activity);
                        list.add(screenReceiver);
                    }
                    if(0 != list.size())
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
                netMap.remove(key);
                if(0 == netMap.size())
                    appNetReceiver.unRegisterReceiver(app.getApplicationContext());
                finalAdapter.onActivityDestroyed(activity);
                ThreadUtils.cancel(activity);
            }
        });
    }


    public static void clearCache(Context context){

        CacheUtils.getPublicDir(context, "picasso-cache");
    }


    public static void cleanInvalidMemory(Context context) {
        int res = context.checkCallingOrSelfPermission("android.permission.KILL_BACKGROUND_PROCESSES");
        if (PackageManager.PERMISSION_GRANTED == res) {
            getAvailMemory(context);
            ActivityManager activityManger = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> list = activityManger.getRunningAppProcesses();
            if (list != null) {
                int close = 0;
                for (int i = 0; i < list.size(); i++) {
                    ActivityManager.RunningAppProcessInfo apinfo = list.get(i);
                    String[] pkgList = apinfo.pkgList;
                    if (apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                        for (int j = 0; j < pkgList.length; j++) {
                            activityManger.killBackgroundProcesses(pkgList[j]);
                        }
                        close++;
                    }
                }
                LogUtils.i("已关闭" + close + "个后台进程");
            }
            getAvailMemory(context);
        }
        System.gc();
    }

    public static long getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        LogUtils.d("可用内存---->>>" + (mi.availMem / (1024 * 1024)) + "M");
        return mi.availMem / (1024 * 1024);
    }
}
