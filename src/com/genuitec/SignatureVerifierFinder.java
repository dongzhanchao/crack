package com.genuitec;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SignatureVerifierFinder {

    public boolean readEachJar(File jarFile) throws Exception {
	boolean isFounded = false;
	ZipFile zipFile = new ZipFile(jarFile);
	Enumeration<? extends ZipEntry> zes = zipFile.entries();
	List<ZipEntry> needFiles = new ArrayList<ZipEntry>(5);
	while (zes.hasMoreElements()) {
	    ZipEntry ze = zes.nextElement();
	    String name = ze.getName();
	    // if (name.contains("Signature")) {
	    // if (name.endsWith("publicKey.bytes")) {
	    if (name.contains("SignatureVerifier")) {
		isFounded = true;
		needFiles.add(ze);
	    }
	}

	if (isFounded) {
	    System.out.println(jarFile.getAbsolutePath());
	    for (ZipEntry zipEntry : needFiles) {
		System.out.println("\t" + zipEntry.getName());
	    }
	}

	return isFounded;
    }

    public void find(String filepath) throws Exception {
	List<File> files = getFiles(new File(filepath));
	for (File file : files) {
	    readEachJar(file);
	}
    }

    public List<File> getFiles(File file) {
	if (!file.exists()) {
	    return null;
	}
	if (file.isFile() && file.getName().endsWith("jar")) {
	    List<File> tmpFiles = new ArrayList<File>();
	    tmpFiles.add(file);
	    return tmpFiles;
	}

	File[] files = file.listFiles();
	if (files == null || files.length == 0) {
	    return null;
	}
	List<File> fff = new ArrayList<File>(100);
	for (File f : files) {
	    List<File> tmpFile = getFiles(f);
	    if (tmpFile != null) {
		fff.addAll(tmpFile);
	    }
	}
	return fff;
    }

    public File[] getSubFiles(File file) {
	return file.listFiles();
    }

    public static void main(String[] args) throws Exception {
	SignatureVerifierFinder svf = new SignatureVerifierFinder();
	svf.find("/Applications/MyEclipse/Common/plugins");
    }
}
