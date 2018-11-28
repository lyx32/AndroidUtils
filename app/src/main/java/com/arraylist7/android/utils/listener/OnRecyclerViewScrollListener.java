package com.arraylist7.android.utils.listener;

import android.os.Message;
import android.support.v7.widget.RecyclerView;

import com.arraylist7.android.utils.BitmapUtils;
import com.arraylist7.android.utils.handler.NHandler;
import com.arraylist7.android.utils.inter.IHandler;


/**
 * 功能：优化滑动加载图片<br>
 * 时间：2015年12月24日<br>
 * 备注：<br>
 *
 * @author ke
 */
public class OnRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private NHandler handler;
    private RecyclerView recyclerView;
    private RecyclerView.OnScrollListener listener;

    public OnRecyclerViewScrollListener(RecyclerView recyclerView) {
        this(recyclerView, null);
    }

    public OnRecyclerViewScrollListener(RecyclerView recyclerView, RecyclerView.OnScrollListener listener) {
        super();
        this.recyclerView = recyclerView;
        this.listener = listener;
        handler = new NHandler(new IHandler() {
            @Override
            public void handlerMsg(NHandler handler, Message msg) {
                if (200 == msg.what) {
                    BitmapUtils.getPicasso().resumeTag(OnRecyclerViewScrollListener.this.recyclerView.getContext());
                } else if (201 == msg.what) {
                    BitmapUtils.getPicasso().pauseTag(OnRecyclerViewScrollListener.this.recyclerView.getContext());
                }
            }
        });
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (handler.hasMessages(200) && handler.hasMessages(201)) {
                handler.removeCallbacksAndMessages(null);
                handler.sendEmptyMessageDelayed(200, 300);
            } else {
                handler.sendEmptyMessage(200);
            }
        } else {
            if (handler.hasMessages(200) && handler.hasMessages(201)) {
                handler.removeCallbacksAndMessages(null);
                handler.sendEmptyMessageDelayed(201, 300);
            } else {
                handler.sendEmptyMessage(201);
            }
        }
        if (null != listener) listener.onScrollStateChanged(recyclerView, newState);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public RecyclerView.OnScrollListener getListener() {
        return listener;
    }
}
