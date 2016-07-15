package com.constambeys.load;

import java.util.Map;
import java.util.Map.Entry;

import com.constambeys.python.IProgress;

import uk.ac.ebi.pride.tools.jmzreader.JMzReader;
import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.jmzreader.model.Spectrum;

public class MSIImage {

	private JMzReader run;
	private int lines;
	private int columns;
	private int[][] data;

	int widthInMM;
	int heightInMM;

	public MSIImage(JMzReader run, ILoadPattern pattern) throws Exception {

		this.run = run;
		this.widthInMM = pattern.getWidthMM();
		this.heightInMM = pattern.getHeightMM();
		this.data = pattern.getDataStructure();
		this.lines = data.length;
		this.columns = data[0].length;
	}

	public Spectrum getSpectrum(int line, int column) throws JMzReaderException {
		int index = data[line][column];
		// get a List of all scan numbers in the mzXML file
		// List<String> scanNumbers = inputParser.getSpectraIds();
		Spectrum spectrum = run.getSpectrumByIndex(index);
		return spectrum;
	}

	public double[][] getReduceSpec(double mzRangeLower, double mzRangeHighest, IProgress p) throws JMzReaderException {

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

	public int getLines() {
		return data.length;
	}

	public int getWidth() {
		return data[0].length;
	}

	public int getWidthMM() {
		return widthInMM;
	}

	public int getHeightMM() {
		return heightInMM;
	}
}
