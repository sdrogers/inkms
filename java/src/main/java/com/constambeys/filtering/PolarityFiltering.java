package com.constambeys.filtering;

import java.util.ArrayList;

import com.constambeys.readers.IReader;
import com.constambeys.readers.Spectrum;

/**
 * Hides mass spectrometry data that are not needed based on charge current POSITIVE or NEGATIVE
 * 
 * @author Constambeys
 *
 */
public class PolarityFiltering implements IFiltering {

	IReader reader;
	Integer indexes[];

	public PolarityFiltering(IReader reader, ScanType p) throws Exception {
		this.reader = reader;

		ArrayList<Integer> filtered = new ArrayList<Integer>();
		for (int i = 1; i <= reader.getSpectraCount(); i++) {
			if (reader.getSpectraPolarity(i) == p) {
				filtered.add(i);
			}
		}

		indexes = filtered.toArray(new Integer[filtered.size()]);
	}

	@Override
	public Spectrum getSpectrumByIndex(int index) {
		return reader.getSpectrumByIndex(indexes[index]);
	}

	@Override
	public ScanType getSpectraPolarity(int index) throws Exception {
		return reader.getSpectraPolarity(indexes[index]);
	}

	@Override
	public int getSpectraCount() {
		return indexes.length;
	}

	@Override
	public int getRealIndex(int index) {
		return indexes[index - 1];
	}
}
