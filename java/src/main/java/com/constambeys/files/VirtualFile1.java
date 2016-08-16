package com.constambeys.files;

import com.constambeys.readers.IReader;
import com.constambeys.readers.Spectrum;

/**
 * Square image file
 * 
 * @author Constambeys
 *
 */
public class VirtualFile1 implements IReader {

	private static int ROWS = 100;
	private static int COLUMNS = 100;

	// Normal Pattern
	public boolean patternNormal[][] = new boolean[ROWS][COLUMNS];

	public VirtualFile1() {

		for (int row = 0; row < ROWS; row++) {
			for (int column = 0; column < COLUMNS; column++) {
				if (row == column) {
					patternNormal[row][column] = true;
				} else {
					patternNormal[row][column] = false;
				}
			}
		}
	}

	@Override
	public Spectrum getSpectrumByIndex(int index) {
		index--;

		int row = index / COLUMNS;
		int column = index % COLUMNS;

		double mzs[];
		double ints[];

		if (patternNormal[row][column]) {
			mzs = new double[] { 260.2, 260.8 };
			ints = new double[] { 100, 150 };
		} else {
			mzs = new double[] { 260.1 };
			ints = new double[] { 10 };
		}
		return new Spectrum(mzs, ints);
	}

	@Override
	public ScanType getSpectraPolarity(int index) throws Exception {
		return ScanType.POSITIVE;
	}

	@Override
	public int getSpectraCount() {
		return ROWS * COLUMNS;
	}

	public int getLines() {
		return ROWS;
	}

	public int getColumns() {
		return COLUMNS;
	}

	public int getWidth() {
		return 2 * COLUMNS;
	}

	public int getHeight() {
		return 2 * ROWS;
	}
}
