package com.arraylist7.android.utils;


import android.content.Context;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能：使用NIO的文件工具类<br>
 * 时间：2015年10月26日<br>
 * 备注：<br>
 *
 * @author ke
 */
public final class FileUtils {

    FileUtils() {
    }


    public static void createFile(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) {
            if (f.getParentFile().exists())
                f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Uri getFileUri(Context context, String path) {
        return FileProvider.getUriForFile(context, "com.arraylist7.android.utils.FileProvider", new File(path));
    }

    public static String getFileName(String filePath) {
        if (StringUtils.isNullOrEmpty(filePath))
            return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    public static String getFileFormat(String fileName) {
        if (StringUtils.isNullOrEmpty(fileName))
            return "";
        int point = fileName.lastIndexOf('.');
        return fileName.substring(point + 1);
    }


    /**
     * 将文件大小转成中文大小（10.05KB，10.05MB）
     *
     * @param fileS
     * @return
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        if (!dir.exists()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        if (StringUtils.isNullOrEmpty(files))
            return 0L;
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }


    /**
     * 读取文件全部内容
     *
     * @param path 读取文件路径
     * @return
     */
    public static String readerFile(String path) {
        return readerFile(new File(path), Charset.defaultCharset().name());
    }

    /**
     * 读取文件全部内容
     *
     * @param path    读取文件路径
     * @param charset 读取编码
     * @return
     */
    public static String readerFile(String path, String charset) {
        return readerFile(new File(path), charset);
    }

    /**
     * 读取文件全部内容
     *
     * @param file 读取文件
     * @return
     */
    public static String readerFile(File file) {
        return readerFile(file, Charset.defaultCharset().name());
    }


    /**
     * 读取文件全部内容
     *
     * @param file    读取文件
     * @param charset 读取编码
     * @return
     */
    public static String readerFile(File file, String charset) {
        if (null == file) {
            LogUtils.e("file 对象不能为空！");
            return null;
        }
        if (!file.exists()) {
            LogUtils.e(file.getAbsolutePath() + " 不存在！");
            return null;
        }
        if (!file.canRead()) {
            LogUtils.e(file.getAbsolutePath() + " 没有读取权限！");
            return null;
        }
        StringBuffer result = new StringBuffer();
        FileInputStream fis = null;
        FileChannel channel = null;
        try {
            fis = new FileInputStream(file);
            channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(65535);
            int size = 0;
            while (0 < (size = channel.read(buffer))) {
                result.append(new String(buffer.array(), 0, size, Charset.forName(charset)));
                buffer.flip();
            }
            buffer.clear();
            buffer = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fis);
            IOUtils.close(channel);
        }
        return result.toString();
    }


    /**
     * 读取文件指定多少行
     *
     * @param path 读取文件路径
     * @param top  读取行数
     * @return
     */
    public static String[] readerTopLines(String path, int top) {
        return readerTopLines(new File(path), Charset.defaultCharset().name(), top);
    }

    /**
     * 读取文件指定多少行
     *
     * @param file 读取文件
     * @param top  读取行数
     * @return
     */
    public static String[] readerTopLines(File file, int top) {
        return readerTopLines(file, Charset.defaultCharset().name(), top);
    }

    /**
     * 读取文件指定多少行
     *
     * @param path    读取文件路径
     * @param charset 读取编码
     * @param top     读取行数
     * @return
     */
    public static String[] readerTopLines(String path, String charset, int top) {
        return readerTopLines(new File(path), charset, top);
    }

    /**
     * 读取文件指定多少行
     *
     * @param file    读取文件
     * @param charset 读取编码
     * @param top     读取行数
     * @return
     */
    public static String[] readerTopLines(File file, String charset, int top) {
        if (null == file) {
            LogUtils.e("file 对象不能为空！");
            return null;
        }
        if (!file.exists()) {
            LogUtils.e(file.getAbsolutePath() + " 不存在！");
            return null;
        }
        if (!file.canRead()) {
            LogUtils.e(file.getAbsolutePath() + " 没有读取权限！");
            return null;
        }
        List<String> list = new ArrayList<>();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, charset);
            reader = new BufferedReader(isr);
            String line = null;
            int index = 0;
            while (index != top && null != (line = reader.readLine())) {
                list.add(line);
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(reader);
            IOUtils.close(isr);
            IOUtils.close(fis);
        }
        return list.toArray(new String[]{});
    }


    /**
     * 将文件按照真实行数读取
     *
     * @param path 读取文件路径
     * @return
     */
    public static String[] readerLines(String path) {
        return readerLines(new File(path), Charset.defaultCharset().name());
    }

    /**
     * 将文件按照真实行数读取
     *
     * @param path    读取文件路径
     * @param charset 读取编码
     * @return
     */
    public static String[] readerLines(String path, String charset) {
        return readerLines(new File(path), charset);
    }

    /**
     * 将文件按照真实行数读取
     *
     * @param file 读取文件
     * @return
     */
    public static String[] readerLines(File file) {
        return readerLines(file, Charset.defaultCharset().name());
    }

    /**
     * 将文件按照真实行数读取
     *
     * @param file    读取文件
     * @param charset 读取编码
     * @return
     */
    public static String[] readerLines(File file, String charset) {
        if (null == file) {
            LogUtils.e("file 对象不能为空！");
            return null;
        }
        if (!file.exists()) {
            LogUtils.e(file.getAbsolutePath() + " 不存在！");
            return null;
        }
        if (!file.canRead()) {
            LogUtils.e(file.getAbsolutePath() + " 没有读取权限！");
            return null;
        }
        List<String> list = new ArrayList<>();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, charset);
            reader = new BufferedReader(isr);
            String line = null;
            while (null != (line = reader.readLine())) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(reader);
            IOUtils.close(fis);
        }
        return list.toArray(new String[]{});
    }


    public static void writerFile(String path, String content) throws UnsupportedEncodingException {
        writerFile(path, content, Charset.defaultCharset().name(), false);
    }

    public static void writerFile(String path, String content, String charset) throws UnsupportedEncodingException {
        writerFile(path, content, charset, false);
    }

    public static void writerFile(String path, String content, boolean isAppend) throws UnsupportedEncodingException {
        writerFile(path, content, Charset.defaultCharset().name(), isAppend);
    }

    public static void writerFile(String path, String content, String charset, boolean isAppend) throws UnsupportedEncodingException {
        writerFile(path, content.getBytes(charset), isAppend);
    }

    public static void writerFile(String path, byte[] content) {
        writerFile(path, content, false);
    }

    public static void writerFile(String path, byte[] content, boolean isAppend) {
        File file = new File(path);
        if (!file.getParentFile().exists())
            file.mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                LogUtils.e("创建文件 " + file.getAbsolutePath() + " 失败", e);
                return;
            }
        }
        if (!file.canWrite()) {
            LogUtils.e(file.getAbsolutePath() + " 没有写入权限！");
            return;
        }
        FileOutputStream fos = null;
        FileChannel channel = null;
        try {
            fos = new FileOutputStream(file, isAppend);
            channel = fos.getChannel();
            ByteBuffer buffer = ByteBuffer.wrap(content);
            channel.write(buffer);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fos);
            IOUtils.close(channel);
        }
    }

    public static void copyFile(String sourceFilePath, String newFilePath) {
        try {
            copyFile(IOUtils.fileIn(sourceFilePath), IOUtils.fileOut(newFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void copyFile(InputStream sourceFileStream, String newFilePath) {
        try {
            copyFile(sourceFileStream, IOUtils.fileOut(newFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void copyFile(InputStream sourceFileStream, OutputStream newFileStream) {

        if (null == sourceFileStream) {
            LogUtils.e("sourceFileStream 不能为空");
            return;
        }
        if (null == newFileStream) {
            LogUtils.e("newFileStream 不能为空");
            return;
        }

        try {
            byte[] b = new byte[65535];
            int size = 0;
            while (0 < (size = sourceFileStream.read(b))) {
                newFileStream.write(b, 0, size);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(sourceFileStream);
            IOUtils.close(newFileStream);
        }
    }


    public static void deleteFile(File fileOrDir) {
        if (!fileOrDir.exists()) {
            return;
        }
        if (fileOrDir.isFile()) {
            fileOrDir.delete();
        } else if (fileOrDir.isDirectory()) {
            File[] files = fileOrDir.listFiles();
            if (StringUtils.isNullOrEmpty(files))
                return;
            for (File file : files)
                deleteFile(file);
        }
    }


}