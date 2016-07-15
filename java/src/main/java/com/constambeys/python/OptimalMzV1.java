package com.constambeys.python;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import com.constambeys.load.MSIImage;

import uk.ac.ebi.pride.tools.jmzreader.model.Spectrum;

public class OptimalMzV1 extends OptimalMz  {


	public OptimalMzV1(MSIImage loadMZML, ICheckLetter isLetter, IBinResolution bins) {
		super(loadMZML, isLetter, bins);
	}

	public void run() throws Exception {

		long startTime = System.nanoTime();

		int resolution = bins.getBinsCount();
		stats = new Stats[resolution];
		for (int i = 0; i < resolution; i++) {
			stats[i] = new Stats();
			stats[i].index = i;
		}

		double mzRangeLower = bins.getLowerBound();
		double mzRangeHighest = bins.getHigherBound();

		for (int line = 0; line < loadMZML.getLines(); line++) {
			if (p != null)
				p.update((int) ((float) line / loadMZML.getLines() * 100));

			for (int x = 0; x < loadMZML.getWidth(); x++) {
				Spectrum spectrum = loadMZML.getSpectrum(line, x);
				boolean isLetterCheck = isLetter.check(x, line);

				Map<Double, Double> map = spectrum.getPeakList();
				for (Entry<Double, Double> entry : map.entrySet()) {
					double mz = entry.getKey();
					double i = entry.getValue();

					if (mzRangeLower <= mz && mz <= mzRangeHighest) {
						if (isLetterCheck) {
							int indx = bins.getMassIndex(mz);
							stats[indx].c += 1;
							stats[indx].i += i;
						} else {
							int indx = bins.getMassIndex(mz);
							stats[indx].c1 += 1;
							stats[indx].i1 += i;
						}
					}
				}
			}
		}
		for (int i = 0; i < resolution; i++) {
			if (stats[i].c != 0)
				stats[i].i = stats[i].i / stats[i].c;
			if (stats[i].c1 != 0)
				stats[i].i1 = stats[i].i1 / stats[i].c1;
			stats[i].diff = stats[i].i1 - stats[i].i;
		}

		if (p != null)
			p.update(100);

		long end = System.nanoTime() - startTime;
		System.out.println(String.format("%.2fs", end / 1000000000.0));

		Arrays.sort(stats);
	}

}
