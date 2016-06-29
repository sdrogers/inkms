package pyhton;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.jmzreader.model.Spectrum;

public abstract class OptimalMz {

	class Stats implements Comparable<Stats> {
		double mz;
		double diff;
		int c;
		double i;
		int c1;
		double i1;

		@Override
		public int compareTo(Stats o) {
			return Double.compare(diff, o.diff);
		}
	}

	private Stats[] stats;

	private double mzRangeLower;
	private double mzRangeHighest;
	private int resolution;
	private double range;

	public OptimalMz() {
	}

	public void init(LoadMZXML loadMZML, double mzRangeLower, double mzRangeHighest, int resolution)
			throws JMzReaderException {

		long startTime = System.nanoTime();

		double resolutionMZ = mzRangeHighest - mzRangeLower;
		stats = new Stats[resolution];
		for (int i = 0; i < resolution; i++) {
			stats[i] = new Stats();
		}

		for (int line = 0; line < loadMZML.getLines(); line++) {
			System.out.println(String.format("\r%.2f", (float) line / loadMZML.getLines() * 100));
			System.out.flush();
			for (int x = 0; x < loadMZML.getWidth(); x++) {
				Spectrum spectrum = loadMZML.getSpectrum(line, x);
				boolean isLetter = isLetter(x, line);

				Map<Double, Double> map = spectrum.getPeakList();
				for (Entry<Double, Double> entry : map.entrySet()) {
					double mz = entry.getKey();
					double i = entry.getValue();
					if (mzRangeLower <= mz && mz <= mzRangeHighest) {
						if (isLetter) {
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
			stats[i].mz = i * resolutionMZ / resolution + mzRangeLower;
			stats[i].diff = stats[i].i1 - stats[i].i;
		}
		System.out.println("\r100%\n");
		long end = System.nanoTime() - startTime;
		System.out.println(String.format("%.2fs", end / 1000000000.0));

		Arrays.sort(stats);
		this.mzRangeLower = mzRangeLower;
		this.mzRangeHighest = mzRangeHighest;
		this.resolution = resolution;
		this.range = resolutionMZ / resolution;
	}

	public abstract boolean isLetter(int x, int line);

	public int[] getN(int n, int threshold_i) {

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

	public int[] getN(int n) {
		return getN(n, 0);
	}

	public void printN(int n) {
		int[] result = getN(n);
		for (int i = 0; i < result.length; i++) {
			int indx = result[i];
			System.out.println(String.format("mz: %f - %f , i: %f - %f, c: %d - %d", stats[indx].mz,
					stats[i].mz + range, stats[indx].i, stats[indx].i1, stats[indx].c, stats[indx].c1));
			System.out.print("i1 - i: ");
			System.out.println(stats[indx].diff);

		}
		System.out.println("range:");
		System.out.println(range);
	}
}
