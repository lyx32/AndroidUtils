package com.arraylist7.android.utils.itemdecoration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.arraylist7.android.utils.LogUtils;

/**
 * 使用该类绘制分割线，必须要给RecyclerView的Item设置margin
 */
public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {

    protected Paint paint;


    /**
     * 绘制RecyclerView分割线
     *
     * @param color       分割线颜色
     * @param strokeWidth 分割线线条宽度（注：请不要大于RecyclerView.Item的margin）
     */
    public RecyclerViewItemDecoration(int color, int strokeWidth) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
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
        int childCount = parent.getAdapter().getItemCount();
        int spanCount = getSpanCount(parent);
        for (int i = 0; i < childCount; i++) {
            if (parent.getChildCount() < i) break;
            boolean isLastRow = isLastRow(parent, i, spanCount, childCount);
            if (!isLastRow) {
                View child = parent.getChildAt(i);
                if (null != child) {
                    ViewGroup.MarginLayoutParams lp = ((ViewGroup.MarginLayoutParams) child.getLayoutParams());
                    if (null != lp) {
                        int startX = child.getLeft() - lp.leftMargin;
                        int endX = child.getRight() + lp.rightMargin;
                        int y = child.getBottom() + lp.bottomMargin;
                        c.drawLine(startX, y, endX, y, paint);
                    }
                }
            }
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getAdapter().getItemCount();
        int spanCount = getSpanCount(parent);
        for (int i = 0; i < childCount; i++) {
            if (parent.getChildCount() < i) break;
            boolean isLastColumn = isLastColumn(parent, i, spanCount, childCount);
            if (!isLastColumn) {
                boolean isLastRow = isLastRow(parent, i, spanCount, childCount);
                boolean isFirstRow = isFirstRow(parent, i, spanCount, childCount);
                View child = parent.getChildAt(i);
                if (null != child) {
                    ViewGroup.MarginLayoutParams lp = ((ViewGroup.MarginLayoutParams) child.getLayoutParams());
                    if (null != lp) {
                        int x = child.getRight() + lp.rightMargin;
                        int startY = child.getTop() - (isFirstRow ? 0 : lp.topMargin);
                        int endY = child.getBottom() + (isLastRow ? 0 : lp.bottomMargin);
                        c.drawLine(x, startY, x, endY, paint);
                    }
                }
            }
        }
    }


    public boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
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
        } else if (layoutManager instanceof LinearLayoutManager) {
            if (LinearLayoutManager.VERTICAL == ((LinearLayoutManager) layoutManager).getOrientation())
                return true;
            else
                return (pos + 1 == childCount);
        }
        return false;
    }


    public boolean isFirstRow(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return pos < spanCount;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL)
                return pos == 0;
            return true;
        } else if (layoutManager instanceof LinearLayoutManager) {
            if (LinearLayoutManager.HORIZONTAL == ((LinearLayoutManager) layoutManager).getOrientation())
                return true;
            return pos == 0;
        }
        return false;
    }

    public boolean isLastRow(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int rowCount = (int) Math.ceil((childCount + 0F) / spanCount);
            int curCount = (int) Math.ceil((pos + 1F) / spanCount);
            return rowCount == curCount;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                int rowCount = (int) Math.ceil((childCount + 0F) / spanCount);
                int curCount = (int) Math.ceil((pos + 1F) / spanCount);
                return rowCount == curCount;
            } else {
                return true;
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            if (LinearLayoutManager.HORIZONTAL == ((LinearLayoutManager) layoutManager).getOrientation())
                return true;
            return childCount == pos + 1;
        }
        return false;
    }

}