package com.constambeys.ui;

import java.awt.EventQueue;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Startup {
	
	public static Object sync = new Object();
	public static boolean DEBUG = false; 
	public static boolean DEBUG_SPECTRUM = false;

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Startup window = new Startup();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws Exception
	 */
	public Startup() throws Exception {
		frame = new FrameMain();
	}

	/**
	 * Loads an icon from resources and rescales it to the given {@code width} and {@code height}
	 * 
	 * @param name
	 *            the resource name
	 * @param width
	 *            the width to scale the image
	 * @param height
	 *            the height to scale the image
	 * @return
	 * @throws IOException
	 */
	public static ImageIcon loadIcon(String name, int width, int height) throws IOException {
		// Load the file from resource folder
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream(name);
		// Resize
		Image org = ImageIO.read(is);
		Image image = org.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(image);
	}
}
