package com.constambeys.ui.colormaps;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Display a vertical colour bar
 * 
 * @author Constambeys
 *
 */
public class ColormapShow extends JPanel {

	protected IColormap colormap;

	public ColormapShow() {
	}

	public void setColormap(IColormap colormap) {
		this.colormap = colormap;
		this.repaint();
	}

	public IColormap getColormap() {
		return this.colormap;
	}

	@Override
	protected void paintComponent(Graphics g) {
		try {
			super.paintComponent(g);
			if (colormap == null)
				return;

			int height = getHeight();
			int width = getWidth();
			int max = colormap.getMaxIndex();
			for (int i = 0; i <= height; i++) {
				// Reverse top to bottom height;
				int j = (height - i);
				g.setColor(new Color(colormap.get((int) ((float) j / height * max))));
				g.fillRect(0, i, width, 1);
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}

	/**
	 * Test
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ColormapShow colormapShow = new ColormapShow();
		// colormapShow.setColormap(new Hot());
		// colormapShow.setColormap(new Gray());
		colormapShow.setColormap(new Custom(0.5f));

		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(colormapShow);
		f.setVisible(true);
	}
}
