package com.constambeys.load;

import uk.ac.ebi.pride.tools.jmzreader.JMzReader;

public class LoadPattern implements ILoadPattern {

	private Param param;
	private int scansTotal;
	private int startIndex;
	private int step;

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

	public static class Param {
		public int lines;
		public int widthInMM;
		public int heightInMM;
		public float downMotionInMM;
	}

	public LoadPattern(JMzReader run, Param param, Type type) throws Exception {

		this.param = param;
		this.scansTotal = run.getSpectraCount();

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
		// if not scansPerLine.is_integer():
		// raise Exception('Pixels per line not integer value')
		scansPerLineFloat = (scansPerLineFloat * (param.widthInMM - param.downMotionInMM) / param.widthInMM);
		int scansPerLine = (int) scansPerLineFloat;
		int skip = scansTotal - param.lines * scansPerLine;
		int skipPerLine = skip / param.lines;
		int remaining = skip - skipPerLine * param.lines;

		// -----------------------------------------------------------------------

		int[][] data = new int[param.lines][scansPerLine];
		boolean direction = true; // forward / backward
		int index = startIndex;
		for (int line = 0; line < param.lines; line++) {
			if (direction) {
				for (int i = 0; i < scansPerLine; i++) {
					data[line][i] = index;
					index = index + step;
				}
			} else {
				for (int j = 0, i = scansPerLine - 1; i >= 0; i--, j++) {
					data[line][j] = index + (i * step);
				}
				index = index + (scansPerLine * step);
			}
			index = index + (skipPerLine * step);
			direction = !direction;
			if (remaining >= 0) {
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
