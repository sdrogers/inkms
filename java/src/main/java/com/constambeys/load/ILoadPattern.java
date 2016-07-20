package com.constambeys.load;

public interface ILoadPattern {

	/**
	 * @return a 2d array mapping the spectrum index of each pixel
	 */
	int[][] getDataStructure();

	/**
	 * @return the width in millimetres of the image 
	 */
	int getWidthMM();

	/**
	 * @return the height in millimetres of the image
	 */
	int getHeightMM();
}
