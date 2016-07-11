package com.constambeys.python;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.jmzreader.model.Spectrum;

public class OptimalMz implements IOptimalMz {

	public class Stats implements Comparable<Stats> {
		public double mz;
		public double diff;
		public int c;
		public double i;
		public int c1;
		public double i1;

		@Override
		public int compareTo(Stats o) {
			return Double.compare(diff, o.diff);
		}
	}

	private Stats[] stats;

	private ICheckLetter isLetter;
	private double mzRangeLower;
	private double mzRangeHighest;
	private int resolution;
	private double range;
	private IProgress p;

	public OptimalMz() {

	}

	public void run(ICheckLetter isLetter, LoadMZXML loadMZML, double mzRangeLower, double mzRangeHighest,
			int resolution) throws JMzReaderException {

		this.isLetter = isLetter;
		long startTime = System.nanoTime();

		double resolutionMZ = mzRangeHighest - mzRangeLower;
		stats = new Stats[resolution];
		for (int i = 0; i < resolution; i++) {
			stats[i] = new Stats();
			stats[i].mz = i * resolutionMZ / resolution + mzRangeLower;
		}

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
							int indx = (int) ((mz - mzRangeLower) / resolutionMZ * resolution);
							stats[indx].c += 1;
							stats[indx].i += i;
						} else {
							int indx = (int) ((mz - mzRangeLower) / resolutionMZ * resolution);
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
		this.mzRangeLower = mzRangeLower;
		this.mzRangeHighest = mzRangeHighest;
		this.resolution = resolution;
		this.range = resolutionMZ / resolution;
	}

	public double getRange() {
		return range;
	}

	public Stats get(int i) {
		return stats[i];
	}

	@Override
	public double getMz(int i) {
		return stats[i].mz;
	}

	public int[] getIndexesN(int n, int threshold_i) {

		int j = 0;
		int[] result = new int[n];

		for (int i = 0; i < resolution; i++) {
			if (stats[i].i >= threshold_i && stats[i].i1 >= threshold_i) {
				result[j] = i;
				j++;
			}
			if (j == n)
				break;
		}

		return result;
	}

	public int[] getIndexesN(int n) {
		return getIndexesN(n, 0);
	}

	public String printN(int n) {
		StringBuffer sb = new StringBuffer();
		int[] result = getIndexesN(n);
		for (int i = 0; i < result.length; i++) {
			int indx = result[i];
			sb.append(String.format("mz: %f - %f , i: %f - %f, c: %d - %d \n", stats[indx].mz, stats[i].mz + range,
					stats[indx].i, stats[indx].i1, stats[indx].c, stats[indx].c1));
			sb.append(String.format("i1 - i: "));
			sb.append(String.format("%f\n", stats[indx].diff));

		}
		sb.append(String.format("range: "));
		sb.append(String.format("%f\n", range));
		return sb.toString();
	}

	public void setProgressListener(IProgress p) {
		this.p = p;
	}

}
