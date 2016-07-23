package com.constambeys.load;

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
		long start = System.nanoTime();

		MzML imzML = imzMLConverter.MzMLHandler.parsemzML(mzML.getAbsolutePath());

		list = imzML.getRun().getSpectrumList();

		count = list.size();

		long end = System.nanoTime() - start;
		System.out.println(String.format("%.2fs", end / 1000000000.0));
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
	public int getSpectraCount() {
		return count;
	}
}
