package pyhton;

public class IsLetterV2 implements IsLetter {

	public IsLetterV2(LoadMZXML loadMZML, double mzRangeLower, double mzRangeHighest, int resolution) {

	}

	@Override
	public boolean check(int x, int line) {
		// @classmethod
		// def V2(cls, loadMZML, mzRangeLower, mzRangeHighest, resolution, templateClass):
		// isLetterFunction = lambda x, line: templateClass.checkIfLetter(x, line)
		// return cls(loadMZML, mzRangeLower, mzRangeHighest, resolution,
		// isLetterFunction)
		return false;
	}

}
