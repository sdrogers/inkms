package com.constambeys.readers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;

import org.expasy.mzjava.core.io.ms.spectrum.MzxmlReader;
import org.expasy.mzjava.core.ms.peaklist.PeakList.Precision;
import org.expasy.mzjava.core.ms.peaklist.Polarity;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;

/**
 * The {@code MzJavaProxy} class reads mzXML data
 * 
 * @author Constambeys
 */
public class MzJavaProxy implements IReader {
	private ArrayList<MsnSpectrum> list;
	private int count;

	public MzJavaProxy(File mzXML) throws IOException {

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
	}

	@Override
	public ScanType getSpectraPolarity(int index) throws Exception {
		MsnSpectrum spectrum = list.get(index - 1);

		Polarity p = spectrum.getPrecursor().getPolarity();

		if (p.equals(Polarity.POSITIVE)) {
			return ScanType.POSITIVE;
		} else if (p.equals(Polarity.NEGATIVE)) {
			return ScanType.NEGATIVE;
		} else {
			throw new Exception("Scan polarity is unknown");
		}
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
