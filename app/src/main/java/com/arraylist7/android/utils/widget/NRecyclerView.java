package com.arraylist7.android.utils.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.arraylist7.android.utils.ClassUtils;
import com.arraylist7.android.utils.listener.OnRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/30 0030.
 */

public class NRecyclerView extends RecyclerView {

    private List<OnRecyclerViewScrollListener> scrollListeners = new ArrayList<>();

    public NRecyclerView(Context context) {
        this(context,null);
    }

    public NRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                setMaxFlingVelocity(60);
            }
        },600);
        this.addOnScrollListener(null);
    }

    @Override
    public void addOnScrollListener(OnScrollListener listener) {
        OnRecyclerViewScrollListener newListener = new OnRecyclerViewScrollListener(this,listener);
        scrollListeners.add(newListener);
        super.addOnScrollListener(newListener);
    }

    @Override
    public void removeOnScrollListener(OnScrollListener listener) {
        synchronized (scrollListeners) {
            OnRecyclerViewScrollListener remove =null;
            for (OnRecyclerViewScrollListener newListener : scrollListeners) {
                if(newListener.getListener().equals(listener)) {
                    remove = newListener;
                    break;
                }
            }
            scrollListeners.remove(remove);
            super.removeOnScrollListener(remove);
        }
    }

    public void setMaxFlingVelocity(int velocity){
        try {
            ClassUtils.setValue(this.getClass(),"mMaxFlingVelocity",velocity);
        } catch (Throwable throwable) {
        }
    }
}
