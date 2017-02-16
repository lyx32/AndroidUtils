package com.arraylist7.android.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.Channel;

public class IOUtils {
	IOUtils() {
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
	
	public static long readAndWriter(InputStream in,OutputStream out) throws IOException{
		if(null == in || null == out) return -1;
		byte[] buf = new byte[65535];
		int len;
		long bytesCount = 0;
		while (-1 != (len = in.read(buf))) {
			bytesCount += len;
			out.write(buf, 0, len);
		}
		if (0 == bytesCount) {
			out.write(buf, 0, 0);
		}
		out.flush();
		return bytesCount;
	}

	/**
	 * 从一个文本流中读取全部内容并返回
	 * <p>
	 * <b style=color:red>注意</b>，它并不会关闭输出流
	 * 
	 * @param reader
	 *            文本输出流
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
	 * @param is
	 *            文本输出流
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

	/**
	 * 将文本输入流写入一个文本输出流。块大小为 65535
	 * <p>
	 * <b style=color:red>注意</b>，它并不会关闭输入/出流
	 * 
	 * @param writer
	 *            输出流
	 * @param reader
	 *            输入流
	 * @throws IOException
	 */
	public static void readAndWrite(Writer writer, Reader reader) throws IOException {
		if (null == writer || null == reader)
			return;

		char[] cbuf = new char[65535];
		int len;
		while (-1 != (len = reader.read(cbuf))) {
			writer.write(cbuf, 0, len);
		}
	}

	public static void close(InputStream in) {
		if (null != in) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void close(OutputStream out) {
		if (null != out) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void close(Reader reader) {
		if (null != reader) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void close(Writer writer) {
		if (null != writer) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void close(Channel channel) {
		if (null != channel) {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
