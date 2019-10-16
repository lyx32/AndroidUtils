package com.arraylist7.android.utils.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.StringUtils;
import com.arraylist7.android.utils.listener.BaseBroadcastReceiverListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BaseBroadcastReceiver extends BroadcastReceiver {


    private static final String ACTION_DATA_KEY = "Activity_Receiver_Action_Data_Key";

    private boolean isRegister = false;
    private String[] actions = null;
    private BaseBroadcastReceiverListener baseListener = null;

    @Override
    public final void onReceive(Context context, Intent intent) {
        if (null == actions || 0 == actions.length)
            return;
        if (null == baseListener)
            return;
        LogUtils.d("收到通用广播...");
        Bundle bundle = intent.getBundleExtra(ACTION_DATA_KEY);
        Map<String, Serializable> map = new HashMap<>();
        if(null != bundle && 0 != bundle.size()) {
            for (String key : bundle.keySet()) {
                map.put(key, bundle.getSerializable(key));
            }
        }
        for (String action : actions) {
            if (intent.getAction().equals(action)) {
                baseListener.onReceiver(context, action, map);
                break;
            }
        }
    }

    public static void send(Context context, String action, String key, Serializable value) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, value);
        send(context, action, bundle);
    }

    public static void send(Context context, String action, Bundle data) {
        Intent intent = new Intent(action);
        intent.putExtra(ACTION_DATA_KEY, data);
        context.sendBroadcast(intent);
    }


    public void registerReceiver(Context context, String action,BaseBroadcastReceiverListener baseListener) {
        registerReceiver(context, StringUtils.asArray(action), baseListener);
    }

    public void registerReceiver(Context context, String[] actions, BaseBroadcastReceiverListener baseListener) {
        try {
            this.baseListener = baseListener;
            IntentFilter filter = new IntentFilter();
            if (null != actions && 0 != actions.length) {
                this.actions = actions;
                for (String action : actions)
                    filter.addAction(action);
            }
            context.registerReceiver(this, filter);
            isRegister = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unRegisterReceiver(Context context) {
        if(isRegister) {
            try {
                context.unregisterReceiver(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
