package com.constambeys.ui;

import java.awt.EventQueue;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.constambeys.load.Pattern1;
import com.constambeys.load.MSIImage;
import com.constambeys.python.IProgress;

import uk.ac.ebi.pride.tools.jmzreader.JMzReader;
import uk.ac.ebi.pride.tools.mzml_wrapper.MzMlWrapper;

public class Startup {

	public static boolean DEBUG = false;

	/**
	 * Debug Method used to load mass spectrometry file
	 * 
	 * @return
	 * @throws Exception
	 */
	public static MSIImage loadMZML() throws Exception {
		long startTime = System.nanoTime();
		JMzReader run = new MzMlWrapper(new File("E:\\Enironments\\data\\abcdefgh_1.mzML"));
		long estimatedTime = System.nanoTime() - startTime;
		System.out.println(String.format("%.3fs", estimatedTime / 1000000000.0));

		Pattern1.Param param = new Pattern1.Param();
		param.lines = 8;
		param.widthInMM = 62;
		param.heightInMM = 10;
		param.downMotionInMM = 1.25f;

		Pattern1 pattern = new Pattern1(run, param, Pattern1.Type.ALL);
		MSIImage loadMZXML = new MSIImage(run, pattern);
		return loadMZXML;

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