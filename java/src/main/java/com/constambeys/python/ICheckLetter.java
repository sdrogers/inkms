package com.constambeys.python;

/**
 * This interface abstractly defines the interested image area
 * 
 * @author Constambeys
 * 
 */
public interface ICheckLetter {

	/**
	 * Check if the given image pixel is letter or not
	 * 
	 * @param x
	 *            the x coordinate of the pixel
	 * @param line
	 *            the line of the pixel
	 * @return true or false
	 */
	public boolean check(int x, int line);
}
