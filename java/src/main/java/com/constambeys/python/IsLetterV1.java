package com.constambeys.python;

import com.constambeys.load.MSIImage;

/**
 * Identifies letters at a specified rectangle area
 * 
 * @author Constambeys
 *
 */
public class IsLetterV1 implements ICheckLetter {

	private int x_start;
	private int x_stop;
	private int y_start;
	private int y_stop;

	/**
	 * Constructs a new {@code IsLetterV1} class which implements {@code ICheckLetter} interface using a rectangle
	 * 
	 * @param msiimage
	 *            the loaded mass spectrometry image
	 * @param x_start_mm
	 *            the starting x coordinate in millimetres of the interested area
	 * @param x_stop_mm
	 *            the ending x coordinate in millimetres of the interested area
	 * @param y_start_mm
	 *            the starting y coordinate in millimetres of the interested area
	 * @param y_stop_mm
	 *            the ending y coordinate in millimetres of the interested area
	 */
	public IsLetterV1(MSIImage msiimage, double x_start_mm, double x_stop_mm, double y_start_mm, double y_stop_mm) {

		this.x_start = (int) (x_start_mm / msiimage.getWidthMM() * msiimage.getWidth());
		this.x_stop = (int) (x_stop_mm / msiimage.getWidthMM() * msiimage.getWidth());
		this.y_start = (int) (y_start_mm / msiimage.getHeightMM() * msiimage.getLines());
		this.y_stop = (int) (y_stop_mm / msiimage.getHeightMM() * msiimage.getLines());
	}

	@Override
	public boolean check(int x, int line) {
		return x_start <= x && x <= x_stop && y_start <= line && line <= y_stop;
	}
}
