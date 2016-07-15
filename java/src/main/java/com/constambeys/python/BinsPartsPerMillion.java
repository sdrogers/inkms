package com.constambeys.python;

import java.util.ArrayList;

public class BinsPartsPerMillion implements IBinResolution {

	Double[] mzs;

	public BinsPartsPerMillion(double mzRangeLower, double mzRangeHighest, int ppm) throws Exception {
		if (mzRangeLower == 0)
			throw new Exception("Lower Mass should be not zero");
		
		ArrayList<Double> arr_mzs = new ArrayList<Double>(100);
		double ratio = ppm / 1000000d;
		double lowerBound = mzRangeLower * (1 - ratio);
		double mz = lowerBound;
		arr_mzs.add(lowerBound);
		while (mz < mzRangeHighest) {
			mz = mz * (1 + ratio) / (1 - ratio);
			arr_mzs.add(mz);
		}

		mzs = arr_mzs.toArray(new Double[1]);

	}

	@Override
	public int getBinsCount() {
		return mzs.length - 1;
	}

	@Override
	public int getMassIndex(double mz) throws Exception {
		for (int i = 0; i < mzs.length - 1; i++) {
			if (mzs[i] <= mz && mzs[i + 1] >= mz) {
				return i;
			}
		}
		throw new Exception("Invalid mz value");
	}

	@Override
	public double getLowerBound() {
		return mzs[0];
	}

	@Override
	public double getHigherBound() {
		return mzs[mzs.length - 1];
	}

	@Override
	public double getLowerMz(int index) {
		return mzs[index];
	}

	@Override
	public double getHigherMz(int index) {
		return mzs[index + 1];
	}

	@Override
	public double getRange() {
		return 0;
	}

	public static void main(String[] args) throws Exception {
		new BinsPartsPerMillion(100, 500, 5000);
	}

}
