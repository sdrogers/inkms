package com.constambeys.ui.graph;

import org.junit.Assert;

public class UtilitiesTest {

	@org.junit.Test
	public void calculateFirstDesimalPlace() {

		int decimalPlace;
		decimalPlace = Utilities.calculateFirstDesimalPlace(10f);
		Assert.assertEquals(0, decimalPlace);

		decimalPlace = Utilities.calculateFirstDesimalPlace(1f);
		Assert.assertEquals(0, decimalPlace);

		decimalPlace = Utilities.calculateFirstDesimalPlace(0.12f);
		Assert.assertEquals(1, decimalPlace);
	}

	@org.junit.Test
	public void calculateDesimalPlaces() {

		int decimalPlace;
		decimalPlace = Utilities.calculateDesimalPlaces(1.23f);
		Assert.assertEquals(2, decimalPlace);
		decimalPlace = Utilities.calculateDesimalPlaces(0f);
		Assert.assertEquals(0, decimalPlace);

	}
}
