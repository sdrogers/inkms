import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.JPanel;

public class Graph extends JPanel {
	private final int MARGIN_X_LEFT = 20;
	private final int MARGIN_X_RIGHT = 5;
	private final int MARGIN_Y_BOTTOM = 5;
	private final float MARGIN_Y_TOP_RATIO = 1.8f;

	private final int MARGIN_X_AXIS = 3;
	private final int MARGIN_Y_AXIS = 3;

	private final int MARGIN_LABELS = 20;

	private int widthInMM;
	private int heightInMM;
	private BufferedImage bufferImage;
	private ImageObserver observer;
	private int[] colormap;

	private String title;
	private Font titleFont;
	private Font axisFont;

	Graph() {
		super();
		hot(1024);
		titleFont = new Font("SansSerif", Font.BOLD, 15);
		axisFont = new Font("SansSerif", Font.BOLD, 10);

		observer = new ImageObserver() {
			@Override
			public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
				return false;
			}
		};
	}

	public void setTitle(String title) {
		this.title = title;
		this.repaint();
	}

	public void draw(double[][] intensity, int widthInMM, int heightInMM) {
		this.widthInMM = widthInMM;
		this.heightInMM = heightInMM;

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
		bufferImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
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

		this.repaint();

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
			// Reduce Height
			g.drawImage(bufferImage, MARGIN_X_LEFT, margin_y_top, width, height, observer);

		} else {
			width = (int) (available_height * ratio);
			height = available_height;
			// Reduce Width
			g.drawImage(bufferImage, MARGIN_X_LEFT, margin_y_top, width, height, observer);
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
			hot[i] = new Color(r[i], g[i], b[i]).getRGB();
		}
		this.colormap = hot;
	}
}