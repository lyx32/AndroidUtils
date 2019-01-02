package com.arraylist7.android.utils.itemdecoration;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {

    protected Drawable mDivider;
    protected int strokeWidth = 5;

    public RecyclerViewItemDecoration(int color) {
        this(color, 5);
    }

    public RecyclerViewItemDecoration(int color, int strokeWidth) {
        mDivider = new ColorDrawable(color);
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
            // 绘制横向时多绘制一点，这样避免4个item相交点会有空白
            int right = child.getRight() + ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).rightMargin + strokeWidth; /*+ params.rightMargin*/
            int top = child.getBottom();// + params.bottomMargin;
            // 绘制是算上margin距离，这样避免分割线高度差异
            int bottom = top + ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).bottomMargin + strokeWidth;
            int spanCount = getSpanCount(parent);
            boolean isLastRow = isLastRow(parent, i, spanCount, childCount);
            if (isLastRow) {
                bottom = top;
                // 如果最后一行则不多绘制，避免横向分割线超出View宽度
                right = child.getRight();
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
            // 绘制纵向时多绘制一点，这样避免4个item相交点会有空白
            int bottom = child.getBottom() + ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).bottomMargin + strokeWidth;
            int left = child.getRight();
            int right = left + ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).rightMargin + strokeWidth;
            int spanCount = getSpanCount(parent);
            boolean isLastColumn = isLastColum(parent, i, spanCount, childCount);
            boolean isLastRow = isLastRow(parent, i, spanCount, childCount);
            if (isLastColumn) {
                right -= strokeWidth;
            }
            // 如果最后一行则不多绘制，避免纵向分割线超出View高度
            if(isLastRow){
                bottom = child.getBottom();
            }
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public boolean isLastColum(RecyclerView parent, int pos, int spanCount, int childCount) {
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

    public boolean isLastRow(RecyclerView parent, int pos, int spanCount, int childCount) {
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