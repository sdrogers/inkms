package com.constambeys.ui.graph;

public class Utilities {

	/**
	 * Calculates the interval's decimal places
	 * <p>
	 * for example 10 order 0, 1 order 0, 0.12 order 1 ...
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
	 * for example 1.23 order 2 ...
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

}
