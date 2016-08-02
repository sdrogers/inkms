package com.constambeys.readers;

public interface IReader {

	/**
	 * Used to filter spectrums based on charge current
	 *
	 */
	enum ScanType {
		POSITIVE, NEGATIVE;
	}

	/**
	 * @param index
	 *            1 based index
	 * @return
	 */
	Spectrum getSpectrumByIndex(int index);

	/**
	 * @param index
	 *            1 based index
	 * @return
	 */
	ScanType getSpectraPolarity(int index) throws Exception;

	/**
	 * @return the number of spectra in the given file
	 */
	int getSpectraCount();

}
