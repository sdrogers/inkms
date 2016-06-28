package readers;

import java.util.ArrayList;
import java.util.List;

public interface IReader {

	class Spectrum {
		List<Double> mz = new ArrayList<Double>();
		List<Double> i = new ArrayList<Double>();;
	}

	public int getSpectraCount();

	public Spectrum getSpectrumByIndex(int index) throws Exception;
}
