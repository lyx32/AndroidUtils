package com.arraylist7.android.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.transition.Transition;
import android.transition.TransitionInflater;

import java.io.File;

public class IntentUtils {

    IntentUtils() {
    }


    public static void activity(Activity form, Class<? extends Activity> to, boolean isFinish) {
        activity(form, to, null, isFinish);
    }

    public static void activity(Activity form, Class<? extends Activity> to, Bundle bundle, boolean isFinish) {
        Intent intent = new Intent(form, to);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        try {
            form.startActivity(intent);
        } catch (ActivityNotFoundException exception) {
            LogUtils.e("没有找到" + to.getClass().getName() + " ", exception);
        }
        if (isFinish) {
            finish(form);
        }
    }

    public static void activityForResult(Activity form, Class<? extends Activity> to, int requestCode) {
        activityForResult(form, to, null, requestCode);
    }

    public static void activityForResult(Activity form, Class<? extends Activity> to, Bundle bundle, int requestCode) {
        Intent intent = new Intent(form, to);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        form.startActivityForResult(intent, requestCode);
    }

    public static void setResult(Activity form, int resultCode, boolean isFinish) {
        setResult(form, resultCode, null, isFinish);
    }

    public static void setResult(Activity form, int resultCode, Bundle bundle, boolean isFinish) {
        Intent intent = new Intent();
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        form.setResult(resultCode, intent);
        if (isFinish) finish(form);
    }

    public static void finish(Activity activity) {
        activity.finish();
    }

    public static void marker(Context context, String packageName) {
        Intent installIntent = new Intent("android.intent.action.VIEW");
        installIntent.setData(Uri.parse("market://details?id=" + packageName));
        context.startActivity(installIntent);
    }

    public static void openWifi(Activity form) {
        form.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    public static void open4G(Activity form) {
        form.startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
    }

    public static void openBrowser(Activity activity, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    public static void openPhone(Activity activity, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(phone.startsWith("tel:") ? phone : "tel:" + phone));
        activity.startActivity(intent);
    }

    public static void openCamera(Activity activity, int requestCode, String fileAbsolutePath) {
        File file = new File(fileAbsolutePath);
        if (null != file) {
            LogUtils.e("保存文件错误：" + fileAbsolutePath);
            return;
        }
        File dirctory = file.getParentFile();
        if (!dirctory.exists()) {
            dirctory.mkdirs();
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(dirctory, file.getName())));
        activity.startActivityForResult(intent, requestCode);
    }

    public static void openImageChoose(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, "选择图片"), requestCode);
    }

    public static String getChooseImagePath(Activity activity, Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor c = activity.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePathColumns[0]);
        String path = c.getString(columnIndex);
        c.close();
        return path;
    }
}
