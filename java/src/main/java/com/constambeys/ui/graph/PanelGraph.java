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
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * JPanel that draws a Buffered Image
 * 
 * @author Constambeys
 *
 */
public class PanelGraph extends JPanel implements MouseListener, MouseMotionListener {
	// CONSTANTS
	protected final int MARGIN_X_LEFT = 30;
	protected final int MARGIN_X_RIGHT = 5;
	protected final int MARGIN_Y_BOTTOM = 30;
	protected final float MARGIN_Y_TOP_RATIO = 1.8f;

	protected final int MARGIN_X_AXIS = 3;
	protected final int MARGIN_Y_AXIS = 3;

	protected final int MARGIN_LABELS = 25;

	public static class Point {

		public double x_ratio;
		public double y_ratio;

		Point(double x_ratio, double y_ratio) {
			this.x_ratio = x_ratio;
			this.y_ratio = y_ratio;
		}
	}

	public interface ImageClicked {
		public void imageClicked(Point p);
	}

	protected List<ImageClicked> clickListeners = new ArrayList<ImageClicked>(1);

	/**
	 * Adds the specified mouse listener to receive mouse events from this component.
	 * 
	 * @param listener
	 */
	public void addClickListener(ImageClicked listener) {
		clickListeners.add(listener);
	}

	/**
	 * Removes the specified mouse listener so that it no longer receives mouse events from this component
	 * 
	 * @param listener
	 */
	public void removeClickListener(ImageClicked listener) {
		clickListeners.remove(listener);
	}

	/**
	 * Trigger the subscribed mouse listeners
	 * 
	 * @param p
	 */
	protected void triggerClickListeners(Point p) {
		for (ImageClicked l : clickListeners) {
			l.imageClicked(p);
		}
	}

	public interface ImageDragged {
		public void imageDragged(Point p);
	}

	protected List<ImageDragged> draggedListeners = new ArrayList<ImageDragged>(1);

	/**
	 * Adds the specified mouse motion listener to receive mouse motion events from this component
	 * 
	 * @param listener
	 */
	public void addDraggedListener(ImageDragged listener) {
		draggedListeners.add(listener);
	}

	/**
	 * Removes the specified mouse motion listener so that it no longer receives mouse motion events from this component.
	 * 
	 * @param listener
	 */
	public void removeDraggedListener(ImageDragged listener) {
		draggedListeners.remove(listener);
	}

	/**
	 * Trigger the subscribed mouse motion listeners
	 * 
	 * @param p
	 */
	protected void triggerDraggedListeners(Point p) {
		for (ImageDragged l : draggedListeners) {
			l.imageDragged(p);
		}
	}

	// Image Dimensions
	protected int widthInMM;
	protected int heightInMM;
	protected BufferedImage bufferImage;
	protected IColormap colormap;

	// Mouse Listener transform x,y relative to image
	protected int margin_y_top;
	protected int widthDisplayed;
	protected int heightDisplayed;

	// Graphics
	protected String title = "";
	protected Font titleFont;
	protected Font axisFont;

	/**
	 * Initialises a new {@code PanelGraph }
	 */
	public PanelGraph() {
		super();
		colormap = new Hot();
		titleFont = new Font("SansSerif", Font.BOLD, 15);
		axisFont = new Font("SansSerif", Font.BOLD, 10);

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/**
	 * Sets Graph Title
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
		this.repaint();
	}

	/**
	 * Draws the image based on the given dimensions
	 * 
	 * @param img
	 *            the image
	 * @param widthInMM
	 *            the image width in millimetres
	 * @param heightInMM
	 *            the image height in millimetres
	 */
	public void draw(BufferedImage img, int widthInMM, int heightInMM) {
		this.widthInMM = widthInMM;
		this.heightInMM = heightInMM;

		this.bufferImage = img;
		this.repaint();
	}

	/**
	 * Converts a 2d array to buffered image. Uses hot colour map
	 * 
	 * @param intensity
	 * @return
	 */
	public BufferedImage calculateImage(double[][] intensity) {

		int height = intensity.length;
		int width = intensity[0].length;

		double max = intensity[0][0];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (max < intensity[i][j]) {
					max = intensity[i][j];
				}
			}
		}

		int resolution = colormap.getMaxIndex();
		BufferedImage bufferImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// Color Image
				// int value = (int) (intensity[j][i] / max * 255);
				// Color color = new Color(value, value, value);
				// bufferImage.setRGB(i, j, color.getRGB());
				int indx = (int) (intensity[j][i] / max * resolution);
				bufferImage.setRGB(i, j, colormap.get(indx));
			}
		}

		return bufferImage;
	}

	/**
	 * @return the displayed image
	 */
	public BufferedImage getImage() {
		return bufferImage;
	}

	/**
	 * Determine Title Dimensions
	 * 
	 * @param g
	 *            Graphics Object
	 * @return
	 */
	private int paintTitleHeight(Graphics g) {
		FontMetrics titleFontMetrics = g.getFontMetrics(titleFont);
		int titleHeight = (titleFontMetrics.getAscent());
		return (int) (titleHeight * 1.2f);
	}

	/**
	 * Draw Title
	 * 
	 * @param g
	 *            Graphics Object
	 * @param margin_y_top
	 *            the top margin
	 * @return
	 */
	private int paintTitle(Graphics g, int margin_y_top) {
		// Title
		FontMetrics titleFontMetrics = g.getFontMetrics(titleFont);

		int titleWidth = titleFontMetrics.stringWidth(title);
		int titleHeight = (titleFontMetrics.getAscent());
		int titleOffsetX = (getWidth() - titleWidth) / 2;
		g.setFont(titleFont);
		g.drawString(title, titleOffsetX, (int) (margin_y_top + titleHeight * 1.2f));
		return titleHeight;
	}

	/**
	 * Determine Image Dimensions
	 * 
	 * @param g
	 *            Graphics Object
	 * @param available_width
	 *            the available display width
	 * @param available_height
	 *            the available display height
	 * @param margin_y_top
	 *            the top margin
	 * @return image width and height
	 */
	private int[] paintImageHeight(Graphics g, int available_width, int available_height, int margin_y_top) {
		double ratio = widthInMM / heightInMM;
		double ratio_ = available_width / available_height;

		int width;
		int height;
		if (ratio > ratio_) {
			width = available_width;
			// Reduce Height
			height = (int) (available_width / ratio);
		} else {
			// Reduce Width
			width = (int) (available_height * ratio);
			height = available_height;
		}
		return new int[] { width, height };
	}

	/**
	 * Draw Image
	 * 
	 * @param g
	 *            Graphics Object
	 * @param available_width
	 *            the available display width
	 * @param available_height
	 *            the available display height
	 * @param margin_y_top
	 *            the top margin
	 * @return image width and height
	 */
	private int[] paintImage(Graphics g, int available_width, int available_height, int margin_y_top) {
		double ratio = widthInMM / heightInMM;
		double ratio_ = available_width / available_height;

		int width;
		int height;
		if (ratio > ratio_) {
			width = available_width;
			// Reduce Height
			height = (int) (available_width / ratio);
		} else {
			// Reduce Width
			width = (int) (available_height * ratio);
			height = available_height;
		}

		this.margin_y_top = margin_y_top;
		this.widthDisplayed = width;
		this.heightDisplayed = height;
		g.drawImage(bufferImage, MARGIN_X_LEFT, margin_y_top, width, height, null);
		return new int[] { width, height };
	}

	/**
	 * Paint image x and y axis
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
	private void paintAxis(Graphics2D g2d, int width, int height, int margin_y_top) {
		// Axis
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(3));
		// X Axis left -> right
		g2d.drawLine(MARGIN_X_LEFT - MARGIN_Y_AXIS, margin_y_top + height + MARGIN_X_AXIS, MARGIN_X_LEFT - MARGIN_Y_AXIS + width, margin_y_top + height + MARGIN_X_AXIS);
		// Y Axis top -> bottom
		g2d.drawLine(MARGIN_X_LEFT - MARGIN_Y_AXIS, margin_y_top, MARGIN_X_LEFT - MARGIN_Y_AXIS, margin_y_top + height);
	}

	/**
	 * Paint image labels
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
	private void paintLabels(Graphics2D g2d, int width, int height, int margin_y_top) {
		FontMetrics axisFontMetrics = g2d.getFontMetrics(axisFont);
		g2d.setFont(axisFont);
		g2d.setStroke(new BasicStroke(1));
		int yOffset = axisFontMetrics.getAscent() / 2;

		// Draw y values
		for (int i = 0; i <= 10; i++) {
			int y = margin_y_top + height * i / 10;
			String value = String.format("%d", heightInMM * (10 - i) / 10);
			// Pointer
			g2d.drawLine(MARGIN_X_LEFT - 5, y, MARGIN_X_LEFT + 5, y);
			g2d.drawString(value, MARGIN_X_LEFT - MARGIN_LABELS, y + yOffset);
		}
		// Draw x values
		for (int i = 0; i <= 10; i++) {
			int x = MARGIN_X_LEFT + width * i / 10;
			String value = String.format("%d", widthInMM * i / 10);
			int xOffset = axisFontMetrics.stringWidth(value) / 2;
			// Pointer
			g2d.drawLine(x, margin_y_top + height + MARGIN_X_AXIS - 5, x, margin_y_top + height + MARGIN_X_AXIS + 5);
			g2d.drawString(value, x - xOffset, margin_y_top + height + MARGIN_X_AXIS + MARGIN_LABELS);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		try {
			super.paintComponent(g);

			if (bufferImage == null) {
				return;
			}
			int titleHeight = paintTitleHeight(g);
			// Image
			int margin_y_image = (int) (MARGIN_Y_TOP_RATIO * titleHeight);
			int available_height = (int) (getHeight() - margin_y_image - MARGIN_Y_BOTTOM);
			int available_width = (int) (getWidth() - MARGIN_X_LEFT - MARGIN_X_RIGHT);
			int estimated_image_height = paintImageHeight(g, available_width, available_height, margin_y_image)[1];

			int margin_y_center = (available_height - estimated_image_height) / 2;
			paintTitle(g, margin_y_center);
			int[] dim = paintImage(g, available_width, available_height, margin_y_image + margin_y_center);
			int width = dim[0];
			int height = dim[1];

			paintAxis((Graphics2D) g, width, height, margin_y_image + margin_y_center);
			paintLabels((Graphics2D) g, width, height, margin_y_image + margin_y_center);

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {

		int x = e.getX() - MARGIN_X_LEFT;
		int y = e.getY() - margin_y_top;
		if (x >= 0 && x <= widthDisplayed && y >= 0 && y <= heightDisplayed) {
			Point p = new Point((double) x / widthDisplayed, (double) y / heightDisplayed);
			triggerClickListeners(p);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {

		int x = e.getX() - MARGIN_X_LEFT;
		int y = e.getY() - margin_y_top;
		if (x >= 0 && x <= widthDisplayed && y >= 0 && y <= heightDisplayed) {
			Point p = new Point((double) x / widthDisplayed, (double) y / heightDisplayed);
			triggerDraggedListeners(p);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}