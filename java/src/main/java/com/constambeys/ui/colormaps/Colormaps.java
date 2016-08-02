package com.constambeys.ui.colormaps;

import java.awt.image.BufferedImage;

public class Colormaps {

	/**
	 * Converts a 2d array to buffered image. Uses the input colour map
	 * 
	 * @param intensity
	 *            2d array
	 * @return the image
	 */
	public static BufferedImage calculateImage(double[][] intensity, IColormap colormap) {

		int height = intensity.length;
		int width = intensity[0].length;

		double max = intensity[0][0];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (max < intensity[i][j]) {
					max = intensity[i][j];
				}
			}
		}

		int resolution = colormap.getMaxIndex();
		BufferedImage bufferImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// Color Image
				// int value = (int) (intensity[j][i] / max * 255);
				// Color color = new Color(value, value, value);
				// bufferImage.setRGB(i, j, color.getRGB());
				int indx = (int) (intensity[j][i] / max * resolution);
				bufferImage.setRGB(i, j, colormap.get(indx));
			}
		}

		return bufferImage;
	}
}
