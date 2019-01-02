package com.arraylist7.android.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2016/11/25.
 */

public class StatusBarUtils {

    /**
     * 设置状态栏颜色
     *
     * @param activity   需要设置的activity
     * @param color 状态栏颜色值(不是R.color.xxx)
     */
    public static void setColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().setStatusBarColor(color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View statusView = createStatusView(activity, color);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            int end = decorView.getChildCount();
            for (int i = 0; i < end; i++) {
                View v = decorView.getChildAt(i);
                if (StringUtils.equals("createStatusView", v.getTag())) {
                    decorView.removeView(v);
                    break;
                }
            }
            decorView.addView(statusView);
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }


    /**
     * 设置状态栏颜色(会比预期颜色更深一些)
     *
     * @param activity   需要设置的activity
     * @param color 状态栏颜色值(不是R.color.xxx)
     */
    public static void setDeepColor(Activity activity, int color) {
        int r = (int) (Color.red(color) * 0.7);
        int g = (int) (Color.green(color) * 0.7);
        int b = (int) (Color.blue(color) * 0.7);
        color = Color.rgb(r, g, b);
        setColor(activity,color);
    }


    /**
     * 设置状态栏为透明
     * <p>
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     *
     * @param activity 需要设置的activity
     * @param isFully  是否完全透明
     */
    public static void setTranslucent(Activity activity, boolean isFully) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = activity.getWindow().getDecorView();
            if (isFully) {
                int options = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(options);
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            } else {
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }


    /**
     * 生成一个和状态栏大小相同的矩形条
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @return 状态栏矩形条
     */
    private static View createStatusView(Activity activity, int color) {
        // 获得状态栏高度
        int statusBarHeight = UiUtils.getStatusHeight(activity);
        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(activity);
        statusView.setTag("createStatusView");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);
        return statusView;
    }
}
