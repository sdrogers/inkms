package com.constambeys.python;

/**
 * 
 * Used to provide progress information
 * 
 * @author Constambeys
 *
 */
public interface IProgress {
	/**
	 * Callback method to provide progress updates
	 * 
	 * @param percent
	 *            the current status 0-100%
	 */
	public void update(int percent);
}
