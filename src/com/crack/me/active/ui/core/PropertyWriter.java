package com.crack.me.active.ui.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import com.crack.me.active.ui.Loggable;

public class PropertyWriter {
	private Loggable log = null;

	public PropertyWriter(Loggable log) {
		this.log = log;
	}

	public void write(String[][] data) {
		String home = System.getProperty("user.home");
		write(data, new File(home + "/.myeclipse.properties"));
	}

	public void write(String[][] data, String file) {
		write(data, new File(file));
	}

	@SuppressWarnings("deprecation")
	public void write(String[][] data, File file) {
		if (data == null) {
			return;
		}
		BufferedWriter bw = null;
		log.log("Writing to license File [" + file.getAbsolutePath() + "]");
		try {
			bw = new BufferedWriter(new FileWriter(file));
			bw.write("##MyEclipse license file");
			bw.newLine();
			bw.write("# " + new Date().toLocaleString());
			bw.newLine();
			for (String[] keyV : data) {
				bw.write(keyV[0] + "=" + keyV[1]);
				bw.newLine();
			}
			bw.write("# Activation keys are uniquely generated on a per-machine basis");
			bw.newLine();
			bw.write("# and cannot be copied to another machine.");
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
