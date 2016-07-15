package com.constambeys.python;

public interface IBinResolution {

	int getBinsCount();

	int getMassIndex(double mz) throws Exception;

	double getLowerBound();

	double getHigherBound();

	double getLowerMz(int index);

	double getHigherMz(int index);

	double getRange();

}
