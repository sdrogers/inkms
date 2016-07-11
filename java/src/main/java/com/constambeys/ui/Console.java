package com.constambeys.ui;

import java.io.File;

import uk.ac.ebi.pride.tools.mzml_wrapper.MzMlWrapper;

public class Console {

	public static void main(String[] args) throws Exception {
		new Console().run();
	}

	public void run() throws Exception {

		MzMlWrapper run = new MzMlWrapper(new File("E:\\Enironments\\data\\abcdefgh_1.mzML"));
		System.out.println(run.getSpectraCount());
	}

}
