package com.constambeys.python;

public class BinEventlyDistributed implements IBinResolution {

	private double mzRangeLower;
	private double mzRangeHighest;
	private int resolution;
	private double resolutionMZ;
	private double range;
	private double mass[];

	public BinEventlyDistributed(double mzRangeLower, double mzRangeHighest, int resolution) {
		this.mzRangeLower = mzRangeLower;
		this.mzRangeHighest = mzRangeHighest;
		this.resolution = resolution;
		this.resolutionMZ = mzRangeHighest - mzRangeLower;
		this.range = resolutionMZ / resolution;
		this.mass = new double[resolution];

		for (int i = 0; i < resolution; i++) {
			mass[i] = i * range + mzRangeLower;
		}
	}

	@Override
	public double getRange() {
		return range;
	}

	@Override
	public int getBinsCount() {
		return resolution;
	}

	@Override
	public double getLowerMz(int index) {
		return mass[index];
	}

	@Override
	public double getHigherMz(int index) {
		return mass[index] + range;
	}

	@Override
	public int getMassIndex(double mz) {
		return (int) ((mz - mzRangeLower) / resolutionMZ * resolution);
	}

	@Override
	public double getLowerBound() {
		return mzRangeLower;
	}

	@Override
	public double getHigherBound() {
		return mzRangeHighest;
	}
}
