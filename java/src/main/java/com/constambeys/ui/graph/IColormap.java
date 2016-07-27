package com.constambeys.ui.graph;

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
