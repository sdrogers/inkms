package com.constambeys.python;

import java.util.Arrays;

import com.constambeys.readers.MSIImage;
import com.constambeys.readers.Spectrum;

/**
 * Finds the optimal mass based on intensity differences
 * 
 * @author Constambeys
 */
public class OptimalMzV1 extends OptimalMz {

	/**
	 * Finds the optimal mass based on intensity differences
	 * 
	 * @param msiimage
	 *            the loaded mass spectrometry image
	 * @param isLetter
	 *            class that identifies letter pixels
	 * @param bins
	 *            class that defines the bins distribution
	 */
	public OptimalMzV1(MSIImage msiimage, ICheckLetter isLetter, IBinResolution bins) {
		super(msiimage, isLetter, bins);
	}

	public void run() throws Exception {

		long startTime = System.nanoTime();

		int resolution = bins.getBinsCount();
		binStatistics = new BinStatistics[resolution];
		for (int i = 0; i < resolution; i++) {
			binStatistics[i] = new BinStatistics();
			binStatistics[i].index = i;
		}

		double mzRangeLower = bins.getLowerBound();
		double mzRangeHighest = bins.getHigherBound();

		for (int line = 0; line < msiimage.getLines(); line++) {
			if (callback != null)
				callback.update((int) ((float) line / msiimage.getLines() * 100));

			for (int x = 0; x < msiimage.getWidthPixels(); x++) {
				Spectrum spectrum = msiimage.getSpectrum(line, x);
				boolean isLetterCheck = isLetter.check(x, line);

				for (int s = 0; s < spectrum.mzs.length; s++) {
					double mz = spectrum.mzs[s];
					double i = spectrum.ints[s];

					if (mzRangeLower <= mz && mz <= mzRangeHighest) {
						if (isLetterCheck) {
							int indx = bins.getMassIndex(mz);
							binStatistics[indx].c += 1;
							binStatistics[indx].i += i;
						} else {
							int indx = bins.getMassIndex(mz);
							binStatistics[indx].c1 += 1;
							binStatistics[indx].i1 += i;
						}
					}
				}
			}
		}
		for (int i = 0; i < resolution; i++) {
			if (binStatistics[i].c != 0)
				binStatistics[i].i = binStatistics[i].i / binStatistics[i].c;
			if (binStatistics[i].c1 != 0)
				binStatistics[i].i1 = binStatistics[i].i1 / binStatistics[i].c1;
			binStatistics[i].diff = binStatistics[i].i - binStatistics[i].i1;
		}

		if (callback != null)
			callback.update(100);

		long end = System.nanoTime() - startTime;
		System.out.println(String.format("%.2fs", end / 1000000000.0));

		Arrays.sort(binStatistics);
	}

}
