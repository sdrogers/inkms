package com.constambeys.load;

import java.util.Map;
import java.util.Map.Entry;

import com.constambeys.python.IProgress;

import uk.ac.ebi.pride.tools.jmzreader.JMzReader;
import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.jmzreader.model.Spectrum;

/**
 * The {@code MSIImage} class represents mass spectrometry image
 * 
 * @author Constambeys
 */
public class MSIImage {

	private JMzReader run;
	private int lines;
	private int columns;
	private int[][] data;

	int widthInMM;
	int heightInMM;

	/**
	 * Constructs a new {@code MSIImage} using a file reader and a pattern
	 * 
	 * @param reader
	 *            a file reader that implements the {@code JMzReader}
	 * @param pattern
	 *            a pattern that implements {@code ILoadPattern} interface
	 * @throws Exception
	 */
	public MSIImage(JMzReader reader, ILoadPattern pattern) throws Exception {

		this.run = reader;
		this.widthInMM = pattern.getWidthMM();
		this.heightInMM = pattern.getHeightMM();
		this.data = pattern.getDataStructure();
		this.lines = data.length;
		this.columns = data[0].length;
	}

	/**
	 * Returns the spectrum at the specified position
	 * 
	 * @param line
	 *            the pixel's line index
	 * @param column
	 *            the pixel's column index
	 * @return a Spectrum class
	 * @throws JMzReaderException
	 */
	public Spectrum getSpectrum(int line, int column) throws JMzReaderException {
		int index = data[line][column];
		// get a List of all scan numbers in the mzXML file
		// List<String> scanNumbers = inputParser.getSpectraIds();
		Spectrum spectrum = run.getSpectrumByIndex(index);
		return spectrum;
	}

	/**
	 * Sums all intensities between lower and upper mass range
	 * 
	 * @param callback
	 *            returns progress status
	 * @param mzrange
	 *            the lower and upper mass per charge value. Arguments must be multiple of 2
	 * @return Returns a 2d array
	 * @throws JMzReaderException
	 */
	public double[][] getReduceSpec(IProgress callback, double... mzrange) throws JMzReaderException {

		long start = System.nanoTime();

		double[][] result = new double[lines][columns];
		for (int line = 0; line < lines; line++) {
			if (callback != null)
				callback.update((int) ((float) line / data.length * 100));

			for (int column = 0; column < columns; column++) {

				double intensity = 0;
				Spectrum spectrum = getSpectrum(line, column);
				Map<Double, Double> map = spectrum.getPeakList();
				for (Entry<Double, Double> entry : map.entrySet()) {
					double mz = entry.getKey();
					double i = entry.getValue();
					for (int m = 0; m < mzrange.length; m = m + 2) {
						if (mzrange[m] <= mz && mz <= mzrange[m + 1]) {
							intensity = intensity + i;
						}
					}
				}

				result[line][column] = intensity;
			}
		}
		if (callback != null)
			callback.update(100);
		long end = System.nanoTime() - start;
		System.out.println(String.format("%.2fs", end / 1000000000.0));

		return result;
	}

	/**
	 * @return the number of lines of the image
	 */
	public int getLines() {
		return data.length;
	}

	/**
	 * @return the width of the image
	 */
	public int getWidth() {
		return data[0].length;
	}

	/**
	 * @return the width in millimetres of the image
	 */
	public int getWidthMM() {
		return widthInMM;
	}

	/**
	 * @return the height in millimetres of the image
	 */
	public int getHeightMM() {
		return heightInMM;
	}
}