package com.constambeys.python;

import java.awt.Color;
import java.awt.image.BufferedImage;
import org.junit.Assert;

import com.constambeys.files.VirtualFile1;
import com.constambeys.filtering.IFiltering;
import com.constambeys.filtering.NoFiltering;
import com.constambeys.patterns.ILoadPattern;
import com.constambeys.patterns.PatternNormal;
import com.constambeys.python.OptimalMz.BinStatistics;
import com.constambeys.readers.MSIImage;

public class OptimalMzV1Test {

	VirtualFile1 reader;
	IFiltering filtering;
	ILoadPattern pattern;
	MSIImage msiimage;

	public OptimalMzV1Test() throws Exception {

		reader = new VirtualFile1();
		filtering = new NoFiltering(reader);

		PatternNormal.Param param = new PatternNormal.Param();
		param.lines = reader.getLines();
		param.widthInMM = reader.getWidth();
		param.heightInMM = reader.getHeight();
		pattern = new PatternNormal(filtering, param);
		msiimage = new MSIImage(reader, pattern);
	}

	@org.junit.Test
	public void Test() throws Exception {
		IBinResolution bins = new BinEvenlyDistributed(0, 500, 500);
		BufferedImage template = new BufferedImage(reader.getWidth(), reader.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int row = 0; row < reader.getLines(); row++) {
			for (int column = 0; column < reader.getColumns(); column++) {
				Color c = reader.patternNormal[row][column] ? Color.BLACK : Color.WHITE;
				template.setRGB(column, row, c.getRGB());
			}
		}
		ICheckLetter isLetter = new IsLetterV2(template, Color.BLACK.getRGB());
		OptimalMz optimalMz = new OptimalMzV1(msiimage, isLetter, bins);
		optimalMz.run();
		BinStatistics b = optimalMz.getTopResults(1).get(0);
		Assert.assertEquals(125, b.i, 0.001);
		Assert.assertEquals(10, b.i1, 0.001);
		Assert.assertEquals(115, b.diff, 0.001);
		
		// System.out.println(optimalMz.printTopResults(1));
	}

}
