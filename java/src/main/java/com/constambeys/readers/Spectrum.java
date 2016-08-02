package com.constambeys.readers;

public class Spectrum {

	public double mzs[];
	public double ints[];

	public Spectrum(int size) {
		mzs = new double[size];
		ints = new double[size];
	}

	public Spectrum(double mzs[], double ints[]) {
		this.mzs = mzs;
		this.ints = ints;
	}

}
