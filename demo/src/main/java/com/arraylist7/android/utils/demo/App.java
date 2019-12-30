package com.arraylist7.android.utils.demo;

import android.app.Application;
import android.content.Context;

import com.arraylist7.android.utils.AppUtils;
import com.arraylist7.android.utils.BitmapUtils;
import com.arraylist7.android.utils.IntentUtils;
import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.StringUtils;

public class App extends Application {


    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LogUtils.setDebug(BuildConfig.DEBUG);
        AppUtils.init(this);
        BitmapUtils.init(context, StringUtils.asMap("request-source","android"));
    }

    public static Context getContext() {
        return context;
    }
}
