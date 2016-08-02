package com.constambeys.filtering;

import com.constambeys.readers.IReader;
import com.constambeys.readers.Spectrum;

/**
 * No filtering of mass spectrometry data
 * 
 * @author Constambeys
 *
 */
public class NoFiltering implements IFiltering {

	IReader reader;

	public NoFiltering(IReader reader) {
		this.reader = reader;
	}

	@Override
	public Spectrum getSpectrumByIndex(int index) {
		return reader.getSpectrumByIndex(index);
	}

	@Override
	public ScanType getSpectraPolarity(int index) throws Exception {
		return reader.getSpectraPolarity(index);
	}

	@Override
	public int getSpectraCount() {
		return reader.getSpectraCount();
	}

	@Override
	public int getRealIndex(int index) {
		return index;
	}

}
