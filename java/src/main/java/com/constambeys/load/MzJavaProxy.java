package com.constambeys.load;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;

import org.expasy.mzjava.core.io.ms.spectrum.MzxmlReader;
import org.expasy.mzjava.core.ms.peaklist.PeakList.Precision;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;

public class MzJavaProxy implements IReader {
	private ArrayList<MsnSpectrum> list;
	private int count;

	public MzJavaProxy(File mzXML) throws IOException {
		long start = System.nanoTime();

		MzxmlReader reader = new MzxmlReader(mzXML, Precision.DOUBLE);

		// we can then add/remove ConsistencyCheck
		reader.removeConsistencyChecks(EnumSet.of(MzxmlReader.ConsistencyCheck.TOTAL_ION_CURRENT, MzxmlReader.ConsistencyCheck.MOST_INTENSE_PEAK));

		count = reader.getExpectedScanCount();

		list = new ArrayList<MsnSpectrum>(count);

		// hasNext() returns true if there is more spectrum to read
		while (reader.hasNext()) {
			list.add(reader.next());
		}

		reader.close();

		long end = System.nanoTime() - start;
		System.out.println(String.format("%.2fs", end / 1000000000.0));
	}

	@Override
	public Spectrum getSpectrumByIndex(int index) {

		MsnSpectrum spectrum = list.get(index - 1);
		Spectrum s = new Spectrum(spectrum.size());

		for (int i = 0; i < spectrum.size(); i++) {
			s.mzs[i] = spectrum.getMz(i);
			s.ints[i] = spectrum.getIntensity(i);
		}

		return s;
	}

	@Override
	public int getSpectraCount() {
		return count;
	}
}
