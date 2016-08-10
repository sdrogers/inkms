package com.constambeys.readers;

import java.io.File;
import imzML.ImzML;

/**
 * The {@code ImzMLProxy} class reads imzML data
 * 
 * @author Constambeys
 */
public class ImzMLProxy2D implements IReader {
	private ImzML imzML;
	private int nColumns;
	private int nRows;

	public ImzMLProxy2D(File mzML) throws Exception {

		imzML = imzMLConverter.ImzMLHandler.parseimzML(mzML.getAbsolutePath());
		if (!imzML.isProcessed())
			throw new Exception("imzml file cannot be processed");

		nColumns = imzML.getWidth();
		nRows = imzML.getHeight();
	}

	@Override
	public Spectrum getSpectrumByIndex(int index) {
		index--;

		// Converts index to 1 based x and y based on known file structure
		int y = index / nColumns + 1;
		int x = index % nColumns + 1;

		mzML.Spectrum orgS = imzML.getSpectrum(x, y);
		Spectrum s;
		if (orgS != null) {
			double[] mzs = orgS.getmzArray();
			double[] ints = orgS.getIntensityArray();
			s = new Spectrum(mzs, ints);
		} else {
			s = new Spectrum(0);
		}

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
