package com.constambeys.ui;

import com.constambeys.ui.graph.Utilities;

/**
 * Displays a mass range in two coded formats
 * 
 * @author Constambeys
 */
public class MassRange {
	double lowerMass;
	double higherMass;
	int decimalPlaces;

	MassRange(double lowerMass, double higherMass) {
		init(lowerMass, higherMass);
	}

	MassRange(String lowerMass, String higherMass) {
		double lowerMass_ = Double.parseDouble(lowerMass);
		double higherMass_ = Double.parseDouble(higherMass);
		init(lowerMass_, higherMass_);
	}

	private void init(double lowerMass, double higherMass) {
		this.lowerMass = lowerMass;
		this.higherMass = higherMass;
		this.decimalPlaces = Math.max(Utilities.calculateDesimalPlaces(lowerMass), Utilities.calculateDesimalPlaces(higherMass));
	}

	public String lowerMass() {
		return String.format("%." + decimalPlaces + "f", lowerMass);
	}

	public String higherMass() {
		return String.format("%." + decimalPlaces + "f", higherMass);
	}

	@Override
	public String toString() {
		return String.format("%sm/z - %sm/z", lowerMass(), higherMass());
	}

	public String toStringV2() {
		double average = (higherMass + lowerMass) / 2;
		String title = String.format("m/z: %." + (decimalPlaces + 1) + "f \u00B1 %." + (decimalPlaces + 1) + "f Th", average, average - lowerMass);
		return title;
	}

	public static double[] convertToDouble(MassRange[] ranges) {
		double massrange[] = new double[ranges.length * 2];

		for (int i = 0; i < ranges.length; i++) {
			MassRange range = ranges[i];
			massrange[2 * i] = range.lowerMass;
			massrange[2 * i + 1] = range.higherMass;
		}
		return massrange;
	}

}