package com.arraylist7.android.utils;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.arraylist7.android.utils.adapter.holder.BaseViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class UiUtils {

    private static Toast toast = null;

    UiUtils() {
    }

    public static int dpTopx(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }

    public static int spTopx(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());
    }

    public static float pxTodp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    public static float pxTosp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }


    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics outMetrics = getDisplayMetrics(context);
        return outMetrics.widthPixels;
    }

    /**
     * 得到屏幕宽高
     *
     * @param context
     * @return
     */
    public static Point getScreen(Context context) {
        DisplayMetrics outMetrics = getDisplayMetrics(context);
        Point wh = new Point(outMetrics.widthPixels, outMetrics.heightPixels);
        return wh;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics outMetrics = getDisplayMetrics(context);
        return outMetrics.heightPixels;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
            if (1 > statusHeight)
                statusHeight = dpTopx(context, 25F);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取当前屏幕截图
     *
     * @param activity
     * @param concatStatusBar 是否包含状态栏
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity, boolean concatStatusBar) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int y = 0;
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        if (!concatStatusBar) {
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            y = frame.top;
            height -= y;
        }
        bp = Bitmap.createBitmap(bmp, 0, y, width, height);
        view.destroyDrawingCache();
        return bp;
    }


    /**
     * 创建快捷方式
     *
     * @param cxt   Context
     * @param icon  快捷方式图标
     * @param title 快捷方式标题
     * @param cls   要启动的类
     */
    public void createDeskShortCut(Context cxt, int icon, String title, Class<?> cls) {
        // 创建快捷方式的Intent
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutIntent.putExtra("duplicate", false);
        // 需要现实的名称
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        // 快捷图片
        Parcelable ico = Intent.ShortcutIconResource.fromContext(cxt.getApplicationContext(), icon);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, ico);
        Intent intent = new Intent(cxt, cls);
        // 下面两个属性是为了当应用程序卸载时桌面上的快捷方式会删除
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        // 点击快捷图片，运行的程序主入口
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        // 发送广播。OK
        cxt.sendBroadcast(shortcutIntent);
    }

    /**
     * 短时间显示提示
     *
     * @param message
     */
    public static void showShort(Context context, String message) {
        if (null != toast) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 长时间显示提示
     *
     * @param message
     */
    public static void showLong(Context context, String message) {
        if (null != toast) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }


    private static AlertDialog dialog = null;

    public static void showLoading(Activity activity) {
        showLoading(activity, null);
    }

    public static void showLoading(Activity activity, String msg) {
        if (null != dialog) {
            dialog.cancel();
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        final BaseViewHolder holder = new BaseViewHolder(activity, R.layout.dialog_progress);
        holder.setText(R.id.dialog_progress_msg, StringUtils.isNullOrEmpty(msg) ? "请稍等..." : msg);
        holder.getView(R.id.dialog_progress, ProgressBar.class).setIndeterminate(true);
        dialog = builder.setTitle(null).setView(holder.getItemView()).show();
    }

    public static void closeLoading() {
        if (null != dialog) {
            dialog.cancel();
            dialog.dismiss();
        }
    }


    /**
     * 显示日期选择
     *
     * @param v
     */
    public static void showDateDialog(View v) {
        showDateDialog(v, null, null);
    }

    /**
     * 显示日期选择
     *
     * @param v
     * @param start
     * @param end
     */
    public static void showDateDialog(final View v, TextView start, TextView end) {
        TextView tv = (TextView) v;
        String datetime = tv.getText().toString();
        Calendar now = Calendar.getInstance(Locale.CHINA);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!StringUtils.isNullOrEmpty(datetime)) {
            try {
                now.setTime(sdf.parse(datetime + " 12:12:12"));
            } catch (ParseException e) {
            }
        }
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear += 1;
                String str = year + "" + (10 > monthOfYear ? "0" + monthOfYear : "" + monthOfYear) + "" + (10 > dayOfMonth ? "0" + dayOfMonth : "" + dayOfMonth);
                ((TextView) v).setText(year + "-" + (10 > monthOfYear ? "0" + monthOfYear : "" + monthOfYear) + "-" + (10 > dayOfMonth ? "0" + dayOfMonth : "" + dayOfMonth));
                v.setTag(str);
            }
        }, year, month, day);
        if (v == end) {
            String startStr = start.getText().toString();
            if (!StringUtils.isNullOrEmpty(startStr)) {
                try {
                    Date startDate = sdf.parse(startStr + " 00:00:01");
                    dpd.getDatePicker().setMinDate(startDate.getTime());
                } catch (ParseException e) {
                }
            }
        } else if (v == start) {
            String endStr = end.getText().toString();
            if (!StringUtils.isNullOrEmpty(endStr)) {
                try {
                    Date endDate = sdf.parse(endStr + " 23:59:59");
                    dpd.getDatePicker().setMaxDate(endDate.getTime());
                } catch (ParseException e) {
                }
            }
        }
        dpd.show();
    }


    /**
     * 判断当前是否网络连接
     *
     * @param context
     * @return 状态码
     */
    public static NetState isConnected(Context context) {
        if (PackageManager.PERMISSION_GRANTED != context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)) {
            return NetState.NET_NO_PERMISSION;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetState stateCode = NetState.NET_NO;
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnectedOrConnecting()) {
            switch (ni.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    stateCode = NetState.NET_WIFI;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    int dataNetworkType = ni.getSubtype();
                    if (Build.VERSION.SDK_INT >= 24)
                        dataNetworkType = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDataNetworkType();
                    switch (dataNetworkType) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            stateCode = NetState.NET_2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            stateCode = NetState.NET_3G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            stateCode = NetState.NET_4G;
                            break;
                        default:
                            stateCode = NetState.NET_UNKNOWN;
                    }
                    break;
                default:
                    stateCode = NetState.NET_UNKNOWN;
                    break;
            }
        }
        return stateCode;
    }
}
