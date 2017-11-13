package com.justin.utils;

import java.io.File;
import java.text.DecimalFormat;

public final class FileUtils {
	public static long fileLen = 0;

	public static void delFilesFromPath(File filePath) {
		File[] files = filePath.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				files[i].delete();
			} else {
				delFilesFromPath(files[i]);
				files[i].delete();// 刪除文件夾
			}
		}
	}
	
	public static String size(File filePath) {
		long fileLen2 = getFileLen(filePath);
		String size = size(fileLen2);
		return size;
	}

	private static long getFileLen(File filePath) {
		fileLen = 0;
		return getFileLenFromPath(filePath);
	}

	private static long getFileLenFromPath(File filePath) {
		File[] files = filePath.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				fileLen += files[i].length();
			} else {
				getFileLenFromPath(files[i]);
			}
		}
		return fileLen;
	}

	private FileUtils() {
	}

	private static String size(long size) {

		if (size / (1024 * 1024 * 1024) > 0) {
			float tmpSize = (float) (size) / (float) (1024 * 1024 * 1024);
			DecimalFormat df = new DecimalFormat("#.##");
			return "" + df.format(tmpSize) + "GB";
		} else if (size / (1024 * 1024) > 0) {
			float tmpSize = (float) (size) / (float) (1024 * 1024);
			DecimalFormat df = new DecimalFormat("#.##");
			return "" + df.format(tmpSize) + "MB";
		} else if (size / 1024 > 0) {
			return "" + (size / (1024)) + "KB";
		} else
			return "" + size + "B";
	}
}
