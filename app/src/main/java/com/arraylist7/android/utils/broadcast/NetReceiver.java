package com.arraylist7.android.utils.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.arraylist7.android.utils.NetState;
import com.arraylist7.android.utils.UiUtils;


/**
 * Created by Administrator on 2016/5/23.
 */
public class NetReceiver extends BroadcastReceiver {

    private NetChangeListener netChangeListener;

    public NetReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (null != netChangeListener) {
                NetState netState = UiUtils.isConnected(context);
                netChangeListener.onNetChange(netState);
            }
        }
    }

    public void registerScreenReceiver(Context context, NetChangeListener netChangeListener) {
        try {
            this.netChangeListener = netChangeListener;
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(this, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unRegisterScreenReceiver(Context context) {
        try {
            context.unregisterReceiver(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static interface NetChangeListener {
        public void onNetChange(NetState state);
    }
}
