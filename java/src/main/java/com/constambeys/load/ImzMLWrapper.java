package com.constambeys.load;

import java.io.File;
import java.io.IOException;

import imzML.ImzML;

public class ImzMLWrapper implements IReader {
	private ImzML imzML;
	private int nColumns;
	private int nRows;

	public ImzMLWrapper(File mzML) throws IOException {
		long start = System.nanoTime();

		imzML = imzMLConverter.ImzMLHandler.parseimzML(mzML.getAbsolutePath());

		nColumns = imzML.getWidth();
		nRows = imzML.getHeight();

		long end = System.nanoTime() - start;
		System.out.println(String.format("%.2fs", end / 1000000000.0));
	}

	@Override
	public Spectrum getSpectrumByIndex(int index) {
		index--;

		int y = index / nColumns + 1;
		int x = index % nColumns + 1;

		double[] mzs = imzML.getSpectrum(x, y).getmzArray();
		double[] ints = imzML.getSpectrum(x, y).getIntensityArray();

		Spectrum s = new Spectrum(mzs, ints);
		return s;
	}

	@Override
	public int getSpectraCount() {
		return nColumns * nRows;
	}

	public int getLines() {
		return nRows;
	}

}
