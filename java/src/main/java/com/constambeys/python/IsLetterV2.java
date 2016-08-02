package com.constambeys.python;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Identifies letters using an overlay image
 * 
 * @author Constambeys
 *
 */
public class IsLetterV2 implements ICheckLetter {

	private BufferedImage imgOverlay;
	private int width;
	private int height;
	private int letter;

	/**
	 * Constructs a new {@code IsLetterV2} class using an overlay image
	 * 
	 * @param imgTemplate
	 *            to identify letters based on the image color
	 * @param letter
	 *            the colour of letters
	 */
	public IsLetterV2(BufferedImage imgTemplate, Color letter) {
		this.imgOverlay = imgTemplate;
		this.width = imgTemplate.getWidth();
		this.height = imgTemplate.getHeight();
		this.letter = letter.getRGB();
	}

	@Override
	public boolean check(int x, int line) {
		if (x >= 0 && x < width && line >= 0 && line < height) {
			if (imgOverlay.getRGB(x, line) == letter) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
