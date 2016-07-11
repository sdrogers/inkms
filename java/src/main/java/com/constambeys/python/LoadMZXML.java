package com.constambeys.python;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.ebi.pride.tools.jmzreader.JMzReader;
import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.jmzreader.model.Spectrum;
import uk.ac.ebi.pride.tools.mzml_wrapper.MzMlWrapper;

public class LoadMZXML {

	private int scansTotal;
	private int startIndex;
	private int step;

	private JMzReader run;
	private Param param;
	private int lines;
	private int columns;
	private int[][] data;
	private IProgress p;

	public LoadMZXML(Param param, Type type) throws Exception {

		this.param = param;

		long startTime = System.nanoTime();

		run = new MzMlWrapper(new File(param.filepath));

		long estimatedTime = System.nanoTime() - startTime;
		System.out.println(String.format("%.3fs", estimatedTime / 1000000000.0));

		this.scansTotal = run.getSpectraCount();

		if (type == Type.POSITIVE) {
			this.scansTotal = (int) Math.ceil(scansTotal / 2);
			this.startIndex = 1;
			this.step = 2;
		} else if (type == Type.NEGATIVE) {
			this.scansTotal = (int) Math.floor(scansTotal / 2);
			this.startIndex = 2;
			this.step = 2;
		} else if (type == Type.NORMAL) {
			this.startIndex = 1;
			this.step = 1;
		} else {
			throw new Exception("Invalid Type: normal, positive or negative");
		}

		data = getDataStructure();
		lines = data.length;
		columns = data[0].length;
	}

	public double[][] getReduceSpec(double mzRangeLower, double mzRangeHighest) throws JMzReaderException {

		long start = System.nanoTime();

		double[][] result = new double[lines][columns];
		for (int line = 0; line < lines; line++) {
			if (p != null)
				p.update((int) ((float) line / data.length * 100));

			for (int column = 0; column < columns; column++) {

				double intensity = 0;
				Spectrum spectrum = getSpectrum(line, column);
				Map<Double, Double> map = spectrum.getPeakList();
				for (Entry<Double, Double> entry : map.entrySet()) {
					double mz = entry.getKey();
					double i = entry.getValue();
					if (mzRangeLower <= mz && mz <= mzRangeHighest) {
						intensity = intensity + i;
					}
				}

				result[line][column] = intensity;
			}
		}
		if (p != null)
			p.update(100);
		long end = System.nanoTime() - start;
		System.out.println(String.format("%.2fs", end / 1000000000.0));

		return result;
	}

	public Spectrum getSpectrum(int line, int column) throws JMzReaderException {
		int index = data[line][column];
		// get a List of all scan numbers in the mzXML file
		// List<String> scanNumbers = inputParser.getSpectraIds();
		Spectrum spectrum = run.getSpectrumByIndex(index);
		return spectrum;
	}

	private int[][] getDataStructure() {

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

	public int getLines() {
		return data.length;
	}

	public int getWidth() {
		return data[0].length;
	}

	public int getWidthMM() {
		return param.widthInMM;
	}

	public int getHeightMM() {
		return param.heightInMM;
	}

	public void setProgressListener(IProgress p) {
		this.p = p;
	}

	public enum Type {
		NORMAL, POSITIVE, NEGATIVE
	}

	public static class Param {
		public String filepath;
		public int lines;
		public int widthInMM;
		public int heightInMM;
		public float downMotionInMM;
	}
}
