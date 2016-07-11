package com.constambeys.readers;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.apache.commons.codec.binary.Base64;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

public class MzXMLNative implements IReader {

	private List<Spectrum> peaks = new ArrayList<Spectrum>(1000);

	public MzXMLNative(File input) throws Exception {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream in = new BufferedInputStream(new FileInputStream(input));
		XMLStreamReader streamReader = inputFactory.createXMLStreamReader(in);

		while (streamReader.hasNext()) {
			if (streamReader.isStartElement()) {

				if (streamReader.getLocalName().equals("peaks")) {
					// precision="64"
					// byteOrder="network"
					if (!streamReader.getAttributeValue(0).equals("zlib"))
						throw new Exception("Invalid peak type");
					if (!streamReader.getAttributeValue(2).equals("64"))
						throw new Exception("Invalid peak type");
					if (!streamReader.getAttributeValue(3).equals("network"))
						throw new Exception("Invalid peak type");

					String encodedString = streamReader.getElementText();
					Spectrum spectrum = getSpectrumValues(encodedString);
					peaks.add(spectrum);
				}
			}
			streamReader.next();
		}
	}

	@Override
	public int getSpectraCount() {
		return peaks.size();
	}

	@Override
	public Spectrum getSpectrumByIndex(int index) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private Spectrum getSpectrumValues(String encodedString) throws IOException, DataFormatException {
		IReader.Spectrum spectrum = new Spectrum();

		byte[] decodedBytes = Base64.decodeBase64(encodedString.getBytes());
		// decompress the bytes
		byte[] decompressed_bytes = decompress(decodedBytes);

		ByteBuffer buffer = ByteBuffer.wrap(decompressed_bytes).order(ByteOrder.BIG_ENDIAN);

		while (buffer.hasRemaining()) {
			spectrum.mz.add(buffer.getDouble());
			spectrum.i.add(buffer.getDouble());
		}
		return spectrum;
	}

	public static byte[] decompress(byte[] data) throws IOException, DataFormatException {

		Inflater inflater = new Inflater();

		inflater.setInput(data);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

		byte[] buffer = new byte[1024];

		while (!inflater.finished()) {

			int count = inflater.inflate(buffer);

			outputStream.write(buffer, 0, count);
		}

		outputStream.close();

		byte[] output = outputStream.toByteArray();

		return output;

	}

}
