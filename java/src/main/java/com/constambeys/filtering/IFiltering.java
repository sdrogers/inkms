package com.constambeys.filtering;

import com.constambeys.readers.IReader;

/**
 * Extends {@code IReader } interface
 * <p>
 * Hides mass spectrometry data that are not needed
 * 
 * @author Constambeys
 *
 */
public interface IFiltering extends IReader {

	/**
	 * @param index
	 *            1 based index
	 * @return the real file index based on the filtering index
	 */
	public int getRealIndex(int index);
}
