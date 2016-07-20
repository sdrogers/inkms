package com.constambeys.readers;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a general file reader
 * 
 * @author Constambeys
 *
 */
public interface IReader {

	class Spectrum {
		List<Double> mz = new ArrayList<Double>();
		List<Double> i = new ArrayList<Double>();;
	}

	/**
	 * 
	 * @return the number of spectra in the file
	 */
	public int getSpectraCount();

	/**
	 * Returns the spectrum based on its 1-based index in the file.
	 * 
	 * @param index
	 *            The 1-based index of the spectrum in the file.
	 * @return A Spectrum or null in case a spectrum with the given index doesn't exist.
	 * @throws Exception
	 */
	public Spectrum getSpectrumByIndex(int index) throws Exception;
}
