package com.constambeys.readers;

import java.io.File;

public class Main {

	public static void main(String[] args) throws Exception {
		long startTime = System.nanoTime();

		File mzxmlFile = new File("E:\\Enironments\\data\\abcdefgh_1.mzXML");
		MzXMLNative reader = new MzXMLNative(mzxmlFile);

		long estimatedTime = System.nanoTime() - startTime;
		System.out.println(String.format("%.3fs", estimatedTime / 1000000000.0));

		System.out.println(reader.getSpectraCount());
	}

}
