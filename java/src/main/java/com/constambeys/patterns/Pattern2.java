package com.constambeys.patterns;

import com.constambeys.load.IReader;

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

	/**
	 * Specifies the spectrum selection algorithm
	 */
	public static enum Type {
		ALL("ALL"), ODD("ODD 1,3,..."), EVEN("EVEN 2,4,...");

		String name;

		private Type(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

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

	public Pattern2(IReader reader, Param param, Type type) throws Exception {

		this.param = param;
		this.scansTotal = reader.getSpectraCount();

		if (type == Type.ODD) { // Positive
			this.scansTotal = (int) Math.ceil(scansTotal / 2);
			this.startIndex = 1;
			this.step = 2;
		} else if (type == Type.EVEN) { // Negative
			this.scansTotal = (int) Math.floor(scansTotal / 2);
			this.startIndex = 2;
			this.step = 2;
		} else if (type == Type.ALL) {
			this.startIndex = 1;
			this.step = 1;
		} else {
			throw new Exception("Invalid Type: normal, positive or negative");
		}
	}

	@Override
	public int[][] getDataStructure() {
		float scansPerLineFloat = (float) scansTotal / param.lines;
		int scansPerLine = (int) scansPerLineFloat;
		int skip = scansTotal - param.lines * scansPerLine;
		int skipPerLine = skip / param.lines;
		int remaining = skip - skipPerLine * param.lines;

		int[][] data = new int[param.lines][scansPerLine];
		int index = startIndex;
		for (int line = 0; line < param.lines; line++) {
			for (int i = 0; i < scansPerLine; i++) {
				data[line][i] = index;
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
