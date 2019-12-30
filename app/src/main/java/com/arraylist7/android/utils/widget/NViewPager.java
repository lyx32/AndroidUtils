package com.arraylist7.android.utils.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class NViewPager extends ViewPager {

    private boolean isCanHorizontalScroll = true;

    public NViewPager(Context context) {
        super(context);
    }

    public NViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isCanHorizontalScroll || -1 == event.findPointerIndex(0))
            return false;
        else
            return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isCanHorizontalScroll || -1 == event.findPointerIndex(0))
            return false;
        else
            return super.onInterceptTouchEvent(event);
    }

    public boolean isCanHorizontalScroll() {
        return isCanHorizontalScroll;
    }

    public void setCanHorizontalScroll(boolean isCanHorizontalScroll) {
        this.isCanHorizontalScroll = isCanHorizontalScroll;
    }
}
