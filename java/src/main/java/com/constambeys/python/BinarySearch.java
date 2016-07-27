package com.constambeys.python;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BinarySearch<T> {

	private List<T> list;

	/**
	 * @param sorted
	 *            list
	 */
	public BinarySearch(List<T> list) {
		this.list = list;
	}

	/**
	 * @return index
	 *         <p>
	 *         value is between list[index] < value <= list[index+1]
	 *         <p>
	 *         index == size-1 then list[size-1] < value
	 */
	public int search(Comparable<T> value) {
		int first = 0;
		int last = list.size() - 2;

		while (first <= last) {
			int midpoint = (first + last) / 2;
			int v1 = value.compareTo(list.get(midpoint));
			int v2 = value.compareTo(list.get(midpoint + 1));

			// value > midpoint && value <= midpoint +1
			if (v1 == 1 && v2 <= 0) {
				return midpoint;
				//  value <= 0
			} else if (midpoint == 0 && value.compareTo(list.get(0)) <= 0) {
				return -1;
			} else {
				// value <= midpoint
				if (v1 <= 0) {
					last = midpoint - 1;
				} else {
					first = midpoint + 1;
				}
			}
		}

		return list.size() - 1;
	}

	public static void main(String[] args) {

		List<Integer> list = new ArrayList<>();
		list.addAll(Arrays.asList(new Integer[] { 0, 2, 4, 6, 8, 10 }));

		BinarySearch<Integer> b = new BinarySearch<Integer>(list);
		System.out.println(b.search(1));
	}

}
