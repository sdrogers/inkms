package com.constambeys.python;

import java.util.ArrayList;
import java.util.List;

import com.constambeys.load.MSIImage;

/**
 * Abstract class that provides basic functionality for optimal mass detection
 * 
 * @author Constambeys
 *
 */
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

	protected MSIImage msiimage;
	protected ICheckLetter isLetter;
	protected IBinResolution bins;
	protected IProgress callback;

	/**
	 * @param msiimage
	 *            the loaded mass spectrometry image
	 * @param isLetter
	 *            class that identifies letter pixels
	 * @param bins
	 *            class that defines the bins distribution
	 */
	public OptimalMz(MSIImage msiimage, ICheckLetter isLetter, IBinResolution bins) {
		this.msiimage = msiimage;
		this.isLetter = isLetter;
		this.bins = bins;
	}

	/**
	 * Calculate the optimal mass intervals
	 * 
	 * @throws Exception
	 */
	public abstract void run() throws Exception;

	/**
	 * returns the top n results that have on average intensity above threshold
	 * 
	 * @param n
	 *            the number of results returned
	 * @param threshold_i
	 *            intesity threshold
	 * @return a list of results
	 */
	public List<Stats> getTopResults(int n, int threshold_i) {

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

	/**
	 * returns the top n results
	 * 
	 * @param n
	 *            the number of results returned
	 * @return a list of results
	 */
	public List<Stats> getTopResults(int n) {
		return getTopResults(n, 0);
	}

	/**
	 * returns the top n results statistics
	 * 
	 * @param n
	 *            the number of results printed
	 * @return String variable
	 */
	public String printTopResults(int n) {
		StringBuffer sb = new StringBuffer();
		List<Stats> result = getTopResults(n);
		for (Stats r : result) {
			sb.append(String.format("mz: %f - %f , i: %f - %f, c: %d - %d \n", bins.getLowerMz(r.index), bins.getHigherMz(r.index), r.i, r.i1, r.c, r.c1));
			sb.append(String.format("i1 - i: "));
			sb.append(String.format("%f\n", r.diff));
		}
		sb.append(String.format("range: "));
		sb.append(String.format("%f\n", bins.getRange()));
		return sb.toString();
	}

	/**
	 * Sets a progress listener
	 * 
	 * @param callback
	 *            during progress updates
	 */
	public void setProgressListener(IProgress callback) {
		this.callback = callback;
	}
}
