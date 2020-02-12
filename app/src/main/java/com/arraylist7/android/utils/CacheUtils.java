package com.arraylist7.android.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

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
     * 得到SD卡目录
     *
     * @param type Environment.DIRECTORY_XXX
     * @return
     */
    @Deprecated
    public static String getStorageDirectory(Context context, String type) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStoragePublicDirectory(type).getAbsolutePath();// 获取跟目录
        }
        return null;
    }

    /**
     * 得到App私有根目录 /data/data/xxxx/
     *
     * @param context
     * @return
     */
    public static String getPrivateDirectory(Context context) {
        if (Build.VERSION.SDK_INT < 24)
            return context.getFilesDir().getAbsolutePath();
        return context.getDataDir().getAbsolutePath();
    }


    /**
     * 创建公有目录下的一级目录 /android/data/xxxx/files/appRootDirName
     *
     * @param appRootDirName
     * @return
     */
    public static String createAppRootDir(Context context, String appRootDirName) {
        File cacheFile = new File(getPublicCachePath(context, appRootDirName));
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        return cacheFile.getAbsolutePath();
    }

    /**
     * 创建公有目录下的二级目录 /android/data/xxxx/files/appRootDirName/childDirName
     *
     * @param appRootDirName 一级目录
     * @param childDirName   二级目录
     * @return
     */
    public static String createAppOtherDir(Context context, String appRootDirName, String childDirName) {
        String dir = getPublicCachePath(context, File.separatorChar + appRootDirName + File.separatorChar + childDirName);
        File cacheFile = new File(dir);
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        return cacheFile.getAbsolutePath();
    }

    /**
     * 获取应用程序私有缓存目录，/data/data/xxxx/caches
     *
     * @param context
     * @return
     */
    public static String getPrivateCachePath(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    /**
     * 获取应用程序私有缓存目录，/data/data/xxxx/caches/dirName
     *
     * @param context
     * @param dirName
     * @return
     */
    public static String getPrivateCachePath(Context context, String dirName) {
        return context.getCacheDir().getAbsolutePath() + "/" + dirName;
    }

    /**
     * 获取应用程序公共缓存目录，/android/data/xxxx/caches
     *
     * @param context
     * @return
     */
    public static String getPublicCachePath(Context context) {
        return context.getExternalCacheDir().getAbsolutePath();
    }

    /**
     * 获取应用程序公共缓存目录，/android/data/xxxx/caches/dirName
     *
     * @param context
     * @param dirName
     * @return
     */
    public static String getPublicCachePath(Context context, String dirName) {
        return context.getExternalCacheDir().getAbsolutePath() + "/" + dirName;
    }


    /**
     * 获取应用程序私有文件目录，/data/data/xxxx/files
     *
     * @param context
     * @return
     */
    public static String getPrivateFilePath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }


    /**
     * 获取应用程序私有文件目录，/data/data/xxxx/files/dirName
     *
     * @param context
     * @param dirName
     * @return
     */
    public static String getPrivateFilePath(Context context, String dirName) {
        return context.getFilesDir().getAbsolutePath() + "/" + dirName;
    }

    /**
     * 获取应用程序公共文件目录，/android/data/xxxx/files
     *
     * @param context
     * @return
     */
    public static String getPublicFilePath(Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath();
    }

    /**
     * 获取应用程序公共文件目录，/android/data/xxxx/files/dirName
     *
     * @param context
     * @param dirName
     * @return
     */
    public static String getPublicFilePath(Context context, String dirName) {
        return context.getExternalFilesDir(dirName).getAbsolutePath();
    }

    /**
     * 清理文件缓存目录（对应应用程序-清除缓存）
     *
     * @param context
     * @return
     */
    public static void cleanCache(Context context) {
        deleteFile(new File(getPublicCachePath(context)));
        deleteFile(new File(getPrivateCachePath(context)));
    }


    /**
     * 清理文件缓存目录（对应应用程序-清除数据）
     *
     * @param context
     * @return
     */
    public static void cleanData(Context context) {
        deleteFile(new File(getPublicFilePath(context)));
        if (Build.VERSION.SDK_INT < 24) {
            deleteFile(context.getCacheDir());
            deleteFile(context.getFilesDir());
        } else {
            deleteFile(context.getDataDir());
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
     * * 删除本应用数据库
     *
     * @param context
     * @param dbName
     */
    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }


    /**
     * 获取内置图片缓存目录
     *
     * @param context
     * @return
     */
    public static String getImageCachePath(Context context) {
        return getPublicCachePath(context, Environment.DIRECTORY_PICTURES);
    }

    /**
     * 清理图片缓存目录
     *
     * @param context
     * @return
     */
    public static void cleanImageCache(Context context) {
        deleteFile(new File(getPublicCachePath(context, Environment.DIRECTORY_PICTURES)));
    }

    /**
     * 获取内置音频缓存目录
     *
     * @param context
     * @return
     */
    public static String getAudioCachePath(Context context) {
        return getPublicCachePath(context, Environment.DIRECTORY_AUDIOBOOKS);
    }

    /**
     * 清理音频缓存目录
     *
     * @param context
     * @return
     */
    public static void cleanAudioCache(Context context) {
        deleteFile(new File(getPublicCachePath(context, Environment.DIRECTORY_AUDIOBOOKS)));
    }

    /**
     * 获取内置视频缓存目录
     *
     * @param context
     * @return
     */
    public static String getVideoCachePath(Context context) {
        return getPublicCachePath(context, Environment.DIRECTORY_MOVIES);
    }


    /**
     * 清理视频缓存目录
     *
     * @param context
     * @return
     */
    public static void cleanVideoCache(Context context) {
        deleteFile(new File(getPublicCachePath(context, Environment.DIRECTORY_MOVIES)));
    }

    /**
     * 获取内置头像缓存目录
     *
     * @param context
     * @return
     */
    public static String getFaceCachePath(Context context) {
        return getPublicCachePath(context, "faces");
    }

    /**
     * 清理头像缓存目录
     *
     * @param context
     * @return
     */
    public static void cleanFaceCache(Context context) {
        deleteFile(new File(getPublicCachePath(context, "faces")));
    }

    /**
     * 获取内置WEB缓存目录
     *
     * @param context
     * @return
     */
    public static String getWebCachePath(Context context) {
        return getPrivateCachePath(context, "web");
    }


    /**
     * 清理WEB缓存目录
     *
     * @param context
     * @return
     */
    public static void cleanWebCache(Context context) {
        deleteFile(new File(getPrivateCachePath(context, "web")));
    }

    /**
     * 获取下载目录
     *
     * @param context
     * @return
     */
    public static String getDownloadCachePath(Context context) {
        return getPublicCachePath(context, Environment.DIRECTORY_DOWNLOADS);
    }


    /**
     * 清理下载目录
     *
     * @param context
     * @return
     */
    public static void cleanDownloadCache(Context context) {
        deleteFile(new File(getPublicCachePath(context, Environment.DIRECTORY_DOWNLOADS)));
    }


    private static void deleteFile(File directory) {
        if (null != directory && directory.exists()) {
            if (directory.isFile() && directory.canWrite()) {
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


}