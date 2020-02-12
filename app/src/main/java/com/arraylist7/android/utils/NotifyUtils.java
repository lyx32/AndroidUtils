package com.arraylist7.android.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import static android.content.Context.NOTIFICATION_SERVICE;

public final class NotifyUtils {
    private static final String ACTION = "AndroidUtils_Notify_Action";
    private static final String KEY_CLASSNAME = "AndroidUtils_Notify_Key_Class";

    private static int DEFAULT_SMALLICON = -1;
    private static Bitmap DEFAULT_LARGEICON = null;


    public static class NotificationClickReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle.containsKey(KEY_CLASSNAME)) {
                Class clazz = (Class) bundle.getSerializable(KEY_CLASSNAME);
                Intent newIntent = new Intent(context, clazz);
                newIntent.putExtras(bundle);
                newIntent.setPackage(clazz.getPackage().getName());
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newIntent);
            }
        }
    }

    private NotifyUtils() {

    }

    public static void init(@DrawableRes int smallIcon, Bitmap largeIcon) {
        NotifyUtils.DEFAULT_SMALLICON = smallIcon;
        NotifyUtils.DEFAULT_LARGEICON = largeIcon;
    }

    /**
     * 创建通知渠道
     *
     * @param id          渠道id，只要唯一就行
     * @param name        渠道名称，给用户看的
     * @param description 渠道描述，给用户看的
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationChannel getChannelId(String id, String name, String description) {
        return getChannelId(id, name, description, NotificationManager.IMPORTANCE_DEFAULT);
    }

    /**
     * 创建通知渠道
     *
     * @param id          渠道id，只要唯一就行
     * @param name        渠道名称，给用户看的
     * @param description 渠道描述，给用户看的
     * @param importance  优先级 NotificationManager.IMPORTANCE_xxx
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationChannel getChannelId(String id, String name, String description, @Nullable int importance) {
        if (importance <= 0)
            importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        channel.setDescription(description);
        return channel;
    }

    /**
     * 下载通知
     *
     * @param context
     * @param uri
     * @param title
     * @param content
     */
    public static void download(Context context, Uri uri, CharSequence title, CharSequence content) {
        download(context, uri, null, title, content);
    }

    /**
     * 下载通知
     *
     * @param context
     * @param uri
     * @param headers
     * @param title
     * @param content
     */
    public static void download(Context context, Uri uri, Map<String, String> headers, CharSequence title, CharSequence content) {
        String savePath = CacheUtils.getDownloadCachePath(context) + "/" + FileUtils.getFileName(uri.getPath());
        FileUtils.createFile(savePath);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(title);
        request.setDescription(content);
        if (!StringUtils.isNullOrEmpty(headers)) {
            for (String key : headers.keySet()) {
                request.addRequestHeader(key, headers.get(key));
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresDeviceIdle(false);
            request.setRequiresCharging(false);
        }
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationUri(Uri.fromFile(new File(savePath)));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    //在广播中取出下载任务的id
                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (-1 != id) {
                        Uri uri = manager.getUriForDownloadedFile(id);
                        if (null != uri) {
                            OtherUtils.install(context, new File(uri.getPath()));
                        }
                    }
                }
                context.unregisterReceiver(this);
            }
        }, intentFilter);
    }


    /**
     * 文字通知
     *
     * @param context
     * @param title
     * @param content
     * @param clickToClass
     * @return
     */
    public static int showText(@NonNull Context context, @NonNull CharSequence title, @NonNull CharSequence content, @Nullable Map<String, Serializable> parameter, @Nullable Class<? extends Activity> clickToClass) {
        return showText(context, DEFAULT_SMALLICON, DEFAULT_LARGEICON, title, content, parameter, clickToClass);
    }


    /**
     * 文字通知
     *
     * @param context
     * @param title
     * @param content
     * @param clickToClass
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int showText(@NonNull Context context, @NonNull NotificationChannel channel, @NonNull CharSequence title, @NonNull CharSequence content, @Nullable Map<String, Serializable> parameter, @Nullable Class<? extends Activity> clickToClass) {
        return showText(context, channel, DEFAULT_SMALLICON, DEFAULT_LARGEICON, title, content, parameter, clickToClass);
    }

    /**
     * 文字通知
     *
     * @param context
     * @param smallIcon
     * @param largeIcon
     * @param title
     * @param content
     * @param clickToClass
     * @return
     */
    public static int showText(@NonNull Context context, @DrawableRes @NonNull int smallIcon, @NonNull Bitmap largeIcon, @NonNull CharSequence title, @NonNull CharSequence content, @Nullable Map<String, Serializable> parameter, @Nullable Class<? extends Activity> clickToClass) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(smallIcon);
        builder.setLargeIcon(largeIcon);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setTicker(StringUtils.getDateTimeNow("yyyy-MM-dd HH:mm:ss"));
        builder.setAutoCancel(true);
        if (null != largeIcon)
            builder.setLargeIcon(largeIcon);
        builder.setDefaults(Notification.DEFAULT_ALL);
        return show(context, builder, parameter, clickToClass);
    }

    /**
     * 文字通知
     *
     * @param context
     * @param smallIcon
     * @param largeIcon
     * @param title
     * @param content
     * @param clickToClass
     * @return
     */
    public static int showText(@NonNull Context context, NotificationChannel channel, @DrawableRes @NonNull int smallIcon, @NonNull Bitmap largeIcon, @NonNull CharSequence title, @NonNull CharSequence content, @Nullable Map<String, Serializable> parameter, @Nullable Class<? extends Activity> clickToClass) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(smallIcon);
        builder.setLargeIcon(largeIcon);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setTicker(StringUtils.getDateTimeNow("yyyy-MM-dd HH:mm:ss"));
        builder.setAutoCancel(true);
        if (null != largeIcon)
            builder.setLargeIcon(largeIcon);
        builder.setDefaults(Notification.DEFAULT_ALL);
        return show(context, builder, channel, parameter, clickToClass);
    }

    /**
     * 多文字通知
     *
     * @param context
     * @param title        通知标题
     * @param content      内容
     * @param allContent   全部内容
     * @param clickToClass 点击跳转页面
     * @return
     */
    public static int showText(@NonNull Context context, @NonNull CharSequence title, @NonNull CharSequence content, @NonNull CharSequence allContent, @Nullable Map<String, Serializable> parameter, @Nullable Class<? extends Activity> clickToClass) {
        return showText(context, DEFAULT_SMALLICON, DEFAULT_LARGEICON, title, content, allContent, parameter, clickToClass);
    }

    /**
     * 多文字通知
     *
     * @param context
     * @param smallIcon    状态栏小图标
     * @param largeIcon    状态栏下拉通知的图标
     * @param title        通知标题
     * @param content      内容
     * @param allContent   全部内容
     * @param clickToClass 点击跳转页面
     * @return
     */
    public static int showText(@NonNull Context context, @DrawableRes @NonNull int smallIcon, @NonNull Bitmap largeIcon, @NonNull CharSequence title, @NonNull CharSequence content, @NonNull CharSequence allContent, @Nullable Map<String, Serializable> parameter, @Nullable Class<? extends Activity> clickToClass) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(smallIcon);
        builder.setLargeIcon(largeIcon);
        builder.setContentTitle(title);
        builder.setContentText(content);
        Notification.BigTextStyle style = new Notification.BigTextStyle();
        style.setBigContentTitle(title);
        style.bigText(allContent);
        style.setSummaryText(content);
        builder.setStyle(style);
        builder.setTicker(StringUtils.getDateTimeNow("yyyy-MM-dd HH:mm:ss"));
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        return show(context, builder, parameter, clickToClass);
    }


    /**
     * 大图通知
     *
     * @param context
     * @param title
     * @param content
     * @param bigBitmap
     * @param clickToClass
     * @return
     */
    public static int showPicture(@NonNull Context context, @NonNull CharSequence title, @NonNull CharSequence content, @NonNull Bitmap bigBitmap, @Nullable Map<String, Serializable> parameter, @Nullable Class<? extends Activity> clickToClass) {
        return showPicture(context, DEFAULT_SMALLICON, DEFAULT_LARGEICON, title, content, bigBitmap, parameter, clickToClass);
    }

    /**
     * 大图通知
     *
     * @param context
     * @param title
     * @param content
     * @param bigBitmap
     * @param clickToClass
     * @return
     */
    public static int showPicture(@NonNull Context context, @NonNull NotificationChannel channel, @NonNull CharSequence title, @NonNull CharSequence content, @NonNull Bitmap bigBitmap, @Nullable Map<String, Serializable> parameter, @Nullable Class<? extends Activity> clickToClass) {
        return showPicture(context, channel, DEFAULT_SMALLICON, DEFAULT_LARGEICON, title, content, bigBitmap, parameter, clickToClass);
    }

    /**
     * 大图通知
     *
     * @param context
     * @param smallIcon
     * @param largeIcon
     * @param title
     * @param content
     * @param bigBitmap
     * @param clickToClass
     * @return
     */
    public static int showPicture(@NonNull Context context, @DrawableRes @NonNull int smallIcon, @NonNull Bitmap largeIcon, @NonNull CharSequence title, @NonNull CharSequence content, @NonNull Bitmap bigBitmap, @Nullable Map<String, Serializable> parameter, @Nullable Class<? extends Activity> clickToClass) {
        return showPicture(context, null, smallIcon, largeIcon, title, content, bigBitmap, parameter, clickToClass);
    }


    /**
     * 大图通知
     *
     * @param context
     * @param smallIcon
     * @param largeIcon
     * @param title
     * @param content
     * @param bigBitmap
     * @param clickToClass
     * @return
     */
    public static int showPicture(@NonNull Context context, NotificationChannel channel, @DrawableRes @NonNull int smallIcon, @NonNull Bitmap largeIcon, @NonNull CharSequence title, @NonNull CharSequence content, @NonNull Bitmap bigBitmap, @Nullable Map<String, Serializable> parameter, @Nullable Class<? extends Activity> clickToClass) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(smallIcon);
        builder.setLargeIcon(largeIcon);
        builder.setContentTitle(title);
        builder.setContentText(content);
        Notification.BigPictureStyle style = new Notification.BigPictureStyle();
        style.setBigContentTitle(title);
        style.bigPicture(bigBitmap);
        style.setSummaryText(content);
        builder.setStyle(style);
        builder.setTicker(StringUtils.getDateTimeNow("yyyy-MM-dd HH:mm:ss"));
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        return show(context, builder, channel, parameter, clickToClass);
    }

    /**
     * 通知简单处理
     *
     * @param context
     * @param builder
     * @return
     */
    public static int show(@NonNull Context context, @NonNull Notification.Builder builder, @Nullable Map<String, Serializable> parameter, @NonNull Class<? extends Activity> clickToClass) {
        return show(context, builder, null, parameter, clickToClass);
    }

    /**
     * 通知简单处理
     *
     * @param context
     * @param builder
     * @return
     */
    public static int show(@NonNull Context context, @NonNull Notification.Builder builder, NotificationChannel channel, @Nullable Map<String, Serializable> parameter, @NonNull Class<? extends Activity> clickToClass) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (null == channel) {
                channel = getChannelId("低版本", "兼容低版本通知", "该渠道是兼容低版本的通知");
                notificationManager.createNotificationChannel(channel);
            }
            builder.setChannelId(channel.getId());
        }
        Intent intent = new Intent(context, NotificationClickReceiver.class);
        intent.setAction(ACTION);
        intent.setPackage(context.getPackageName());
        Bundle bundle = new Bundle();
        bundle.putInt("notificationId", builder.hashCode());
        if (null != clickToClass) {
            bundle.putSerializable(KEY_CLASSNAME, clickToClass);
        }
        if (!StringUtils.isNullOrEmpty(parameter)) {
            Set<String> keys = parameter.keySet();
            for (String key : keys) {
                bundle.putSerializable(key, parameter.get(key));
            }
        }
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, builder.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setWhen(System.currentTimeMillis());
        notificationManager.notify(builder.hashCode(), builder.build());
        return builder.hashCode();
    }

    /**
     * 移除通知
     *
     * @param context
     * @param id
     */
    public static void remove(Context context, int id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }
}
