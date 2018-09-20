package com.arraylist7.android.utils.listener;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;
import java.util.Map;

public interface BaseBroadcastReceiverListener {
    public void onReceiver(Context context, String action,Map<String,Serializable> data);
}
