package com.constambeys.ui.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import com.constambeys.python.BinarySearch;

public class PanelVlines extends JPanel {

	protected final int PAINT_MAX_LINES_ON_SCREEN = 10000;
	protected final int SEARCH = 300;
	protected final double ZOOM_LOWER_BOUND = 0.5d;

	protected final int MARGIN_X_LEFT = 40;
	protected final int MARGIN_X_RIGHT = 5;
	protected final int MARGIN_Y_BOTTOM = 30;
	protected final int MARGIN_Y_TOP = 30;

	protected final int MARGIN_ARROW_LEFT = 3;
	protected final int MARGIN_ARROW_DOWN = 5;

	protected final int MARGIN_X_AXIS = 3;
	protected final int MARGIN_Y_AXIS = 3;

	protected final int MARGIN_LABELS_X = 25;
	protected final int MARGIN_LABELS_Y = 38;

	// Graphics
	protected Font fontAxis;

	/**
	 * Real value statistics
	 */
	protected double xmin;
	protected double xmax;
	protected double ymin;
	protected double ymax;

	/**
	 * Zoom = 1 No Zoom
	 * <p>
	 * Zoom = 2 2x
	 * 
	 * Zoom = 0.5 0.5x
	 */
	protected double zoom = 1;
	/**
	 * Real x value
	 */
	protected double xstart = 0;

	/**
	 * All Sorted
	 */
	protected List<Vline> lines;
	protected BinarySearch<Vline> blines;
	protected Comparable<Vline> findxstart;
	/**
	 * Visible Sorted
	 */
	protected List<Vline> visible;
	protected BinarySearch<Vline> bvisible;

	// Calculated during paint
	protected int available_height;
	protected int available_width;

	// Display hovering info
	boolean showInfo;
	Vline closest;

	/**
	 * Initialises a new {@code PanelVlines }
	 */
	public PanelVlines() {
		super();
		lines = new ArrayList<>();
		blines = new BinarySearch<>(lines);
		visible = new ArrayList<>();
		bvisible = new BinarySearch<>(visible);
		fontAxis = new Font("SansSerif", Font.BOLD, 10);

		MyMouseListener listener = new MyMouseListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
		// https://kodejava.org/how-do-i-handle-mouse-wheel-event/
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {

				double zoomOrg = zoom;

				// If wheel rotation value is a negative it means rotate up, while
				// positive value means rotate down
				if (e.getWheelRotation() < 0) {
					// System.out.println("Rotated Up... " + e.getWheelRotation());
					zoom += 0.5;
				} else {
					// System.out.println("Rotated Down... " + e.getWheelRotation());
					zoom -= 0.5;
					if (zoom < ZOOM_LOWER_BOUND) {
						zoom = ZOOM_LOWER_BOUND;
					}
				}

				// Zoom to mouse position
				// Correcting xx
				xstart = xstart + (e.getX() - MARGIN_X_LEFT) * (xmax - xmin) * (zoom - zoomOrg) / (zoom * zoomOrg * available_width);

				// Get scrolled unit amount
				// System.out.println("ScrollAmount: " + e.getScrollAmount());
				PanelVlines.this.repaint();
			}
		});

	}

	/**
	 * After adding all values call addCommit
	 *
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 */
	public void add(double x, double y) {
		Vline line = new Vline(x, y);
		lines.add(line);
	}

	/**
	 * After adding all values call addCommit
	 *
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @param color
	 *            arrow colour
	 */
	public void add(double x, double y, Color color) {
		Vline line = new Vline(x, y, color);
		lines.add(line);
	}

	/**
	 * Call to update internal state
	 * @throws Exception 
	 */
	public void addCommit() throws Exception {
		Collections.sort(lines);
		calculateStatistics();
		xstart = xmin;
	}

	/**
	 * Paint image x and y axis (same as {@code PanelGraph})
	 * 
	 * @param g2d
	 *            Graphics Object
	 * @param width
	 *            the image width
	 * @param height
	 *            the image height
	 * @param margin_y_top
	 *            the top margin
	 * 
	 */
	protected void paintAxis(Graphics2D g2d, int width, int height, int margin_y_top) {
		// Axis
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(3));
		// X Axis left -> right
		g2d.drawLine(MARGIN_X_LEFT - MARGIN_Y_AXIS, margin_y_top + height + MARGIN_X_AXIS, MARGIN_X_LEFT - MARGIN_Y_AXIS + width, margin_y_top + height + MARGIN_X_AXIS);
		// Y Axis top -> bottom
		g2d.drawLine(MARGIN_X_LEFT - MARGIN_Y_AXIS, margin_y_top, MARGIN_X_LEFT - MARGIN_Y_AXIS, margin_y_top + height);
	}

	/**
	 * Calculates the interval's decimal places
	 * <p>
	 * for example 10 order 0, 1 order 0, 0.1 order 1 ...
	 *
	 * @param interval
	 * @return order
	 */
	protected int calculateDesimalPlaces(double interval) {
		int accuracy = 0;
		while (interval < 1) {
			interval = interval * 10;
			accuracy++;
		}

		return accuracy;
	}

	/**
	 * Updates input data statistical values min, max
	 * 
	 * @throws Exception
	 */
	protected void calculateStatistics() throws Exception {

		if (lines.size() == 0) {
			return;
		}

		xmin = lines.get(0).x;
		xmax = lines.get(lines.size() - 1).x;
		ymin = lines.get(0).y;
		ymax = lines.get(0).y;

		for (Vline line : lines) {
			double y = line.y;

			if (ymin > y)
				ymin = y;
			if (ymax < y)
				ymax = y;
		}

		if (xmax == xmin) {
			xmax = 100;
			xmin = 0;
			throw new Exception("Too few data");
		}

		if (ymax == ymin) {
			ymax = 100;
			ymin = 0;
			throw new Exception("Too few data");
		}

		return;
	}

	/**
	 * Paint image labels (almost the same as {@code PanelGraph}) Added accuracy control
	 * 
	 * @param g2d
	 *            Graphics Object
	 * @param width
	 *            the image width
	 * @param height
	 *            the image height
	 * @param margin_y_top
	 *            the top margin
	 */
	protected void paintLabels(Graphics2D g2d, int width, int height, int margin_y_top) {
		FontMetrics axisFontMetrics = g2d.getFontMetrics(fontAxis);
		g2d.setFont(fontAxis);
		g2d.setStroke(new BasicStroke(1));
		int yOffset = axisFontMetrics.getAscent() / 2;

		int decimalPlaces = 0;
		decimalPlaces = calculateDesimalPlaces(PixelsToY(1, 10));

		// Draw y values
		for (int i = 0; i <= 10; i++) {
			int y = margin_y_top + height * i / 10;
			String value = String.format("%." + decimalPlaces + "e", PixelsToY(10 - i, 10));
			// Pointer
			g2d.drawLine(MARGIN_X_LEFT - 5, y, MARGIN_X_LEFT + 5, y);
			g2d.drawString(value, MARGIN_X_LEFT - MARGIN_LABELS_Y, y + yOffset);
		}

		decimalPlaces = calculateDesimalPlaces(PixelsToX(1, 10) - xstart);

		// Draw x values
		for (int i = 0; i <= 10; i++) {
			int x = MARGIN_X_LEFT + width * i / 10;
			String value = String.format("%." + decimalPlaces + "f", PixelsToX(i, 10));
			int xOffset = axisFontMetrics.stringWidth(value) / 2;
			// Pointer
			g2d.drawLine(x, margin_y_top + height + MARGIN_X_AXIS - 5, x, margin_y_top + height + MARGIN_X_AXIS + 5);
			g2d.drawString(value, x - xOffset, margin_y_top + height + MARGIN_X_AXIS + MARGIN_LABELS_X);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		try {
			super.paintComponent(g);

			available_height = (int) (getHeight() - MARGIN_Y_TOP - MARGIN_Y_BOTTOM);
			available_width = (int) (getWidth() - MARGIN_X_LEFT - MARGIN_X_RIGHT);

			Graphics2D g2d = (Graphics2D) g;

			// Display only a small subset of the data in that range
			int indexStart = 1 + blines.search(new Comparable<PanelVlines.Vline>() {

				@Override
				public int compareTo(Vline o) {
					return Double.compare(xstart, o.x);
				}
			});

			double xstop = PixelsToX(1, 1);

			int indexStop = blines.search(new Comparable<PanelVlines.Vline>() {

				@Override
				public int compareTo(Vline o) {
					return Double.compare(xstop, o.x);
				}
			});
			int step = 1;
			if (indexStop - indexStart > PAINT_MAX_LINES_ON_SCREEN) {
				step = (indexStop - indexStart) / PAINT_MAX_LINES_ON_SCREEN;
			}

			visible.clear();
			for (int i = indexStart; i < indexStop; i += step) {
				Vline line = lines.get(i);
				line.paintComponent(g2d);
				visible.add(line);
			}
			paintAxis(g2d, available_width, available_height, MARGIN_Y_TOP);
			paintLabels(g2d, available_width, available_height, MARGIN_Y_TOP);

			// Display hovering info
			if (showInfo && closest != null) {
				int infoX = closest.x1;
				int infoY = closest.y1;
				if (infoY > available_height)
					infoY = available_height / 2;
				g.drawString(String.format("%f,%f", closest.x, closest.y), MARGIN_X_LEFT + infoX + 3, MARGIN_Y_TOP + available_height - infoY);
			}

		} catch (Exception ex) {
			System.err.println("Panel Spectrum Paint: " + ex.getMessage());
		}
	}

	/**
	 * Represents a pair of x and y coordinates
	 * 
	 * @author Constambeys
	 */
	protected class Vline implements Comparable<Vline> {

		/**
		 * Real X value
		 */
		protected double x;
		/**
		 * Real Y value
		 */
		protected double y;
		/**
		 * X value base on screen size without margins
		 */
		protected int x1;
		/**
		 * y value base on screen size without margins
		 */
		protected int y1;
		protected final Color color;

		Vline(double x, double y) {
			this.x = x;
			this.y = y;
			this.color = Color.BLUE;
		}

		Vline(double x, double y, Color color) {
			this.x = x;
			this.y = y;
			this.color = color;
		}

		public void paintComponent(Graphics2D g) {

			if (x >= xstart) {
				x1 = xToPixels(x);
				if (x1 > available_width) {
					// Outside Screen do not paint
					return;
				}

				y1 = yToPixels(y);

				// Line
				g.setColor(Color.BLACK);
				g.setStroke(new BasicStroke(1));
				g.drawLine(MARGIN_X_LEFT + x1, MARGIN_Y_TOP + available_height, MARGIN_X_LEFT + x1, MARGIN_Y_TOP + available_height - y1);

				// Blue Arrow
				g.setColor(color);
				int[] xPoints = new int[3];
				int[] yPoints = new int[3];
				xPoints[0] = MARGIN_X_LEFT + x1;
				yPoints[0] = MARGIN_Y_TOP + available_height - y1;
				xPoints[1] = MARGIN_X_LEFT + x1 - MARGIN_ARROW_LEFT;
				yPoints[1] = MARGIN_Y_TOP + available_height - y1 + MARGIN_ARROW_DOWN;
				xPoints[2] = MARGIN_X_LEFT + x1 + MARGIN_ARROW_LEFT;
				yPoints[2] = MARGIN_Y_TOP + available_height - y1 + MARGIN_ARROW_DOWN;
				g.drawPolygon(xPoints, yPoints, 3);
				g.fillPolygon(xPoints, yPoints, 3);
			}
		}

		@Override
		public int compareTo(Vline o) {
			return Double.compare(x, o.x);
		}
	}

	/**
	 * Use private class for hiding method calls
	 * 
	 * @author Constambeys
	 *
	 */
	protected class MyMouseListener implements MouseListener, MouseMotionListener {

		private int xmouse;

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			xmouse = e.getX();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			showInfo = true;
		}

		@Override
		public void mouseExited(MouseEvent e) {
			showInfo = false;
			PanelVlines.this.repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {

			int pixels = (e.getX() - xmouse);
			// Move x to new location on screen
			// int stop = (int) (zoom * (x - xx) / (xmax - xmin) * available_width);
			// int start = (int) (zoom * (x - xx') / (xmax - xmin) * available_width);
			// => stop - start = pixels = ...
			// xx' = xx - offset

			double offset = (pixels * (xmax - xmin) / (zoom * available_width));
			if (offset != 0) {
				xmouse = e.getX();
				xstart -= offset;

				PanelVlines.this.repaint();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {

			if (lines.size() == 0) {
				return;
			}
			int x = e.getX() - MARGIN_X_LEFT;
			int index = bvisible.search(new Comparable<PanelVlines.Vline>() {

				@Override
				public int compareTo(Vline o) {
					return Double.compare(x, o.x1);
				}
			});
			int indexStart = Math.max(0, index - SEARCH);
			int indexStop = Math.min(visible.size() - 1, index + SEARCH);

			closest = null;

			for (int i = indexStart + 1; i < indexStop; i++) {
				Vline line = visible.get(i);
				if (closest == null || line.y > closest.y) {
					closest = line;
				}
			}

			PanelVlines.this.repaint();

		}
	}

	/**
	 * Converts x value to the display coordinate
	 * <p>
	 * Does not include margin constants
	 * 
	 * @param x
	 * @return coordinate
	 */
	protected int xToPixels(double x) {
		return (int) (zoom * (x - xstart) / (xmax - xmin) * available_width);
	}

	/**
	 * Converts y value to the display coordinate
	 * <p>
	 * Does not include margin constants
	 *
	 * @param y
	 * @return coordinate
	 */
	protected int yToPixels(double y) {
		return (int) (zoom * (y) / (ymax - ymin) * available_height);
	}

	/**
	 * Converts display coordinate to x value
	 * <p>
	 * Does not include margin constants
	 *
	 * @param x
	 * @return coordinate
	 */
	protected double PixelsToX(int x) {
		return xstart + x * (xmax - xmin) / zoom / available_width;
	}

	/**
	 * Converts display ratio to x value
	 * <p>
	 * Does not include margin constants
	 *
	 * @param x
	 * @return coordinate
	 */
	protected double PixelsToX(int i, int max) {
		// int x1 = (int) (zoom * (x - xx) / (xmax - xmin) * available_width);
		// => find x
		return xstart + ((xmax - xmin) / zoom * i / max);
	}

	/**
	 * Converts display coordinate to y value
	 * <p>
	 * Does not include margin constants
	 *
	 * @param y
	 * @return coordinate
	 */
	protected double PixelsToY(int y) {
		return y * (ymax - ymin) / zoom / available_height;
	}

	/**
	 * Converts display ratio to y value
	 * <p>
	 * Does not include margin constants
	 *
	 * @param y
	 * @return coordinate
	 */
	protected double PixelsToY(int i, int max) {
		// int y1 = (int) (zoom * (y) / (ymax - ymin) * available_height);
		// => find y
		return ((ymax - ymin) / zoom * i / max);
	}

}
