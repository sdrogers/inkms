package com.constambeys.ui.colormaps;

import java.awt.Color;

public class Gray implements IColormap {

	private int[] colormap = new int[256];

	public Gray() {

		for (int i = 0; i < 256; i++) {
			colormap[i] = new Color(i, i, i).getRGB();
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
