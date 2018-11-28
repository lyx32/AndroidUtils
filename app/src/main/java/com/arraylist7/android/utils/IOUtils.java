package com.arraylist7.android.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

public class IOUtils {
    IOUtils() {
    }


    public static byte[] reader(String path) throws IOException {
        InputStream in = fileIn(path);
        int len = in.available();
        byte[] b = new byte[len];
        in.read(b, 0, len);
        close(in);
        return b;
    }

    public static void writerAndClose(String path, byte[] b) throws IOException {
        writerAndClose(path, b, 0, b.length);
    }

    public static void writerAndClose(String path, byte[] b, int off, int len) throws IOException {
        OutputStream out = fileOut(path);
        out.write(b, off, len);
        close(out);
    }


    public static void writerAndClose(OutputStream out, byte[] b) throws IOException {
        writerAndClose(out, b, 0, b.length);
    }


    public static void writerAndClose(OutputStream out, byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        close(out);
    }

    public static void writerAndNoClose(String path, byte[] b) throws IOException {
        writerAndNoClose(path, b, 0, b.length);
    }

    public static void writerAndNoClose(String path, byte[] b, int off, int len) throws IOException {
        OutputStream out = fileOut(path);
        out.write(b, off, len);
    }


    public static void writerAndNoClose(OutputStream out, byte[] b) throws IOException {
        writerAndNoClose(out, b, 0, b.length);
    }

    public static void writerAndNoClose(OutputStream out, byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }


    public static InputStream fileIn(String path) throws FileNotFoundException {
        return fileIn(new File(path));
    }

    public static InputStream fileIn(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    public static OutputStream fileOut(String path) throws FileNotFoundException {
        return fileOut(new File(path));
    }

    public static OutputStream fileOut(File file) throws FileNotFoundException {
        return new FileOutputStream(file);
    }

    public static void readAndWriteAndClose(InputStream in, OutputStream out) throws IOException {
        if (null == in || null == out)
            return;
        byte[] b = new byte[65535];
        int len;
        while (-1 != (len = in.read(b))) {
            out.write(b, 0, len);
        }
        close(in);
        close(out);
    }

    public static void readAndWriterAndNoClose(InputStream in, OutputStream out) throws IOException {
        if (null == in || null == out)
            return;
        byte[] b = new byte[65535];
        int len;
        while (-1 != (len = in.read(b))) {
            out.write(b, 0, len);
        }
    }

    /**
     * 从一个文本流中读取全部内容并返回
     * <p>
     * <b style=color:red>注意</b>，它并不会关闭输出流
     *
     * @param reader 文本输出流
     * @return 文本内容
     * @throws IOException
     */
    public static StringBuilder getString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] cbuf = new char[65535];
        int len;
        while (-1 != (len = reader.read(cbuf))) {
            sb.append(cbuf, 0, len);
        }
        return sb;
    }

    /**
     * 从一个文本流中读取全部内容并返回
     * <p>
     * <b style=color:red>注意</b>，它并不会关闭输出流
     *
     * @param is 文本输出流
     * @return 文本内容
     * @throws IOException
     */
    public static StringBuilder getString(InputStream is) throws IOException {
        StringBuilder res = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader read = new BufferedReader(isr);
        String line;
        while (null != (line = read.readLine())) {
            res.append(line);
        }
        return res;
    }

    public static void close(Closeable io) {
        if (null != io) {
            try {
                io.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
