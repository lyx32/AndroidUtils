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
    private WeakReference<Context> contexts;
    private IHandler handler;

    public NHandler(Context context, IHandler handler) {
        this.contexts = new WeakReference<>(context);
        this.handler = handler;
    }

    public NHandler(IHandler handler) {
        this.handler = handler;
    }

    @Override
    public final void handleMessage(Message msg) {
        Context ctx = null;
        if (null != contexts)
            ctx = contexts.get();
        if (null != handler) {
            handler.handlerMsg(ctx, this, msg);
        }
    }
}
