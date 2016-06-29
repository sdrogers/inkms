package pyhton;

import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;

public class OptimalMzV2 extends OptimalMz {

	public OptimalMzV2(LoadMZXML loadMZML, double mzRangeLower, double mzRangeHighest, int resolution)
			throws JMzReaderException {
		super();
		init(loadMZML, mzRangeLower, mzRangeHighest, resolution);
	}

	@Override
	public boolean isLetter(int x, int line) {
	    //@classmethod
	    //def V2(cls, loadMZML, mzRangeLower, mzRangeHighest, resolution, templateClass):
	    //    isLetterFunction = lambda x, line: templateClass.checkIfLetter(x, line)
	    //    return cls(loadMZML, mzRangeLower, mzRangeHighest, resolution, isLetterFunction)
		return false;
	}

}
