package com.constambeys.ui;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Stores application settings in the hard disk
 * 
 * @author Constambeys
 *
 */
public class Settings {

	Properties props = new Properties();

	/**
	 * Creates a new {@code Settings} class with filename user.properties
	 * 
	 */
	public Settings() {
		try {
			props.load(new FileReader("user.properties"));
		} catch (IOException e) {

		}
	}

	/**
	 * Saves the settings to file
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException {
		props.store(new FileWriter("user.properties"), "User properties");
	}

	/**
	 * Insert a new setting
	 * 
	 * @param key
	 *            a unique identifier
	 * @param value
	 */
	public void put(String key, String value) {
		props.setProperty(key, value);
	}

	/**
	 * Get setting
	 * 
	 * @param key
	 *            a unique identifier
	 * @return
	 */
	public String get(String key) {
		return props.getProperty(key);
	}
}
