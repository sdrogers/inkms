package com.constambeys.ui.graph;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Utilities {

	/**
	 * Calculates the interval's decimal places
	 * <p>
	 * for example 10 interval has order 0, 1 interval has order 0, 0.12 interval has order 1 ...
	 *
	 * @param interval
	 * @return order
	 */
	public static int calculateFirstDesimalPlace(double interval) {
		int accuracy = 0;
		while (interval < 1 && accuracy < 6) {
			interval = interval * 10;
			accuracy++;
		}

		return accuracy;
	}

	/**
	 * Calculates the interval's decimal places
	 * <p>
	 * for example 1.23 interval has order 2 ...
	 *
	 * @param interval
	 * @return order
	 */
	public static int calculateDesimalPlaces(double interval) {
		// Get decimal number
		interval = interval - Math.floor(interval);
		int accuracy = 0;
		while (interval != 0 && accuracy < 6) {
			interval = interval * 10;
			// Get decimal number
			interval = interval - Math.floor(interval);
			accuracy++;
		}

		return accuracy;
	}

	public static BufferedImage copyImage(BufferedImage source) {
		BufferedImage destination = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		Graphics g = destination.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return destination;
	}

}
