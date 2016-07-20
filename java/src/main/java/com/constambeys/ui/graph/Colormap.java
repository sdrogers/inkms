package com.constambeys.ui.graph;

import java.awt.Color;

public class Colormap {

	public static int[] hot = hot(1024);

	/**
	 * Creates a hot colour map of the given size
	 * 
	 * @param resolution
	 *            the number of colours
	 * @return {@link Color} converted to RGB
	 */
	private static int[] hot(int resolution) {
		// http://www.codeproject.com/Articles/18150/Create-Custom-Color-Maps-in-C
		int[] hot = new int[resolution];
		float r[] = new float[resolution];
		float g[] = new float[resolution];
		float b[] = new float[resolution];

		int k = (int) ((float) resolution / 8 * 3);

		for (int i = 0; i < resolution; i++) {
			if (i < k)
				r[i] = (float) ((i + 1.0) / k);
			else
				r[i] = 1.0f;

			if (i < k)
				g[i] = 0.0f;
			else if (i >= k && i < 2 * k)
				g[i] = (float) ((i + 1.0 - k) / k);
			else
				g[i] = 1.0f;

			if (i < 2 * k)
				b[i] = 0f;
			else
				b[i] = (float) ((i + 1.0 - 2 * k) / (resolution - 2 * k));
		}

		for (int i = 0; i < resolution; i++) {
			hot[i] = new Color(r[i], g[i], b[i], 1.0f).getRGB();
		}

		return hot;
	}

}
