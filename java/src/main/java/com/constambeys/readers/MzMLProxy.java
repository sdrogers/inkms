package com.constambeys.readers;

import java.io.File;
import java.io.IOException;

import mzML.MzML;
import mzML.SpectrumList;

/**
 * The {@code MzMLProxy} class reads mzML data
 * 
 * @author Constambeys
 */
public class MzMLProxy implements IReader {
	private SpectrumList list;
	private int count;

	public MzMLProxy(File mzML) throws IOException {
		
		MzML imzML = imzMLConverter.MzMLHandler.parsemzML(mzML.getAbsolutePath());

		list = imzML.getRun().getSpectrumList();

		count = list.size();
	}

	@Override
	public Spectrum getSpectrumByIndex(int index) {
		mzML.Spectrum spectrum = list.getSpectrum(index - 1);

		double[] mzs = spectrum.getmzArray();
		double[] ints = spectrum.getIntensityArray();

		Spectrum s = new Spectrum(mzs, ints);
		return s;
	}

	@Override
	public ScanType getSpectraPolarity(int index) throws Exception {
		mzML.Spectrum spectrum = list.getSpectrum(index - 1);

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
		return count;
	}
}
