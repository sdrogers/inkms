package pyhton;

public class IsLetterV1 implements ICheckLetter {

	private int x_start;
	private int x_stop;
	private int y_start;
	private int y_stop;

	public IsLetterV1(LoadMZXML loadMZML, double x_start_mm, double x_stop_mm, double y_start_mm, double y_stop_mm) {

		this.x_start = (int) (x_start_mm / loadMZML.getWidthMM() * loadMZML.getWidth());
		this.x_stop = (int) (x_stop_mm / loadMZML.getWidthMM() * loadMZML.getWidth());
		this.y_start = (int) (y_start_mm / loadMZML.getHeightMM() * loadMZML.getLines());
		this.y_stop = (int) (y_stop_mm / loadMZML.getHeightMM() * loadMZML.getLines());
	}

	@Override
	public boolean check(int x, int line) {
		return x_start <= x && x <= x_stop && y_start <= line && line <= y_stop;
	}
}
