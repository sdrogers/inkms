package com.constambeys.ui.colormaps;

public interface IColormap {

	/**
	 * @return the maximum allowed index
	 */
	public int getMaxIndex();

	/**
	 * @param indx
	 * @return colour
	 */
	public int get(int indx);
}
