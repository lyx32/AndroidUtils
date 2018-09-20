package com.arraylist7.android.utils.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.arraylist7.android.utils.NetState;
import com.arraylist7.android.utils.UiUtils;
import com.arraylist7.android.utils.inter.INetChange;
import com.arraylist7.android.utils.listener.BaseBroadcastReceiverListener;

import java.io.Serializable;
import java.util.Map;


/**
 * Created by Administrator on 2016/5/23.
 */
public class NetReceiver extends BaseBroadcastReceiver {


    public NetReceiver() {

    }

    public void registerNetReceiver(Context context, final INetChange netChangeListener) {
        registerReceiver(context, ConnectivityManager.CONNECTIVITY_ACTION, new BaseBroadcastReceiverListener() {
            @Override
            public void onReceiver(Context context, String action, Map<String, Serializable> data) {
                if (null != netChangeListener) {
                    NetState netState = UiUtils.isConnected(context);
                    netChangeListener.onNetChange(netState);
                }
            }
        });
    }

}
