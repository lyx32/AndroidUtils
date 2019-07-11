package com.arraylist7.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 功能：缓存目录工具类<br>
 * 时间：2015年10月26日<br>
 * 备注：<br>
 *
 * @author ke
 */
@SuppressLint("SdCardPath")
public final class CacheUtils {

    CacheUtils() {
    }

    /**
     * 得到SD卡根目录
     *
     * @return
     */
    public static String getStorageDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();// 获取跟目录
        }
        return null;
    }

    /**
     * 创建SD卡跟目录下的一级目录
     *
     * @param appRootDirName
     * @return
     */
    public static String createAppRootDir(String appRootDirName) {
        File cacheFile = new File(getStorageDirectory() + File.separatorChar + appRootDirName);
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        return cacheFile.getAbsolutePath();
    }

    /**
     * 创建SD卡跟目录下的二级目录
     *
     * @param appRootDirName 一级目录
     * @param childDirName   二级目录
     * @return
     */
    public static String createAppOtherDir(String appRootDirName, String childDirName) {
        String dir = getStorageDirectory() + File.separatorChar + appRootDirName + File.separatorChar + childDirName;
        File cacheFile = new File(dir);
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        return cacheFile.getAbsolutePath();
    }

    /**
     * 获取内置缓存目录<br/>
     * 如果/android/data/xxx.xxx.xxx/cache不能读写，则返回/android/data/xxx.xxx.xxx/files
     *
     * @param context
     * @param dirName
     * @return
     */
    public static String getInternalCacheDir(Context context, String dirName) {
        String path = context.getExternalCacheDir().getAbsolutePath() + File.separator + dirName;
        File testFile = new File(path + File.separator + System.currentTimeMillis() + ".file");
        if (!testFile.getParentFile().exists())
            testFile.getParentFile().mkdirs();
        if (!testFile.exists()) {
            try {
                testFile.createNewFile();
            } catch (IOException e) {
            }
        }
        if (testFile.exists()) {
            testFile.delete();
            return path;
        }
        path = context.getExternalFilesDir(dirName).getAbsolutePath();
        testFile = new File(path + File.separator + System.currentTimeMillis() + ".file");
        if (!testFile.getParentFile().exists())
            testFile.getParentFile().mkdirs();
        if (!testFile.exists()) {
            try {
                testFile.createNewFile();
            } catch (IOException e) {
            }
        }
        if (testFile.exists()) {
            testFile.delete();
            return path;
        }
        return CacheUtils.createAppOtherDir(context.getPackageName(), dirName);
    }


    /**
     * 获取文件缓存目录
     *
     * @param context
     * @return
     */
    public static String getFileCachePath(Context context) {
        return getInternalCacheDir(context, "files");
    }

    /**
     * 清理文件缓存目录
     *
     * @param context
     * @return
     */
    public static void cleanFileCache(Context context) {
        deleteFile(new File(getInternalCacheDir(context, "files")));
    }

    /**
     * 获取内置图片缓存目录
     *
     * @param context
     * @return
     */
    public static String getImageCachePath(Context context) {
        return getInternalCacheDir(context, "images");
    }

    /**
     * 清理图片缓存目录
     *
     * @param context
     * @return
     */
    public static void cleanImageCache(Context context) {
        deleteFile(new File(getInternalCacheDir(context, "images")));
    }

    /**
     * 获取内置音频缓存目录
     *
     * @param context
     * @return
     */
    public static String getAudioCachePath(Context context) {
        return getInternalCacheDir(context, "audios");
    }

    /**
     * 清理音频缓存目录
     *
     * @param context
     * @return
     */
    public static void cleanAudioCache(Context context) {
        deleteFile(new File(getInternalCacheDir(context, "audios")));
    }

    /**
     * 获取内置视频缓存目录
     *
     * @param context
     * @return
     */
    public static String getVideoCachePath(Context context) {
        return getInternalCacheDir(context, "videos");
    }


    /**
     * 清理视频缓存目录
     *
     * @param context
     * @return
     */
    public static void cleanVideoCache(Context context) {
        deleteFile(new File(getInternalCacheDir(context, "videos")));
    }

    /**
     * 获取内置头像缓存目录
     *
     * @param context
     * @return
     */
    public static String getFaceCachePath(Context context) {
        return getInternalCacheDir(context, "faces");
    }

    /**
     * 清理头像缓存目录
     *
     * @param context
     * @return
     */
    public static void cleanFaceCache(Context context) {
        deleteFile(new File(getInternalCacheDir(context, "faces")));
    }

    /**
     * 获取内置WEB缓存目录
     *
     * @param context
     * @return
     */
    public static String getWebCachePath(Context context) {
        return getInternalCacheDir(context, "web");
    }


    /**
     * 清理WEB缓存目录
     *
     * @param context
     * @return
     */
    public static void cleanWebCache(Context context) {
        deleteFile(new File(getInternalCacheDir(context, "web")));
    }

    /**
     * 获取下载缓存目录
     *
     * @param context
     * @return
     */
    public static String getDownloadCachePath(Context context) {
        return getInternalCacheDir(context, "download");
    }

    /**
     * 清理下载缓存目录
     *
     * @param context
     * @return
     */
    public static void cleanDownloadCache(Context context) {
        deleteFile(new File(getInternalCacheDir(context, "download")));
    }

    /**
     * 内置缓存目录
     *
     * @param context
     * @param dirName 不传则清除所有，包含自定义目录
     */
    public static void cleanInternalCache(Context context, String dirName) {
        if (!StringUtils.isNullOrEmpty(dirName)) {
            deleteFile(new File(getInternalCacheDir(context, dirName)));
        } else {
            deleteFile(context.getFilesDir());
            deleteFile(context.getCacheDir());
            deleteFile(new File(createAppRootDir(context.getPackageName())));
        }
    }

    /**
     * * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * *
     *
     * @param context
     */
    public static void cleanDatabases(Context context) {
        deleteFile(new File("/data/data/" + context.getPackageName() + "/databases"));
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) *
     *
     * @param context
     */
    public static void cleanSharedPreference(Context context) {
        deleteFile(new File("/data/data/" + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * * 按名字清除本应用数据库 * *
     *
     * @param context
     * @param dbName
     */
    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    private static void deleteFile(File directory) {
        if (null != directory && directory.exists()) {
            if (directory.isFile()) {
                directory.delete();
            } else if (directory.isDirectory()) {
                for (File item : directory.listFiles()) {
                    deleteFile(item);
                }
            }
        }
    }

    /**
     * 获取缓存目录大小（xxxxxx，字节）
     *
     * @param cacheDir
     * @return
     */
    public static long getCacheDirSize(String cacheDir) {
        return FileUtils.getDirSize(new File(cacheDir));
    }

    /**
     * 获取缓存目录大小（xx.xxMB，带单位）
     *
     * @param cacheDir
     * @return
     */
    public static String getCacheDirSizeString(String cacheDir) {
        return FileUtils.formatFileSize(getCacheDirSize(cacheDir));
    }


    public static void cleanInvalidMemory(Context context) {
        int res = context.checkCallingOrSelfPermission("android.permission.KILL_BACKGROUND_PROCESSES");
        if (PackageManager.PERMISSION_GRANTED == res) {
            getAvailMemory(context);
            ActivityManager activityManger = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> list = activityManger.getRunningAppProcesses();
            if (list != null) {
                int close = 0;
                for (int i = 0; i < list.size(); i++) {
                    ActivityManager.RunningAppProcessInfo apinfo = list.get(i);
                    String[] pkgList = apinfo.pkgList;
                    if (apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                        for (int j = 0; j < pkgList.length; j++) {
                            activityManger.killBackgroundProcesses(pkgList[j]);
                        }
                        close++;
                    }
                }
                LogUtils.i("已关闭" + close + "个后台进程");
            }
            getAvailMemory(context);
        }
        System.gc();
    }

    public static long getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        LogUtils.d("可用内存---->>>" + (mi.availMem / (1024 * 1024)) + "M");
        return mi.availMem / (1024 * 1024);
    }


}