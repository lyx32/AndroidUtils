package com.arraylist7.android.utils.broadcast;

/**
 * Created by Administrator on 2017/2/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.StringUtils;
import com.arraylist7.android.utils.inter.IOperator;
import com.arraylist7.android.utils.listener.BaseBroadcastReceiverListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ActivityReceiver extends BaseBroadcastReceiver {
    public static final String ACTION_RECEIVE_DATA = "Activity_Receiver_Receive_Data";
    public static final String ACTION_LOGIN_SUCCESS = "Activity_Receiver_Login_Success";
    public static final String ACTION_LOGIN_OUT = "Activity_Receiver_Login_Out";

    public ActivityReceiver() {

    }


    public static void sendData(Context context, String key, Serializable value) {
        send(context, ActivityReceiver.ACTION_RECEIVE_DATA, key, value);
    }

    public static void sendData(Context context, Map<String, Serializable> data) {
        Bundle bundle = new Bundle();
        for (String key : data.keySet()) {
            bundle.putSerializable(key, data.get(key));
        }
        send(context, ActivityReceiver.ACTION_RECEIVE_DATA, bundle);
    }

    public static void sendLoginSuccess(Context context, String key, Serializable value) {
        send(context, ActivityReceiver.ACTION_LOGIN_SUCCESS, key, value);
    }


    public static void sendLoginSuccess(Context context, Map<String, Serializable> data) {
        Bundle bundle = new Bundle();
        for (String key : data.keySet()) {
            bundle.putSerializable(key, data.get(key));
        }
        send(context, ActivityReceiver.ACTION_LOGIN_SUCCESS, bundle);
    }

    public static void sendLoginOut(Context context, String key, Serializable value) {
        send(context, ActivityReceiver.ACTION_LOGIN_OUT, key, value);
    }

    public static void sendLoginOut(Context context, Map<String, Serializable> data) {
        Bundle bundle = new Bundle();
        for (String key : data.keySet()) {
            bundle.putSerializable(key, data.get(key));
        }
        send(context, ActivityReceiver.ACTION_LOGIN_OUT, bundle);
    }


    public void registerActivityReceiver(Context context,final  IOperator listener) {
        registerReceiver(context, StringUtils.asArray(ActivityReceiver.ACTION_RECEIVE_DATA, ActivityReceiver.ACTION_LOGIN_SUCCESS, ActivityReceiver.ACTION_LOGIN_OUT), new BaseBroadcastReceiverListener() {
            @Override
            public void onReceiver(Context context, String action, Map<String, Serializable> data) {
                if (ActivityReceiver.ACTION_RECEIVE_DATA.equals(action)) {
                    listener.onReceivedData(data);
                }else if (ActivityReceiver.ACTION_LOGIN_SUCCESS.equals(action)) {
                    listener.onLoginSuccess(data);
                }else if (ActivityReceiver.ACTION_LOGIN_OUT.equals(action)) {
                    listener.onLoginOut(data);
                }
            }
        });
    }

}