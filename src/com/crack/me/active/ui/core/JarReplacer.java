package com.crack.me.active.ui.core;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public abstract class JarReplacer {

    public final static FileFilter jarFileFilter = new FileFilter() {
	public boolean accept(File paramFile) {
	    return paramFile.isDirectory() || paramFile.getName().endsWith(".jar");
	}
    };

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

    public boolean replace(List<File> fs) {
	if (fs == null) {
	    return false;
	}
	boolean success = true;
	for (File file : fs) {
	    if (isFileLegal(file)) {
		success &= replace(file);
	    }
	}
	return success;
    }

    public abstract boolean replace(File f);

    public abstract boolean isFileLegal(File f);

    public abstract String getProcessInfo();

    public static FileFilter getFileFilter() {
	return jarFileFilter;
    }

    public List<File> findLegalFiles(List<File> files) {
	if (files == null) {
	    return null;
	}
	List<File> needFiles = new ArrayList<File>();
	for (File file : files) {
	    if (isFileLegal(file)) {
		needFiles.add(file);
	    }
	}
	return needFiles;
    }

    /**
     * 创建备份文件
     * 
     * @param file
     * @return
     */
    public File createBakFile(File file) {
	String lastName = ".bak" + sdf.format(new Date());
	String bakFileName = file.getAbsoluteFile() + lastName;
	File newFile = new File(bakFileName);

	InputStream in = null;
	OutputStream out = null;
	try {
	    in = new FileInputStream(file);
	    out = new FileOutputStream(newFile);
	    writeStream(in, out);
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	} finally {
	    try {
		if (in != null) {
		    in.close();
		}
		if (out != null) {
		    out.close();
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	return newFile;
    }

    /**
     *  写入流
     * 
     * @param in
     * @param out
     * @throws IOException
     */
    public void writeStream(InputStream in, OutputStream out) throws IOException {
	byte[] data = new byte[1024];
	int len = -1;
	while ((len = in.read(data)) != -1) {
	    out.write(data, 0, len);
	}
	out.flush();
    }

    public void writeJarEntry(InputStream in, JarOutputStream out) throws IOException {
	writeStream(in, out);
	in.close();
	out.flush();
	out.closeEntry();
    }

    public void addJarFile(String entryName, InputStream in, JarOutputStream out) throws IOException {
	JarEntry je = new JarEntry(entryName);
	out.putNextEntry(je);
	writeJarEntry(in, out);
    }

    public void addJarFile(String className, JarOutputStream out) throws IOException {
	addJarFile(//
		className,//
		ClassLoader.getSystemResourceAsStream(className),//
		out);
    }

}
