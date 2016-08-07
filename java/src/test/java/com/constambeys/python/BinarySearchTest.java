package com.constambeys.python;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BinarySearchTest {

	@org.junit.Test
	public void testArrayWith6Elemenets() {

		BinarySearch<Integer> b = createClass(new Integer[] { 0, 2, 4, 6, 8, 10 });

		assertEquals(-1, b.search(0));
		assertEquals(0, b.search(1));
		assertEquals(0, b.search(2));
		assertEquals(1, b.search(3));
		assertEquals(4, b.search(9));
		assertEquals(4, b.search(10));
		assertEquals(5, b.search(11));
	}

	@org.junit.Test
	public void testArrayWith2Elemenets() {

		BinarySearch<Integer> b = createClass(new Integer[] { 0, 2 });

		assertEquals(-1, b.search(-1));
		assertEquals(-1, b.search(0));
		assertEquals(0, b.search(1));
		assertEquals(0, b.search(2));
		assertEquals(1, b.search(3));
	}

	@org.junit.Test
	public void testArrayWith1Elemenets() {

		BinarySearch<Integer> b = createClass(new Integer[] { 0 });

		assertEquals(-1, b.search(-1));
		assertEquals(-1, b.search(0));
		assertEquals(0, b.search(1));
	}

	@org.junit.Test
	public void testArrayWith0Elemenets() {
		BinarySearch<Integer> b = createClass(new Integer[] {});
	}

	private BinarySearch<Integer> createClass(Integer[] values) {
		List<Integer> list = new ArrayList<>();
		list.addAll(Arrays.asList(values));
		BinarySearch<Integer> b = new BinarySearch<Integer>(list);
		return b;
	}

}
