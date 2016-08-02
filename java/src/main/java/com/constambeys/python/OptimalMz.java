package com.constambeys.python;

import java.util.ArrayList;
import java.util.List;

import com.constambeys.readers.MSIImage;

/**
 * Abstract class that provides basic functionality for optimal mass detection
 * 
 * @author Constambeys
 *
 */
public abstract class OptimalMz {

	public class BinStatistics implements Comparable<BinStatistics> {
		/**
		 * Saves bin index used to retrieve bin information
		 */
		public int index;
		/**
		 * The rank of this bin used for sorting the bins
		 */
		public double diff;
		/**
		 * The added intensity values of selected region - 0
		 */
		public int c;
		/**
		 * The sum or average intensity of selected region 0
		 */
		public double i;
		/**
		 * The added intensity values of remaining region - 1
		 */
		public int c1;
		/**
		 * The sum or average intensity of remaining region - 1
		 */
		public double i1;

		@Override
		public int compareTo(BinStatistics o) {
			return Double.compare(diff, o.diff);
		}
	}

	protected BinStatistics[] binStatistics;

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
	public List<BinStatistics> getTopResults(int n, int threshold_i) {

		ArrayList<BinStatistics> result = new ArrayList<BinStatistics>(n);

		for (int i = 0; i < binStatistics.length; i++) {
			if (result.size() == n)
				break;

			if (binStatistics[i].i >= threshold_i && binStatistics[i].i1 >= threshold_i) {
				result.add(binStatistics[i]);
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
	public List<BinStatistics> getTopResults(int n) {
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
		List<BinStatistics> result = getTopResults(n);
		for (BinStatistics r : result) {
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
