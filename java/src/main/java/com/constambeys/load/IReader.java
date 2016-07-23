package com.constambeys.load;

public interface IReader {

	/**
	 * @param index
	 *            1 based index
	 * @return
	 */
	Spectrum getSpectrumByIndex(int index);

	/**
	 * @return the number of spectra in the given file 
	 */
	int getSpectraCount();

}
