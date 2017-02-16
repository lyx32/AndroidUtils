package com.arraylist7.android.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 
 * 
 * 功能：使用NIO的文件工具类<br>
 * 时间：2015年10月26日<br>
 * 备注：<br>
 * 
 * @author ke
 * 
 */
public final class FileUtils {

	FileUtils() {
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

	public static String readerFile(String path) throws IOException {
		return readerFile(new File(path));
	}

	public static String readerFile(File file) throws IOException {
		if (null == file) {
			throw new NullPointerException();
		}
		if (!file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}
		if (!file.canRead()) {
			throw new RuntimeException(file.getAbsolutePath() + "没有读取权限");
		}
		FileInputStream fis = new FileInputStream(file);
		FileChannel channel = fis.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(10086);
		channel.read(buffer);
		long end = channel.size();
		byte[] bt = buffer.array();
		buffer.clear();
		String str = new String(bt, 0, (int)end);
		bt = null;
		buffer.clear();
		buffer = null;
		IOUtils.close(fis);
		IOUtils.close(channel);
		return str;
	}

	public static void writeFile(String path, String content) throws Exception {
		writeFile(path, content, false);
	}

	public static void writeFile(String path, byte[] content) throws Exception {
		writeFile(path, content, false);
	}

	public static void writeFile(String path, String content, boolean isAppend)
			throws Exception {
		writeFile(path, content.getBytes(), isAppend);
	}

	public static void writeFile(String path, byte[] content, boolean isAppend)
			throws Exception {
		File file = new File(path);
		if (!file.getParentFile().exists())
			file.mkdirs();
		if (!file.exists()) {
			file.createNewFile();
		}
		if (!file.canWrite()) {
			throw new RuntimeException(path + "没有写入权限");
		}
		FileOutputStream fos = new FileOutputStream(file, isAppend);
		FileChannel channel = fos.getChannel();
		ByteBuffer buffer = ByteBuffer.wrap(content);
		channel.write(buffer);
		fos.flush();
		IOUtils.close(fos);
		IOUtils.close(channel);
	}

	public static void copyFile(String path, String newPath) {
		File source = new File(path);
		File newFile = new File(newPath);
		if (!newFile.exists()) {
			try {
				newFile.createNewFile();
			} catch (IOException e) {
				LogUtils.e("创建文件失败！", e);
			}
		}
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel sc = null;
		FileChannel nc = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(newFile);
			sc = fis.getChannel();
			nc = fos.getChannel();
			nc.transferFrom(sc, 0, sc.size());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.close(fis);
			IOUtils.close(fos);
			IOUtils.close(sc);
			IOUtils.close(nc);
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
			for (File file : files)
				deleteFile(file);
		}
	}

}