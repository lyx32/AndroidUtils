package com.arraylist7.android.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;

import androidx.annotation.NonNull;

/**
 * Created by Administrator on 2018/1/4 0004.
 */

public final class CanvasUtils {


    CanvasUtils() {
    }


    /**
     * 绘制圆角矩形，低版本使用贝塞尔线模拟圆角矩形
     *
     * @param canvas 画布
     * @param rect   矩形大小
     * @param rx     x圆角大小
     * @param ry     y圆角大小
     * @param paint  画笔
     */
    public static void drawRoundRect(Canvas canvas, @NonNull RectF rect, float rx, float ry, @NonNull Paint paint) {
        drawRoundRect(canvas, rect.left, rect.top, rect.right, rect.bottom, rx, ry, paint);
    }

    /**
     * 绘制圆角矩形，低版本使用贝塞尔线模拟圆角矩形
     *
     * @param canvas 画布
     * @param left   左边起点
     * @param top    上面起点
     * @param right  矩形宽度
     * @param bottom 矩形高度
     * @param rx     x圆角大小
     * @param ry     y圆角大小
     * @param paint  画笔
     */
    public static void drawRoundRect(Canvas canvas, float left, float top, float right, float bottom, float rx, float ry, @NonNull Paint paint) {
        if (Build.VERSION.SDK_INT >= 21) {
            canvas.drawRoundRect(left, top, right, bottom, rx, ry, paint);
        } else {
            Path backgroundPath = new Path();
            // 左边纵向居中，这样好去画左上角的圆角
            backgroundPath.moveTo(left, top + (bottom) / 2);
            // 连线到左上角，留一点距离去画圆角
            backgroundPath.lineTo(left, top + ry);
            // 左上角圆角
            backgroundPath.quadTo(left, top, left + rx, top);
            // 连线到右上角，留一点距离去画圆角
            backgroundPath.lineTo(right - rx, top);
            // 右上角圆角
            backgroundPath.quadTo(right, top, right, top + ry);
            // 连线到右下角，留一点距离去画圆角
            backgroundPath.lineTo(right, bottom - ry);
            // 右下角圆角
            backgroundPath.quadTo(right, bottom, right - rx, bottom);
            // 连线到左下角，留一点距离去画圆角
            backgroundPath.lineTo(left + rx, bottom);
            // 左下角圆角
            backgroundPath.quadTo(left, bottom, left, bottom - ry);
            // 连线到左上角，画圆点之前的位置。（PS：这一步其实可以不要）
            backgroundPath.lineTo(left, top + ry);
            backgroundPath.close();
            canvas.drawPath(backgroundPath, paint);
        }
    }

    /**
     * 获取文字宽度
     *
     * @param paint
     * @param str
     * @return
     */
    public static float getTextWidth(Paint paint, String str) {
        return paint.measureText(str);
    }
    /**
     * 获取文字宽度
     *
     * @param paint
     * @param str
     * @return
     */
    public static int getTextHeight(Paint paint, String str) {
        return getTextWidthAndHeight(paint,str).height();
    }

    /**
     * 获取文字宽度
     *
     * @param paint
     * @param str
     * @return
     */
    public static Rect getTextWidthAndHeight(Paint paint, String str) {
        Rect widthAndHeight = new Rect();
        paint.getTextBounds(str, 0, str.length(), widthAndHeight);
        return widthAndHeight;
    }
}
