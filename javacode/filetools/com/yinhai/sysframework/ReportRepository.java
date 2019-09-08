package com.yinhai.sysframework;

import java.io.File;
import java.util.Vector;

public class ReportRepository {

	private String repositoryPath;

	public ReportRepository(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}

	public File[] getReports(String path) {
		File dir = new File(repositoryPath + File.separator + path);
		File[] dirContents = dir.listFiles();

		Vector dirs = new Vector();
		Vector files = new Vector();

		for (int i = 0; i < dirContents.length; i++) {
			File file = dirContents[i];
			if (file.isDirectory()) {
				dirs.add(file);
			} else if (file.getName().endsWith(".jasper")) {
				files.add(file);
			}
		}

		java.util.Collections.sort(dirs);
		java.util.Collections.sort(files);
		dirs.addAll(files);
		return (File[]) dirs.toArray(new File[dirs.size()]);
	}

	public File[] getFileList(String path) {
		File dir = new File(repositoryPath + File.separator + path);
		File[] dirContents = dir.listFiles();

		Vector dirs = new Vector();
		Vector files = new Vector();

		for (int i = 0; i < dirContents.length; i++) {
			File file = dirContents[i];
			if (file.isDirectory()) {
				dirs.add(file);
			} else {
				files.add(file);
			}
		}

		java.util.Collections.sort(dirs);
		java.util.Collections.sort(files);
		dirs.addAll(files);
		return (File[]) dirs.toArray(new File[dirs.size()]);
	}

	public void deleteFile(String fileName) {
		File file = new File(repositoryPath + File.separator + fileName);
		if (file.exists()) {
			if (file.isDirectory())
				deleteSubdirectories(file);
			file.delete();
		}
	}

	public void createDirectory(String currentDir, String dirName) {
		currentDir = repositoryPath + File.separator + currentDir;
		File dir = new File(currentDir);
		if (dir.exists()) {
			File newDir = new File(dir, dirName);
			newDir.mkdir();
		}
	}

	private void deleteSubdirectories(File dir) {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory())
				deleteSubdirectories(files[i]);
			files[i].delete();
		}
	}
}
