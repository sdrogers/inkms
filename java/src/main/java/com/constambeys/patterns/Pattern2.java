package com.constambeys.patterns;

import com.constambeys.filtering.IFiltering;

/**
 * Implements normal pattern left-> right
 * 
 * @author Constambeys
 *
 */
public class Pattern2 implements ILoadPattern {

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
	}

	/**
	 * Constructs a normal pattern class which implements{@code ILoadPattern}
	 * 
	 * @param reader
	 *            a file reader that implements the {@code JMzReader}
	 * @param param
	 *            the file parameters
	 * @param type
	 *            the selection algorithm
	 * @throws Exception
	 */

	public Pattern2(IFiltering reader, Param param) throws Exception {

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
		int scansPerLine = (int) scansPerLineFloat;
		int skip = scansTotal - param.lines * scansPerLine;
		int skipPerLine = skip / param.lines;
		int remaining = skip - skipPerLine * param.lines;

		if (scansPerLine <= 0) {
			throw new Exception("Invalid input data. Calculated scans per line cannot be 0");
		}

		int[][] data = new int[param.lines][scansPerLine];
		int index = startIndex;
		for (int line = 0; line < param.lines; line++) {
			for (int i = 0; i < scansPerLine; i++) {
				data[line][i] = reader.getRealIndex(index);
				index = index + step;
			}

			index = index + (skipPerLine * step);
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
