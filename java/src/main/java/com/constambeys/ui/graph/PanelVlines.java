package com.constambeys.ui.graph;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
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

	protected final int PAINT_MAX_LINES_ON_SCREEN0 = 2000;
	protected final int PAINT_MAX_LINES_ON_SCREEN1 = 2000;

	protected final int SEARCH_PIXELS = 20;
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
	protected double xmin = 0;
	protected double xmax = 100;
	protected double ymin = 0;
	protected double ymax = 100;

	/**
	 * Zoom = 1 No Zoom
	 * <p>
	 * Zoom = 2 2x
	 * 
	 * Zoom = 0.5 0.5x
	 */
	protected double zoom = 1;
	/**
	 * Real x value which the plot starts
	 */
	protected double xstart = 0;

	/**
	 * All Sorted
	 */
	protected List<Vline> lines0;
	protected BinarySearch<Vline> blines0;
	/**
	 * Visible Sorted
	 */
	protected List<Vline> visible0;
	protected BinarySearch<Vline> bvisible0;

	/**
	 * All Sorted
	 */
	protected List<Vline> lines1;
	protected BinarySearch<Vline> blines1;
	/**
	 * Visible Sorted
	 */
	protected List<Vline> visible1;
	protected BinarySearch<Vline> bvisible1;

	protected Comparable<Vline> findxstart;

	// Calculated during paint
	protected int available_height;
	protected int available_width;

	// Display hovering info
	protected boolean showInfo;
	protected Vline closest;

	// Zoom to a selected region
	protected boolean showSelectedRegion = false;
	protected Point selectionStart;
	protected Point selectionStop;

	/**
	 * Initialises a new {@code PanelVlines }
	 */
	public PanelVlines() {
		super();
		lines0 = new ArrayList<>();
		blines0 = new BinarySearch<>(lines0);
		visible0 = new ArrayList<>();
		bvisible0 = new BinarySearch<>(visible0);
		lines1 = new ArrayList<>();
		blines1 = new BinarySearch<>(lines1);
		visible1 = new ArrayList<>();
		bvisible1 = new BinarySearch<>(visible1);
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
	public synchronized void add(double x, double y) {
		Vline line = new Vline(x, y);
		lines0.add(line);
	}

	/**
	 * After adding all values call addCommit
	 *
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @param isBlue
	 *            true is coloured blue otherwise red
	 */
	public synchronized void add(double x, double y, boolean isBlue) {
		if (isBlue) {
			Vline line = new Vline(x, y, Color.BLUE);
			lines0.add(line);
		} else {
			Vline line = new Vline(x, y, Color.RED);
			lines1.add(line);
		}
	}

	/**
	 * Clear all values
	 */
	public synchronized void clear() {
		lines0.clear();
		lines1.clear();
	}

	/**
	 * Call to update internal state
	 * 
	 * @throws Exception
	 */
	public synchronized void addCommit() throws TooFewDataException {
		Collections.sort(lines0);
		Collections.sort(lines1);

		calculateStatistics();
		xstart = xmin;
		zoom = 0.9;
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
	 * Updates input data statistical values min, max
	 * 
	 * @throws Exception
	 */
	protected void calculateStatistics() throws TooFewDataException {

		if (lines0.size() == 0) {
			throw new TooFewDataException();
		}

		xmin = lines0.get(0).x;
		xmax = lines0.get(lines0.size() - 1).x;

		if (xmax == xmin) {
			xmax = 100;
			xmin = 0;
			throw new TooFewDataException();
		}

		ymin = 0;
		ymax = lines0.get(0).y;

		for (Vline line : lines0) {
			double y = line.y;

			if (ymin > y)
				ymin = y;
			if (ymax < y)
				ymax = y;
		}

		if (ymax == ymin) {
			ymax = 100;
			ymin = 0;
			throw new TooFewDataException();
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
		decimalPlaces = Utilities.calculateFirstDesimalPlace(PixelsToY(1, 10));

		// Draw y values
		for (int i = 0; i <= 10; i++) {
			int y = margin_y_top + height * i / 10;
			String value = String.format("%." + decimalPlaces + "e", PixelsToY(10 - i, 10));
			// Pointer
			g2d.drawLine(MARGIN_X_LEFT - 5, y, MARGIN_X_LEFT + 5, y);
			g2d.drawString(value, MARGIN_X_LEFT - MARGIN_LABELS_Y, y + yOffset);
		}

		decimalPlaces = Utilities.calculateFirstDesimalPlace(PixelsToX(1, 10) - xstart);

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

	private void paint(Graphics2D g2d, List<Vline> list, BinarySearch<Vline> blist, List<Vline> visible, int limit) {
		visible.clear();

		// Display only a small subset of the data in that range
		int indexStart = 1 + blist.search(new Comparable<PanelVlines.Vline>() {

			@Override
			public int compareTo(Vline o) {
				return Double.compare(xstart, o.x);
			}
		});

		double xstop = PixelsToX(1, 1);

		int indexStop = 1 + blist.search(new Comparable<PanelVlines.Vline>() {

			@Override
			public int compareTo(Vline o) {
				return Double.compare(xstop, o.x);
			}
		});
		int step = 1;
		if (indexStop - indexStart > limit) {
			step = (indexStop - indexStart) / limit;
		}

		for (int i = indexStart; i < indexStop; i += step) {
			Vline line = list.get(i);
			line.paintComponent(g2d);
			visible.add(line);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		try {
			super.paintComponent(g);

			available_height = (int) (getHeight() - MARGIN_Y_TOP - MARGIN_Y_BOTTOM);
			available_width = (int) (getWidth() - MARGIN_X_LEFT - MARGIN_X_RIGHT);

			Graphics2D g2d = (Graphics2D) g;

			paint(g2d, lines1, blines1, visible1, PAINT_MAX_LINES_ON_SCREEN1);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			paint(g2d, lines0, blines0, visible0, PAINT_MAX_LINES_ON_SCREEN0);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

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

			if (showSelectedRegion) {
				g.setColor(Color.YELLOW);
				g.drawRect(Math.min(selectionStart.x, selectionStop.x), Math.min(selectionStart.y, selectionStop.y), Math.abs(selectionStop.x - selectionStart.x), Math.abs(selectionStop.y - selectionStart.y));
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

		// Move graph left/ right
		private int xmouse;

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isControlDown()) {
				selectionStart = e.getPoint();
				selectionStop = e.getPoint();
				showSelectedRegion = true;
			} else {
				xmouse = e.getX();
				showSelectedRegion = false;
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (showSelectedRegion) {
				int selectionStartX = Math.min(selectionStart.x, selectionStop.x);
				int selectionStopX = Math.max(selectionStart.x, selectionStop.x);

				double x1 = PixelsToX(selectionStartX - MARGIN_X_LEFT);
				double x2 = PixelsToX(selectionStopX - MARGIN_X_LEFT);

				xstart = Math.min(x1, x2);
				// zoom * (xstop-xstart)/(xmax-xmin) * available width = available width
				zoom = (xmax - xmin) / Math.abs(x1 - x2);
				showSelectedRegion = false;
				PanelVlines.this.repaint();
			}

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
			if (showSelectedRegion) {
				selectionStop = e.getPoint();
				PanelVlines.this.repaint();
			} else {
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
		}

		private void find(int x, List<Vline> visible, BinarySearch<Vline> bvisible) {
			int index = bvisible.search(new Comparable<PanelVlines.Vline>() {

				@Override
				public int compareTo(Vline o) {
					return Double.compare(x, o.x1);
				}
			});

			for (int i = index; i >= 0; i--) {
				Vline line = visible.get(i);
				if (x - line.x1 > SEARCH_PIXELS)
					break;

				if (closest == null || line.y > closest.y) {
					closest = line;
				}

			}

			for (int i = index + 1; i < visible.size(); i++) {
				Vline line = visible.get(i);
				if (line.x1 - x > SEARCH_PIXELS)
					break;

				if (closest == null || line.y > closest.y) {
					closest = line;
				}

			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {

			int x = e.getX() - MARGIN_X_LEFT;
			closest = null;
			find(x, visible0, bvisible0);
			find(x, visible1, bvisible1);

			PanelVlines.this.repaint();

		}

	}

	/**
	 * Converts x value to the display coordinate
	 * <p>
	 * Does not include margin constants
	 * 
	 */
	protected int xToPixels(double x) {
		return (int) (zoom * (x - xstart) / (xmax - xmin) * available_width);
	}

	/**
	 * Converts y value to the display coordinate
	 * <p>
	 * Does not include margin constants
	 *
	 */
	protected int yToPixels(double y) {
		return (int) (zoom * (y - ymin) / (ymax - ymin) * available_height);
	}

	/**
	 * Converts display coordinate to x value
	 * <p>
	 * Does not include margin constants
	 *
	 */
	protected double PixelsToX(int x) {
		return xstart + x * (xmax - xmin) / zoom / available_width;
	}

	/**
	 * Converts display ratio to x value
	 * <p>
	 * Does not include margin constants
	 *
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
	 */
	protected double PixelsToY(int y) {
		return ymin + y * (ymax - ymin) / zoom / available_height;
	}

	/**
	 * Converts display ratio to y value
	 * <p>
	 * Does not include margin constants
	 *
	 */
	protected double PixelsToY(int i, int max) {
		// int y1 = (int) (zoom * (y) / (ymax - ymin) * available_height);
		// => find y
		return ymin + ((ymax - ymin) / zoom * i / max);
	}

	@SuppressWarnings("serial")
	public static class TooFewDataException extends Exception {

		public TooFewDataException() {
		}
	}
}
