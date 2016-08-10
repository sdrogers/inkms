package com.constambeys.patterns;

/**
 * Specifies the the mass spectrometer scanning pattern
 * 
 * @author Constambeys
 */
public interface ILoadPattern {

	/**
	 * @return a 2d array mapping the spectrum index of each pixel lines x columns
	 * @throws Exception
	 */
	int[][] getDataStructure() throws Exception;

	/**
	 * @return the width in millimetres of the image
	 */
	int getWidthMM();

	/**
	 * @return the height in millimetres of the image
	 */
	int getHeightMM();
}
