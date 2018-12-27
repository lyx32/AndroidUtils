package com.arraylist7.android.utils.itemdecoration;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;


public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int strokeWidth = 5;

    public RecyclerViewItemDecoration(int javaColor) {
        this(javaColor,5);
    }
    public RecyclerViewItemDecoration(int javaColor, int strokeWidth) {
        mDivider = new ColorDrawable(javaColor);
        this.strokeWidth = strokeWidth;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int left = child.getLeft();//- params.leftMargin;
            int right = child.getRight() /*+ params.rightMargin*/;
            int top = child.getBottom();// + params.bottomMargin;
            int bottom = top + strokeWidth;

            int spanCount = getSpanCount(parent);
            boolean isLastRow = isLastRow(parent, i, spanCount, childCount);
            if (isLastRow) {
                bottom -= strokeWidth;
            }
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int top = child.getTop();
            int bottom = child.getBottom();
            int left = child.getRight();
            int right = left + strokeWidth;

            int spanCount = getSpanCount(parent);
            boolean isLastColumn = isLastColum(parent, i, spanCount, childCount);
            if (isLastColumn) {
                right -= strokeWidth;
            }
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private boolean isLastColum(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return ((pos + 1) % spanCount == 0);
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                return ((pos + 1) % spanCount == 0);
            } else {
                return (pos + 1 == childCount);
            }
        }
        return false;
    }

    private boolean isLastRow(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return childCount - pos <= spanCount;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                return childCount - pos <= spanCount;
            } else {
                return true;
            }
        }
        return false;
    }

}