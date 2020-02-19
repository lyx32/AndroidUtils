package com.arraylist7.android.utils.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.adapter.RecyclerViewAdapter;
import com.arraylist7.android.utils.itemdecoration.RecyclerViewItemDecoration;
import com.arraylist7.android.utils.listener.OnRecyclerViewItemClickListener;
import com.arraylist7.android.utils.listener.OnRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

public class NRecyclerView extends RecyclerView {

    private List<OnRecyclerViewScrollListener> listeners = new ArrayList<>();

    public NRecyclerView(Context context) {
        this(context, null);
    }

    public NRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // 默认上下滑动
        setVertical();
        // 默认添加一个滚动事件
        addOnScrollListener(new OnScrollListener() {
        });
    }


    public void setVertical() {
        setVertical(true);
    }

    public void setHorizontal() {
        setHorizontal(true);
    }

    public void setVertical(final boolean isCanScroll) {
        LinearLayoutManager manager = getLinearLayoutManager(true, isCanScroll);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        this.setLayoutManager(manager);
    }

    public void setHorizontal(final boolean isCanScroll) {
        LinearLayoutManager manager = getLinearLayoutManager(false, isCanScroll);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.setLayoutManager(manager);
    }

    public void setGridLayout(int spanCount) {
        setGridLayout(spanCount, true);
    }

    public void setGridLayout(int spanCount, final boolean isCanScroll) {
        GridLayoutManager manager = new GridLayoutManager(this.getContext(), spanCount) {
            public boolean canScrollVertically() {
                return isCanScroll;
            }
        };
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        this.setLayoutManager(manager);
    }

    public <T> void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        Adapter adapter = this.getAdapter();
        if (null == adapter)
            return;
        if (adapter instanceof RecyclerViewAdapter) {
            RecyclerViewAdapter<T> recyclerViewAdapter = (RecyclerViewAdapter<T>) adapter;
            recyclerViewAdapter.setOnItemClickListener(listener);
        }
    }


    public void setListDivider(int color, int strokeWidth) {
        this.addItemDecoration(new RecyclerViewItemDecoration(color, strokeWidth));
    }

    @Override
    public void addOnScrollListener(OnScrollListener listener) {
        if (null != listener) {
            OnRecyclerViewScrollListener l = new OnRecyclerViewScrollListener(this, listener);
            listeners.add(l);
            super.addOnScrollListener(l);
        }
    }

    @Override
    public void removeOnScrollListener(OnScrollListener listener) {
        if (null != listener) {
            if (listener instanceof OnRecyclerViewScrollListener) {
                super.removeOnScrollListener(listener);
            } else {
                OnRecyclerViewScrollListener remove = null;
                for (OnRecyclerViewScrollListener l : listeners) {
                    if (l.getListener() == listener) {
                        remove = l;
                        this.removeOnScrollListener(l);
                        break;
                    }
                }
                if (null != remove)
                    listeners.remove(remove);
            }
        }
    }


    @Override
    @Deprecated
    public void setOnScrollListener(OnScrollListener listener) {
        addOnScrollListener(listener);
    }

    private LinearLayoutManager getLinearLayoutManager(final boolean isVerticallyScroll, final boolean isCanScroll) {
        return new LinearLayoutManager(this.getContext()) {
            public boolean canScrollVertically() {
                return isVerticallyScroll && isCanScroll;
            }

            @Override
            public boolean canScrollHorizontally() {
                return !isVerticallyScroll && isCanScroll;
            }

            private static final float MILLISECONDS_PER_INCH = 25f;

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, final int position) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        View view = getChildAt(0);
                        if (view != null) {
                            //获取当前item的position
                            int firstChildPos = getPosition(view);
                            //算出需要滑动的item数量
                            int delta = Math.abs(position - firstChildPos) + 1;
                            if (delta == 0)
                                delta = 1;
                            double data = 2D / delta;
                            // 已经爆了，那说明需要滚动的数量有点多，那么则滚快点
                            if ((data + "").contains("E")) {
                                data = 0.001D;
                            }
                            return (float) data / 2;
                        } else {
                            return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                        }
                    }
                };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        };
    }
}
