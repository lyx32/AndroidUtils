package com.arraylist7.android.utils.listener;

import android.content.Context;
import android.os.Message;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

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
public class OnListViewScrollListener implements OnScrollListener {

    private NHandler handler;
    private AbsListView listView;
    private OnScrollListener listener;

    public OnListViewScrollListener(AbsListView listView) {
        this(listView, null);
    }

    public OnListViewScrollListener(AbsListView listView, OnScrollListener listener) {
        super();
        this.listView = listView;
        this.listener = listener;
        handler = new NHandler(new IHandler() {
            @Override
            public void handlerMsg(Context context, NHandler handler, Message msg) {
                if (200 == msg.what) {
                    BitmapUtils.getPicasso().resumeTag(OnListViewScrollListener.this.listView.getContext());
                } else if (201 == msg.what) {
                    BitmapUtils.getPicasso().pauseTag(OnListViewScrollListener.this.listView.getContext());
                }
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int state) {
        if (null != BitmapUtils.getPicasso()) {
            if (state == SCROLL_STATE_IDLE || state == SCROLL_STATE_TOUCH_SCROLL) {
                if (handler.hasMessages(200) && handler.hasMessages(201)) {
                    handler.removeMessages(200);
                    handler.sendEmptyMessageDelayed(200, 300);
                } else {
                    handler.sendEmptyMessage(200);
                }
            } else {
                if (handler.hasMessages(200) && handler.hasMessages(201)) {
                    handler.removeMessages(201);
                    handler.sendEmptyMessageDelayed(201, 300);
                } else {
                    handler.sendEmptyMessage(201);
                }
            }
        }
        if (null != listener) listener.onScrollStateChanged(absListView, state);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (null != listener)
            listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    public AbsListView getListView() {
        return listView;
    }

    public OnScrollListener getListener() {
        return listener;
    }
}
