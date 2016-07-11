package com.constambeys.ui;

import java.awt.EventQueue;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.constambeys.python.IProgress;
import com.constambeys.python.LoadMZXML;

public class Startup {

	public static boolean DEBUG = true;

	public static LoadMZXML loadMZML(IProgress progressTracker) {
		LoadMZXML.Param param = new LoadMZXML.Param();
		param.filepath = "E:\\Enironments\\data\\abcdefgh_1.mzML";
		param.lines = 8;
		param.widthInMM = 62;
		param.heightInMM = 10;
		param.downMotionInMM = 1.25f;
		try {
			LoadMZXML loadMZXML = new LoadMZXML(param, LoadMZXML.Type.NORMAL);
			loadMZXML.setProgressListener(progressTracker);
			return loadMZXML;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

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
