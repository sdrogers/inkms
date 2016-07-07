package pyhton;

import java.awt.image.BufferedImage;

public class IsLetterV2 implements ICheckLetter {

	private BufferedImage imgTemplate;
	private int width;
	private int height;

	// -1 white
	// -16777216 black
	public IsLetterV2(BufferedImage imgTemplate) {
		this.imgTemplate = imgTemplate;
		this.width = imgTemplate.getWidth();
		this.height = imgTemplate.getHeight();
	}

	@Override
	public boolean check(int x, int line) {
		if (x >= 0 && x < width && line >= 0 && line < height) {
			if (imgTemplate.getRGB(x, line) == -16777216) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
