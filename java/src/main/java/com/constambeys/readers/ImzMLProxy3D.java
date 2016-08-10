package com.constambeys.readers;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;

import imzML.ImzML;
import mzML.Scan;
import mzML.SpectrumList;

/**
 * The {@code ImzMLProxy} class reads imzML data
 * 
 * @author Constambeys
 */
public class ImzMLProxy3D implements IReader {
	private ImzML imzML;
	private int nColumns;
	private int nRows;
	private ArrayList<Integer> xy[][];

	public ImzMLProxy3D(File mzML) throws Exception {

		imzML = imzMLConverter.ImzMLHandler.parseimzML(mzML.getAbsolutePath());

		nColumns = imzML.getWidth();
		nRows = imzML.getHeight();
		xy = new ArrayList[nRows][nColumns];

		for (int r = 0; r < nRows; r++) {
			for (int c = 0; c < nColumns; c++) {
				xy[r][c] = new ArrayList<Integer>();
			}
		}

		SpectrumList spectrumList = imzML.getRun().getSpectrumList();
		int nSpec = spectrumList.getChildCount();

		for (int i = 0; i < nSpec; i++) {
			Scan scan = spectrumList.getSpectrum(i).getScanList().getScan(0);
			// 1 based
			int positionX = Integer.parseInt(scan.getCVParam("IMS:1000050").getValue());
			int positionY = Integer.parseInt(scan.getCVParam("IMS:1000051").getValue());

			xy[positionY - 1][positionX - 1].add(i);

		}
	}

	@Override
	public Spectrum getSpectrumByIndex(int index) {
		index--;

		int y = index / nColumns;
		int x = index % nColumns;

		SpectrumList spectrumList = imzML.getRun().getSpectrumList();

		double mzs[] = new double[0];
		double ints[] = new double[0];

		for (int i : xy[y][x]) {
			mzML.Spectrum orgS = spectrumList.getSpectrum(i);
			mzs = (double[]) ArrayUtils.addAll(mzs, orgS.getmzArray());
			ints = (double[]) ArrayUtils.addAll(ints, orgS.getIntensityArray());
		}

		return new Spectrum(mzs, ints);
	}

	@Override
	public ScanType getSpectraPolarity(int index) throws Exception {
		throw new Exception("Scan Polarity not supported");
	}

	@Override
	public int getSpectraCount() {
		return nColumns * nRows;
	}

	public int getLines() {
		return nRows;
	}

}
