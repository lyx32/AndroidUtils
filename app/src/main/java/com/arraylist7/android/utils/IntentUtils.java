package com.arraylist7.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IntentUtils {
    public static final String DATA_BUNDLE_KEY = "androd_utils_intent_data";

    IntentUtils() {
    }

    public static void activity(Activity form, Class<? extends Activity> to, boolean isFinish) {
        activity(form, to, null, isFinish);
    }

    public static void activity(Activity form, Class<? extends Activity> to, Bundle bundle, boolean isFinish) {
        Intent intent = new Intent(form, to);
        if (null != bundle) {
            intent.putExtra(DATA_BUNDLE_KEY, bundle);
        }
        form.startActivity(intent);
        form.overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
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
            intent.putExtra(DATA_BUNDLE_KEY, bundle);
        }
        form.startActivityForResult(intent, requestCode);
        form.overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
    }

    public static void finish(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
    }

    public static void marker(Context context, String packageName) {
        Intent installIntent = new Intent("android.intent.action.VIEW");
        installIntent.setData(Uri.parse("market://details?id=" + packageName));
        context.startActivity(installIntent);
    }

    public static void openBrowser(Activity activity, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
    }

    public static void openPhone(Activity activity, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
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
        activity.overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
    }

    public static void openImageChoose(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, "选择图片"), requestCode);
        activity.overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
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
