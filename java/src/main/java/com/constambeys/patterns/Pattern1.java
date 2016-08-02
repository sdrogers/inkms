package com.constambeys.patterns;

import com.constambeys.filtering.IFiltering;

/**
 * Implements meandering pattern left-> right -> down -> left
 * 
 * @author Constambeys
 *
 */
public class Pattern1 implements ILoadPattern {

	private Param param;
	/**
	 * the filtered number of spectrums
	 */
	private int scansTotal;
	/**
	 * the starting spectrum index
	 */
	private int startIndex;
	/**
	 * the increment in each step
	 */
	private int step;

	private IFiltering reader;

	/**
	 * Specifies the file parameters
	 */
	public static class Param {
		public int lines;
		public int widthInMM;
		public int heightInMM;
		public float downMotionInMM;
	}

	/**
	 * Constructs a meandering pattern class which implements{@code ILoadPattern}
	 * 
	 * @param reader
	 *            a file reader that implements the {@code JMzReader}
	 * @param param
	 *            the file parameters
	 * @param type
	 *            the selection algorithm
	 * @throws Exception
	 */
	public Pattern1(IFiltering reader, Param param) throws Exception {

		this.param = param;
		this.reader = reader;
		this.scansTotal = reader.getSpectraCount();

		this.startIndex = 1;
		this.step = 1;
	}

	@Override
	public int[][] getDataStructure() throws Exception {

		if (param.lines == 0) {
			throw new Exception("Invalid input data. Line parameter cannot be 0");
		}

		float scansPerLineFloat = (float) scansTotal / param.lines;
		// if not scansPerLine.is_integer():
		// raise Exception('Pixels per line not integer value')
		scansPerLineFloat = (scansPerLineFloat * (param.widthInMM - param.downMotionInMM) / param.widthInMM);
		int scansPerLine = (int) scansPerLineFloat;
		int skip = scansTotal - param.lines * scansPerLine;
		int skipPerLine = skip / param.lines;
		int remaining = skip - skipPerLine * param.lines;

		if (scansPerLine <= 0) {
			throw new Exception("Invalid input data. Calculated scans per line cannot be 0");
		}
		// -----------------------------------------------------------------------

		int[][] data = new int[param.lines][scansPerLine];
		boolean direction = true; // forward / backward
		int index = startIndex;
		for (int line = 0; line < param.lines; line++) {
			if (direction) {
				for (int i = 0; i < scansPerLine; i++) {
					data[line][i] = reader.getRealIndex(index);
					index = index + step;
				}
			} else {
				for (int j = 0, i = scansPerLine - 1; i >= 0; i--, j++) {
					data[line][j] = reader.getRealIndex(index + (i * step));
				}
				index = index + (scansPerLine * step);
			}
			index = index + (skipPerLine * step);
			direction = !direction;
			if (remaining > 0) {
				remaining = remaining - 1;
				index = index + step;
			}
		}
		return data;
	}

	@Override
	public int getWidthMM() {
		return param.widthInMM;
	}

	@Override
	public int getHeightMM() {
		return param.heightInMM;
	}
}
