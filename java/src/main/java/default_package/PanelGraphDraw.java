package default_package;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public class PanelGraphDraw extends PanelGraph implements PanelGraph.ImageClicked, PanelGraph.ImageDragged, MouseMotionListener {

	protected BufferedImage imgOverlay;
	private Graphics2D g2dOverlay;
	private int overlayWidth;
	private int overlayHeight;

	private int mouseX;
	private int mouseY;

	int rectSize = 10;
	boolean drawing = true;

	public PanelGraphDraw() {
		super();
		super.addClickListener(this);
		super.addDraggedListener(this);

	}

	public void draw(BufferedImage img, int widthInMM, int heightInMM) {
		super.draw(img, widthInMM, heightInMM);
		overlayWidth = img.getWidth();
		overlayHeight = img.getHeight();
		if (g2dOverlay != null)
			g2dOverlay.dispose();
		imgOverlay = new BufferedImage(overlayWidth, overlayHeight, BufferedImage.TYPE_INT_ARGB);
		g2dOverlay = imgOverlay.createGraphics();
	}

	public BufferedImage getTemplateOverlay() {
		return imgOverlay;
	}

	public void setDrawingDimension(int size) {
		this.rectSize = size;
	}

	public void setState(boolean drawing) {
		this.drawing = drawing;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (imgOverlay == null)
			return;

		g.drawImage(imgOverlay, MARGIN_X_LEFT, margin_y_top, widthDisplayed, heightDisplayed, null);

		g.setColor(Color.WHITE);
		g.fillRect(mouseX - rectSize / 2, mouseY - rectSize / 2, rectSize, rectSize);

	}

	private void imageClickedDragged(Point p) {

		if (drawing) {
			// reset composite
			g2dOverlay.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			g2dOverlay.setColor(Color.WHITE);
		} else {
			// http://stackoverflow.com/questions/5672697/java-filling-a-bufferedimage-with-transparent-pixels
			// clear
			g2dOverlay.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));

		}

		int width = (int) ((double) rectSize / widthDisplayed * overlayWidth);
		int height = (int) ((double) rectSize / heightDisplayed * overlayHeight) + 1;

		g2dOverlay.fillRect((int) (overlayWidth * p.x), (int) (overlayHeight * p.y), width, height);

		this.repaint();

	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		if (g2dOverlay != null)
			g2dOverlay.dispose();
	}

	@Override
	public void imageClicked(Point p) {
		imageClickedDragged(p);
	}

	@Override
	public void imageDragged(Point p) {
		imageClickedDragged(p);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		mouseX = e.getX();
		mouseY = e.getY();
		this.repaint();
	}

}
