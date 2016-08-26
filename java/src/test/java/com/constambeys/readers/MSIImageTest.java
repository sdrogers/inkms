package com.constambeys.readers;

import org.junit.Assert;

import com.constambeys.files.VirtualFile1;
import com.constambeys.files.VirtualFile2;
import com.constambeys.filtering.IFiltering;
import com.constambeys.filtering.NoFiltering;
import com.constambeys.filtering.PolarityFiltering;
import com.constambeys.patterns.ILoadPattern;
import com.constambeys.patterns.PatternMeandering;
import com.constambeys.patterns.PatternNormal;
import com.constambeys.readers.IReader.ScanType;

public class MSIImageTest {

	private double R0 = 250f;
	private double R1 = 10f;

	/**
	 * File: File1
	 * <p>
	 * Filtering: NoFiltering
	 * <p>
	 * ILoadPattern: Pattern1
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void test1PatternMeandering() throws Exception {

		VirtualFile1 reader;
		IFiltering filtering;
		ILoadPattern pattern;
		MSIImage msiimage;

		reader = new VirtualFile1();
		filtering = new NoFiltering(reader);

		PatternMeandering.Param param = new PatternMeandering.Param();
		param.lines = reader.getLines();
		int lines = reader.getLines();
		int columns = reader.getColumns();
		param.widthInMM = reader.getWidth();
		param.heightInMM = reader.getHeight();
		param.dropInMM = 0;
		pattern = new PatternMeandering(filtering, param);
		msiimage = new MSIImage(reader, pattern);

		double[][] intensity = msiimage.getTotalIntensity(null, 0, 500);

		Assert.assertEquals(reader.getLines(), intensity.length);
		Assert.assertEquals(reader.getColumns(), intensity[0].length);

		for (int line = 0; line < lines; line++) {
			for (int column = 0; column < columns; column++) {
				if (line % 2 == 0) {
					if (reader.patternNormal[line][column]) {
						Assert.assertEquals(R0, intensity[line][column], 0.001);
					} else {
						Assert.assertEquals(R1, intensity[line][column], 0.001);
					}
				} else {
					if (reader.patternNormal[line][columns - 1 - column]) {
						Assert.assertEquals(R0, intensity[line][column], 0.001);
					} else {
						Assert.assertEquals(R1, intensity[line][column], 0.001);
					}
				}
			}
		}

	}

	/**
	 * File: File2
	 * <p>
	 * Filtering: NoFiltering
	 * <p>
	 * ILoadPattern: Pattern1
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void test2PatternMeandering() throws Exception {

		VirtualFile2 reader;
		IFiltering filtering;
		ILoadPattern pattern;
		MSIImage msiimage;

		reader = new VirtualFile2();
		filtering = new NoFiltering(reader);

		PatternMeandering.Param param = new PatternMeandering.Param();
		param.lines = reader.getLines();
		int lines = reader.getLines();
		int columns = reader.getColumns();
		param.widthInMM = reader.getWidth();
		param.heightInMM = reader.getHeight();
		param.dropInMM = 0;
		pattern = new PatternMeandering(filtering, param);
		msiimage = new MSIImage(reader, pattern);

		double[][] intensity = msiimage.getTotalIntensity(null, 0, 500);

		Assert.assertEquals(reader.getLines(), intensity.length);
		Assert.assertEquals(reader.getColumns(), intensity[0].length);

		for (int line = 0; line < lines; line++) {
			for (int column = 0; column < columns; column++) {
				if (line % 2 == 0) {
					if (reader.patternNormal[line][column]) {
						Assert.assertEquals(R0, intensity[line][column], 0.001);
					} else {
						Assert.assertEquals(R1, intensity[line][column], 0.001);
					}
				} else {
					if (reader.patternNormal[line][columns - 1 - column]) {
						Assert.assertEquals(R0, intensity[line][column], 0.001);
					} else {
						Assert.assertEquals(R1, intensity[line][column], 0.001);
					}
				}
			}
		}

	}

	/**
	 * File: File1
	 * <p>
	 * Filtering: NoFiltering
	 * <p>
	 * ILoadPattern: Pattern2
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void test1PatternNormal() throws Exception {

		VirtualFile1 reader;
		IFiltering filtering;
		ILoadPattern pattern;
		MSIImage msiimage;

		reader = new VirtualFile1();
		filtering = new NoFiltering(reader);

		PatternNormal.Param param = new PatternNormal.Param();
		param.lines = reader.getLines();
		param.widthInMM = reader.getWidth();
		param.heightInMM = reader.getHeight();
		pattern = new PatternNormal(filtering, param);
		msiimage = new MSIImage(reader, pattern);

		double[][] intensity = msiimage.getTotalIntensity(null, 0, 500);

		Assert.assertEquals(reader.getLines(), intensity.length);
		Assert.assertEquals(reader.getColumns(), intensity[0].length);

		for (int line = 0; line < intensity.length; line++) {
			for (int column = 0; column < intensity[0].length; column++) {
				if (reader.patternNormal[line][column]) {
					Assert.assertEquals(R0, intensity[line][column], 0.001);
				} else {
					Assert.assertEquals(R1, intensity[line][column], 0.001);
				}
			}
		}

	}

	/**
	 * File: File2
	 * <p>
	 * Filtering: NoFiltering
	 * <p>
	 * ILoadPattern: Pattern2
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void test2PatternNormal() throws Exception {

		VirtualFile2 reader;
		IFiltering filtering;
		ILoadPattern pattern;
		MSIImage msiimage;

		reader = new VirtualFile2();
		filtering = new NoFiltering(reader);

		PatternNormal.Param param = new PatternNormal.Param();
		param.lines = reader.getLines();
		param.widthInMM = reader.getWidth();
		param.heightInMM = reader.getHeight();
		pattern = new PatternNormal(filtering, param);
		msiimage = new MSIImage(reader, pattern);

		double[][] intensity = msiimage.getTotalIntensity(null, 0, 500);

		Assert.assertEquals(reader.getLines(), intensity.length);
		Assert.assertEquals(reader.getColumns(), intensity[0].length);

		for (int line = 0; line < intensity.length; line++) {
			for (int column = 0; column < intensity[0].length; column++) {
				if (reader.patternNormal[line][column]) {
					Assert.assertEquals(R0, intensity[line][column], 0.001);
				} else {
					Assert.assertEquals(R1, intensity[line][column], 0.001);
				}
			}
		}

	}

	/**
	 * * File: File1
	 * <p>
	 * Filtering: PolarityFiltering
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void testPositiveFiltering() throws Exception {
		VirtualFile1 reader;
		IFiltering filtering;

		reader = new VirtualFile1();
		filtering = new PolarityFiltering(reader, ScanType.POSITIVE);

		Assert.assertEquals(reader.getSpectraCount(), filtering.getSpectraCount());

	}

	/**
	 * * * File: File1
	 * <p>
	 * Filtering: PolarityFiltering
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void testNegativeFiltering() throws Exception {
		VirtualFile1 reader;
		IFiltering filtering;

		reader = new VirtualFile1();
		filtering = new PolarityFiltering(reader, ScanType.NEGATIVE);

		Assert.assertEquals(0, filtering.getSpectraCount());

	}

}
