package com.constambeys.ui.colormaps;

import java.awt.Color;

/**
 * Custom colour map
 * 
 * @author Constambeys
 *
 */
public class Custom implements IColormap {

	private int[] colormap = new int[200];

	public Custom(float hue) {

		for (int i = 0; i < 100; i++) {
			colormap[i] = Color.HSBtoRGB(hue, 1f, i / 100f);
		}

		float h = hue;
		float s = 1f;
		for (int i = 0; i < 100; i++) {
			h = h + 0.0016f;
			s = s - 0.01f;
			colormap[100 + i] = Color.HSBtoRGB(h, s, 1f);
		}

	}

	@Override
	public int getMaxIndex() {
		return colormap.length - 1;
	}

	@Override
	public int get(int indx) {
		return colormap[indx];
	}

}
