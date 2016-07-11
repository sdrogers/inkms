package com.constambeys.ui;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Settings {

	String filepath;
	Properties props = new Properties();

	public static Settings getAppSettings() {
		return new Settings("user.properties");
	}

	public Settings(String filepath) {
		try {
			this.filepath = filepath;
			props.load(new FileReader(filepath));
		} catch (IOException e) {

		}
	}

	public void save() throws IOException {
		props.store(new FileWriter(filepath), "User properties");
	}

	public void put(String key, String value) {
		props.setProperty(key, value);
	}

	public String get(String key) {
		return props.getProperty(key);
	}
}
