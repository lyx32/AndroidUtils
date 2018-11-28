package com.arraylist7.android.utils.demo;

import android.app.Application;
import android.content.Context;

import com.arraylist7.android.utils.AppUtils;
import com.arraylist7.android.utils.LogUtils;

public class App extends Application {


    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LogUtils.setDebug(BuildConfig.DEBUG);
        AppUtils.init(this);
    }

    public static Context getContext() {
        return context;
    }
}
