package pyhton;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.jmzreader.model.Spectrum;

public class OptimalMzV2 implements IOptimalMz {
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

	public class Pixel {
		public double i;
		public int c;
	}

	private Stats[] stats;

	private IsLetter isLetter;
	private double mzRangeLower;
	private double mzRangeHighest;
	private int resolution;
	private double range;

	public OptimalMzV2(IsLetter isLetter, LoadMZXML loadMZML, double mzRangeLower, double mzRangeHighest,
			int resolution) throws JMzReaderException {

		this.isLetter = isLetter;
		long startTime = System.nanoTime();

		double resolutionMZ = mzRangeHighest - mzRangeLower;
		stats = new Stats[resolution];
		for (int i = 0; i < resolution; i++) {
			stats[i] = new Stats();
			stats[i].mz = i * resolutionMZ / resolution + mzRangeLower;
		}

		Pixel[][][] pixels = new Pixel[loadMZML.getLines()][loadMZML.getWidth()][resolution];

		for (int line = 0; line < loadMZML.getLines(); line++) {
			System.out.println(String.format("\r%.2f", (float) line / loadMZML.getLines() * 100));
			System.out.flush();
			for (int x = 0; x < loadMZML.getWidth(); x++) {
				for (int i = 0; i < resolution; i++) {
					pixels[line][x][i] = new Pixel();
				}
				Spectrum spectrum = loadMZML.getSpectrum(line, x);
				boolean isLetterCheck = isLetter.check(x, line);

				Map<Double, Double> map = spectrum.getPeakList();
				for (Entry<Double, Double> entry : map.entrySet()) {
					double mz = entry.getKey();
					double i = entry.getValue();
					if (mzRangeLower <= mz && mz <= mzRangeHighest) {
						int indx = (int) ((mz - mzRangeLower) / resolutionMZ * resolution);

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

		for (int line = 0; line < loadMZML.getLines(); line++) {
			System.out.println(String.format("\r%.2f", (float) line / loadMZML.getLines() * 100));
			System.out.flush();
			for (int x = 0; x < loadMZML.getWidth(); x++) {

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
			stats[i].diff = -(stats[i].i - stats[i].i1) * (stats[i].diff) * (stats[i].diff);
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

	public double getRange() {
		return range;
	}

	public Stats get(int i) {
		return stats[i];
	}

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

	public void printN(int n) {
		int[] result = getIndexesN(n);
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
