package com.constambeys.python;

import java.util.ArrayList;
import java.util.List;

import com.constambeys.load.MSIImage;

public abstract class OptimalMz {

	public class Stats implements Comparable<Stats> {
		public int index;
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

	protected Stats[] stats;

	protected MSIImage loadMZML;
	protected ICheckLetter isLetter;
	protected IBinResolution bins;
	protected IProgress p;

	public OptimalMz(MSIImage loadMZML, ICheckLetter isLetter, IBinResolution bins) {
		this.loadMZML = loadMZML;
		this.isLetter = isLetter;
		this.bins = bins;
	}

	public abstract void run() throws Exception;

	public List<Stats> getIndexesN(int n, int threshold_i) {

		ArrayList<Stats> result = new ArrayList<Stats>(n);

		for (int i = 0; i < stats.length; i++) {
			if (result.size() == n)
				break;

			if (stats[i].i >= threshold_i && stats[i].i1 >= threshold_i) {
				result.add(stats[i]);
			}
		}

		return result;
	}

	public List<Stats> getIndexesN(int n) {
		return getIndexesN(n, 0);
	}

	public String printN(int n) {
		StringBuffer sb = new StringBuffer();
		List<Stats> result = getIndexesN(n);
		for (Stats r : result) {
			sb.append(String.format("mz: %f - %f , i: %f - %f, c: %d - %d \n", bins.getLowerMz(r.index), bins.getHigherMz(r.index), r.i, r.i1, r.c, r.c1));
			sb.append(String.format("i1 - i: "));
			sb.append(String.format("%f\n", r.diff));
		}
		sb.append(String.format("range: "));
		sb.append(String.format("%f\n", bins.getRange()));
		return sb.toString();
	}

	public void setProgressListener(IProgress p) {
		this.p = p;
	}
}
