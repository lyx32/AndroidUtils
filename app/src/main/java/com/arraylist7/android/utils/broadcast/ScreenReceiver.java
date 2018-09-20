package com.arraylist7.android.utils.broadcast;

/**
 * Created by Administrator on 2017/2/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.NetState;
import com.arraylist7.android.utils.StringUtils;
import com.arraylist7.android.utils.UiUtils;
import com.arraylist7.android.utils.inter.IScreen;
import com.arraylist7.android.utils.listener.BaseBroadcastReceiverListener;

import java.io.Serializable;
import java.util.Map;

/**
 * https://github.com/litesuits/android-common/blob/master/app/src/main/java/com/litesuits/common/receiver/ScreenReceiver.java
 */
public class ScreenReceiver extends BaseBroadcastReceiver {

    public ScreenReceiver() {

    }


    public void registerScreenReceiver(Context context, final IScreen screenListener) {
        registerReceiver(context, StringUtils.asArray(Intent.ACTION_SCREEN_OFF, Intent.ACTION_SCREEN_ON), new BaseBroadcastReceiverListener() {
            @Override
            public void onReceiver(Context context, String action, Map<String, Serializable> data) {
                if (action.equals(Intent.ACTION_SCREEN_ON)) {
                    LogUtils.d("屏幕解锁广播...");
                    if (screenListener != null) {
                        screenListener.onScreenOnOrOff(true);
                    }
                } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                    LogUtils.d("屏幕加锁广播...");
                    if (screenListener != null) {
                        screenListener.onScreenOnOrOff(false);
                    }
                }
            }
        });
    }

}