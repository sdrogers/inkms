package com.constambeys.python;

import com.constambeys.load.MSIImage;

public class IsLetterV1 implements ICheckLetter {

	private int x_start;
	private int x_stop;
	private int y_start;
	private int y_stop;

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
