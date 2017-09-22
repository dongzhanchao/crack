package com.crack.me.active.ui.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.crack.me.active.ui.Loggable;

public class PublicKeyBytesReplacer extends JarReplacer {

    private Loggable log = null;

    private String defaultFileName = "publicKey.bytes";

    public PublicKeyBytesReplacer(Loggable log) {
	this.log = log;
    }

    public boolean replace(File f) {
	if (f == null) {
	    return false;
	}
	log.log("\treplacing file [" + f.getAbsolutePath() + "]");

	File bakFile = createBakFile(f);
	if (bakFile != null) {
	    JarOutputStream out = null;
	    try {
		if (bakFile != null) {
		    String defaultPackage = "";
		    JarFile bakJar = new JarFile(bakFile);
		    out = new JarOutputStream(new FileOutputStream(f));
		    Enumeration<JarEntry> jes = bakJar.entries();
		    while (jes.hasMoreElements()) {
			JarEntry j = jes.nextElement();
			String name = j.getName();
			if (name.endsWith(defaultFileName)) {
			    defaultPackage = name.substring(0, name.lastIndexOf('/'));
			    continue;
			}
			out.putNextEntry(j);
			InputStream in = bakJar.getInputStream(j);
			writeJarEntry(in, out);
		    }

		    addJarFile(//
			    defaultPackage + "/" + defaultFileName,//
			    new FileInputStream(defaultFileName), //
			    out);
		}
	    } catch (Exception e) {
		e.printStackTrace();
		return false;
	    } finally {
		if (out != null) {
		    try {
			out.close();
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
	    }
	}
	return false;
    }

    @Override
    public boolean isFileLegal(File f) {
	if (f == null) {
	    return false;
	}

	String fn = f.getName();
	if (fn.startsWith("com.genuitec.eclipse.core_")) {
	    ZipFile zipFile = null;
	    try {
		zipFile = new ZipFile(f);
		Enumeration<? extends ZipEntry> zes = zipFile.entries();
		while (zes.hasMoreElements()) {
		    ZipEntry ze = zes.nextElement();
		    String name = ze.getName();
		    if (name.endsWith(defaultFileName)) {
			return true;
		    }
		}

	    } catch (Exception e) {
		e.printStackTrace();
		return false;
	    } finally {
		try {
		    if (zipFile != null) {
			zipFile.close();
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}

	return false;
    }

    @Override
    public String getProcessInfo() {
	return "Replacing [publicKey.bytes].";
    }

}
