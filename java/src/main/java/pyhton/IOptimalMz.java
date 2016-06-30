package pyhton;

public interface IOptimalMz {
	public double getRange();

	public double getMz(int i);

	public int[] getIndexesN(int n, int threshold_i);

	public int[] getIndexesN(int n);

	public String printN(int n);
}
