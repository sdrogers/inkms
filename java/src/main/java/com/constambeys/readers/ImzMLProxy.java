package com.constambeys.readers;

import java.io.File;
import imzML.ImzML;

/**
 * The {@code ImzMLProxy} class reads imzML data
 * 
 * @author Constambeys
 */
public class ImzMLProxy implements IReader {
	private ImzML imzML;
	private int nColumns;
	private int nRows;

	public ImzMLProxy(File mzML) throws Exception {
		long start = System.nanoTime();

		imzML = imzMLConverter.ImzMLHandler.parseimzML(mzML.getAbsolutePath());
		if (!imzML.isProcessed())
			throw new Exception("imzml file cannot be processed");

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
	public ScanType getSpectraPolarity(int index) throws Exception {
		index--;

		int y = index / nColumns + 1;
		int x = index % nColumns + 1;

		mzML.Spectrum spectrum = imzML.getSpectrum(x, y);

		if (spectrum.getCVParam("MS:1000130") != null) {
			return IReader.ScanType.POSITIVE;
		} else if (spectrum.getCVParam("MS:1000129") != null) {
			return IReader.ScanType.NEGATIVE;
		} else {
			throw new Exception("Error while retrieving scan polarity");
		}

	}

	@Override
	public int getSpectraCount() {
		return nColumns * nRows;
	}

	public int getLines() {
		return nRows;
	}

}
