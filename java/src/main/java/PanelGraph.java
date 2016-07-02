import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class PanelGraph extends JPanel {
	// CONSTANTS
	protected final int MARGIN_X_LEFT = 20;
	protected final int MARGIN_X_RIGHT = 5;
	protected final int MARGIN_Y_BOTTOM = 5;
	protected final float MARGIN_Y_TOP_RATIO = 1.8f;

	protected final int MARGIN_X_AXIS = 3;
	protected final int MARGIN_Y_AXIS = 3;

	protected final int MARGIN_LABELS = 20;

	public static class Point {
		double x;
		double y;

		Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

	public interface ImageListener {
		public void mouseClicked(Point p);
	}

	protected List<ImageListener> mouseListeners = new ArrayList<ImageListener>(1);

	public void addListener(ImageListener list) {
		mouseListeners.add(list);
	}

	public void removeListener(ImageListener list) {
		mouseListeners.remove(list);
	}

	protected void triggerMouseListeners(Point p) {
		for (ImageListener l : mouseListeners) {
			l.mouseClicked(p);
		}
	}

	// Image Dimensions
	protected int widthInMM;
	protected int heightInMM;
	protected BufferedImage bufferImage;
	protected int[] colormap;

	// Mouse Listener transform x,y relative to image
	protected int margin_y_top;
	protected int widthDisplayed;
	protected int heightDisplayed;

	// Graphics
	protected String title = "";
	protected Font titleFont;
	protected Font axisFont;

	PanelGraph() {
		super();
		hot(1024);
		titleFont = new Font("SansSerif", Font.BOLD, 15);
		axisFont = new Font("SansSerif", Font.BOLD, 10);

		addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				PanelGraph.this.mouseClicked(e);
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
		});
	}

	public void setTitle(String title) {
		this.title = title;
		this.repaint();
	}

	public void draw(BufferedImage img, int widthInMM, int heightInMM) {
		this.widthInMM = widthInMM;
		this.heightInMM = heightInMM;

		this.bufferImage = img;
		this.repaint();
	}

	public BufferedImage getImage(double[][] intensity) {

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

		int resolution = colormap.length - 1;
		BufferedImage bufferImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// Color Image
				// int value = (int) (intensity[j][i] / max * 255);
				// Color color = new Color(value, value, value);
				// bufferImage.setRGB(i, j, color.getRGB());
				int indx = (int) (intensity[j][i] / max * resolution);
				bufferImage.setRGB(i, j, colormap[indx]);
			}
		}

		return bufferImage;
	}

	private int paintTitleHeight(Graphics g) {
		FontMetrics titleFontMetrics = g.getFontMetrics(titleFont);
		int titleHeight = (titleFontMetrics.getAscent());
		return (int) (titleHeight * 1.2f);
	}

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

	private int[] paintImageHeight(Graphics g, int available_width, int available_height, int margin_y_top) {
		double ratio = widthInMM / heightInMM;
		double ratio_ = available_width / available_height;

		int width;
		int height;
		if (ratio > ratio_) {
			width = available_width;
			height = (int) (available_width / ratio);
		} else {
			width = (int) (available_height * ratio);
			height = available_height;
		}
		return new int[] { width, height };
	}

	private int[] paintImage(Graphics g, int available_width, int available_height, int margin_y_top) {
		double ratio = widthInMM / heightInMM;
		double ratio_ = available_width / available_height;

		int width;
		int height;
		if (ratio > ratio_) {
			width = available_width;
			height = (int) (available_width / ratio);
			this.margin_y_top = margin_y_top;
			this.widthDisplayed = width;
			this.heightDisplayed = height;
			// Reduce Height
			g.drawImage(bufferImage, MARGIN_X_LEFT, margin_y_top, width, height, null);

		} else {
			width = (int) (available_height * ratio);
			height = available_height;
			this.margin_y_top = margin_y_top;
			this.widthDisplayed = width;
			this.heightDisplayed = height;
			// Reduce Width
			g.drawImage(bufferImage, MARGIN_X_LEFT, margin_y_top, width, height, null);
		}
		return new int[] { width, height };
	}

	private void paintAxis(Graphics2D g2d, int width, int height, int margin_y_top) {
		// Axis
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(3));
		// X Axis left -> right
		g2d.drawLine(MARGIN_X_LEFT - MARGIN_Y_AXIS, margin_y_top + height + MARGIN_X_AXIS,
				MARGIN_X_LEFT - MARGIN_Y_AXIS + width, margin_y_top + height + MARGIN_X_AXIS);
		// Y Axis top -> bottom
		g2d.drawLine(MARGIN_X_LEFT - MARGIN_Y_AXIS, margin_y_top, MARGIN_X_LEFT - MARGIN_Y_AXIS, margin_y_top + height);
	}

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

	private void hot(int resolution) {
		// http://www.codeproject.com/Articles/18150/Create-Custom-Color-Maps-in-C
		int[] hot = new int[resolution];
		float r[] = new float[resolution];
		float g[] = new float[resolution];
		float b[] = new float[resolution];

		int k = (int) ((float) resolution / 8 * 3);

		for (int i = 0; i < resolution; i++) {
			if (i < k)
				r[i] = (float) ((i + 1.0) / k);
			else
				r[i] = 1.0f;

			if (i < k)
				g[i] = 0.0f;
			else if (i >= k && i < 2 * k)
				g[i] = (float) ((i + 1.0 - k) / k);
			else
				g[i] = 1.0f;

			if (i < 2 * k)
				b[i] = 0f;
			else
				b[i] = (float) ((i + 1.0 - 2 * k) / (resolution - 2 * k));
		}

		for (int i = 0; i < resolution; i++) {
			hot[i] = new Color(r[i], g[i], b[i], 1.0f).getRGB();
		}
		this.colormap = hot;
	}

	private void mouseClicked(MouseEvent e) {

		int x = e.getX() - MARGIN_X_LEFT;
		int y = e.getY() - margin_y_top;
		if (x >= 0 && x <= widthDisplayed && y >= 0 && y <= heightDisplayed) {
			Point p = new Point((double) x / widthDisplayed, (double) y / heightDisplayed);
			triggerMouseListeners(p);
		}
	}
}