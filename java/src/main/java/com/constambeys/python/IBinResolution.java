package com.constambeys.python;

/**
 * 
 * This interface abstractly defines the bins at a given interval
 * 
 * @author Constambeys
 *
 */
public interface IBinResolution {

	/**
	 * @return the number of bins
	 */
	int getBinsCount();

	/**
	 * Calculates the position of the given mass value
	 * 
	 * @param mz
	 *            the mass per charge value
	 * @return the mass index
	 * @throws Exception
	 */
	int getMassIndex(double mz) throws Exception;

	/**
	 * @return the lowest mass per charge value
	 */
	double getLowerBound();

	/**
	 * @return the highest mass per charge value
	 */
	double getHigherBound();

	/**
	 * @param index
	 *            the bin index
	 * @return the lowest mass per charge value of the given bin
	 */
	double getLowerMz(int index);

	/**
	 * @param index
	 *            the bin index
	 * @return the higher mass per charge value of the given bin
	 */
	double getHigherMz(int index);

	/**
	 * @return the bin resolution - size
	 */
	double getRange();

}
