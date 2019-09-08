package com.yinhai.sysframework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUploadUtil {

	private static final int MAX_SIZE = 1073741824;
	private static final String[] TYPES = { ".jpg", ".gif", ".zip" };
	private static final String imageFolder = "yhimages";

	public static void saveFile(String folderName, String fileName, byte[] fileData, int size)
			throws FileNotFoundException, IOException {
		if (!checkSize(size)) {
			throw new IOException(size + "文件太大 !");
		}

		if (!checkType(fileName)) {
			throw new IOException("不支持文件上传类型 !");
		}
		saveToFile(folderName, fileName, fileData);
	}

	private static void saveToFile(String folderName, String fileName, byte[] fileData) throws FileNotFoundException,
			IOException {
		File fl = new File("yhimages/" + folderName);
		if (!fl.exists())
			fl.mkdirs();
		fl = null;
		fl = new File("yhimages/" + folderName + "/" + fileName);
		fl.deleteOnExit();
		fl = null;
		OutputStream o = new FileOutputStream("yhimages/" + folderName + "/" + fileName);
		o.write(fileData);
		o.close();

		if (fileName.endsWith("zip")) {

			File filezip = new File("yhimages/" + folderName + "/" + fileName);
			if (filezip.exists()) {
				ZipInputStream zipIn = new ZipInputStream(new FileInputStream(filezip));

				for (;;) {
					ZipEntry zipentry = zipIn.getNextEntry();
					if (zipentry == null) {
						break;
					}

					if (!zipentry.isDirectory()) {
						String tFileName = zipentry.getName();

						String tFolderName = tFileName.substring(0, 4);
						File tfl = new File("yhimages/" + tFolderName);
						if (!tfl.exists())
							tfl.mkdirs();
						tfl = null;

						OutputStream to = new FileOutputStream("yhimages/" + tFolderName + "/" + tFileName);
						int count = -1;
						int total = 0;
						byte[] data = new byte['?'];
						while ((count = zipIn.read(data)) != -1) {
							total += count;
							to.write(data, 0, count);
						}
						to.close();
					}
					zipIn.closeEntry();
				}
				zipIn.close();
				filezip.deleteOnExit();
			}
		}
	}

	public static void delFile(String fileName, String dest) throws NullPointerException, SecurityException {
		delFromFile(fileName);
	}

	private static void delFromFile(String fileName) throws NullPointerException, SecurityException {
		File file = new File(fileName);
		if (file.exists())
			file.delete();
	}

	private static boolean checkSize(int size) {
		if (size > 1073741824)
			return false;
		return true;
	}

	private static boolean checkType(String fileName) {
		for (int i = 0; i < TYPES.length; i++) {
			if (fileName.toLowerCase().endsWith(TYPES[i])) {
				return true;
			}
		}
		return false;
	}
}
