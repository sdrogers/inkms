package com.constambeys.python;

import java.util.Arrays;
import com.constambeys.load.MSIImage;
import com.constambeys.load.Spectrum;

/**
 * Find the optimal mass based on intensity differences and the number of pixels above average intensity
 * 
 * @author Constambeys
 * 
 */
public class OptimalMzV2 extends OptimalMz {

	public class Pixel {
		public double i;
		public int c;
	}

	int pixelWeight = 2;

	/**
	 * @param msiimage
	 *            the loaded mass spectrometry image
	 * @param isLetter
	 *            class that identifies letter pixels
	 * @param bins
	 *            class that defines the bins distribution
	 * @param pixelWeight
	 *            the number of pixels raised to power pixelWeight
	 * 
	 */
	public OptimalMzV2(MSIImage msiimage, ICheckLetter isLetter, IBinResolution bins, int pixelWeight) {
		super(msiimage, isLetter, bins);
		this.pixelWeight = pixelWeight;
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

		Pixel[][][] pixels = new Pixel[msiimage.getLines()][msiimage.getWidth()][resolution];

		for (int line = 0; line < msiimage.getLines(); line++) {
			if (callback != null)
				callback.update((int) ((float) line / msiimage.getLines() * 100));

			for (int x = 0; x < msiimage.getWidth(); x++) {
				for (int i = 0; i < resolution; i++) {
					pixels[line][x][i] = new Pixel();
				}
				Spectrum spectrum = msiimage.getSpectrum(line, x);
				boolean isLetterCheck = isLetter.check(x, line);

				for (int s = 0; s < spectrum.mzs.length; s++) {
					double mz = spectrum.mzs[s];
					double i = spectrum.ints[s];
					
					if (mzRangeLower <= mz && mz <= mzRangeHighest) {
						int indx = bins.getMassIndex(mz);

						if (isLetterCheck) {
							stats[indx].c += 1;
							stats[indx].i += i;
						} else {
							stats[indx].c1 += 1;
							stats[indx].i1 += i;
						}
						pixels[line][x][indx].i += i;
						pixels[line][x][indx].c += 1;
					}
				}
			}
		}

		for (int i = 0; i < resolution; i++) {
			if (stats[i].c != 0)
				stats[i].i = stats[i].i / stats[i].c;
			if (stats[i].c1 != 0)
				stats[i].i1 = stats[i].i1 / stats[i].c1;
		}

		for (int line = 0; line < msiimage.getLines(); line++) {
			if (callback != null)
				callback.update((int) ((float) line / msiimage.getLines() * 100));

			for (int x = 0; x < msiimage.getWidth(); x++) {

				boolean isLetterCheck = isLetter.check(x, line);

				if (!isLetterCheck)
					continue;

				for (int i = 0; i < resolution; i++) {
					if (pixels[line][x][i].c > 0 && pixels[line][x][i].i / pixels[line][x][i].c > stats[i].i1) {
						stats[i].diff = stats[i].diff + 1;
					}
				}
			}
		}

		for (int i = 0; i < resolution; i++) {
			stats[i].diff = -(stats[i].i - stats[i].i1) * Math.pow(stats[i].diff, pixelWeight);
		}

		if (callback != null)
			callback.update(100);
		long end = System.nanoTime() - startTime;
		System.out.println(String.format("%.2fs", end / 1000000000.0));

		Arrays.sort(stats);
	}
}
