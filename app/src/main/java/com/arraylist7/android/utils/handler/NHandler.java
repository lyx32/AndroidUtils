package com.arraylist7.android.utils.handler;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.arraylist7.android.utils.inter.IHandler;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2017/6/15 0015.
 */

public class NHandler extends Handler {
    private IHandler handler;
    private final WeakReference<Context> contexts;

    public NHandler(Context context,IHandler handler) {
        this.contexts = new WeakReference<Context>(context);
        this.handler = handler;
    }

    @Override
    public final void handleMessage(Message msg) {
        Context context = contexts.get();
        if (context != null) {
            handler.handlerMsg(msg);
        }else{
            this.removeCallbacksAndMessages(null);
        }
    }
}
