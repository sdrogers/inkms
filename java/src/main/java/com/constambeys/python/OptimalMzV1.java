package com.constambeys.python;

import java.util.Arrays;
import com.constambeys.load.MSIImage;
import com.constambeys.load.Spectrum;

/**
 * Finds the optimal mass based on intensity differences
 * 
 * @author Constambeys
 */
public class OptimalMzV1 extends OptimalMz {

	/**
	 * Finds the optimal mass based on intensity differences
	 * 
	 * @param loadMZML
	 *            the loaded mass spectrometry image
	 * @param isLetter
	 *            class that identifies letter pixels
	 * @param bins
	 *            class that defines the bins distribution
	 */
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
			if (callback != null)
				callback.update((int) ((float) line / loadMZML.getLines() * 100));

			for (int x = 0; x < loadMZML.getWidth(); x++) {
				Spectrum spectrum = loadMZML.getSpectrum(line, x);
				boolean isLetterCheck = isLetter.check(x, line);

				for (int s = 0; s < spectrum.mzs.length; s++) {
					double mz = spectrum.mzs[s];
					double i = spectrum.ints[s];

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

		if (callback != null)
			callback.update(100);

		long end = System.nanoTime() - startTime;
		System.out.println(String.format("%.2fs", end / 1000000000.0));

		Arrays.sort(stats);
	}

}
