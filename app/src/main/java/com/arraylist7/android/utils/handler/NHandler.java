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
    private final WeakReference<IHandler> handlers;

    public NHandler(IHandler handler) {
        handlers = new WeakReference<IHandler>(handler);
    }

    @Override
    public final void handleMessage(Message msg) {
        IHandler handler = handlers.get();
        if (handler != null) {
            handler.handlerMsg(this, msg);
        }
    }
}
