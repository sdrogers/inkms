package com.constambeys.ui.graph;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 * Extends {@link PanelGraph} and display an overlay image
 * 
 * @author Constmabeys
 *
 */
public class PanelGraphDraw extends PanelGraph {

	protected BufferedImage imgOverlay;
	private Graphics2D g2dOverlay;
	private int overlayWidth;
	private int overlayHeight;

	/**
	 * Mouse Pointer
	 */
	private boolean mouseVisible;
	private int mouseX;
	private int mouseY;

	int rectSize = 10;
	boolean drawing = true;

	public static int LETTER_COLOR = new Color(255, 255, 255, 128).getRGB();

	/**
	 * Initialises a {@code PanelGraphDraw }
	 */
	public PanelGraphDraw() {
		super();
		super.addClickListener(new MyClickListener());
		super.addDraggedListener(new MyDraggedListener());
		super.addMouseMotionListener(new MyMouseMotionListener());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.constambeys.ui.graph.PanelGraph#draw(java.awt.image.BufferedImage, int, int)
	 */
	@Override
	public void draw(BufferedImage img, int widthInMM, int heightInMM) {
		super.draw(img, widthInMM, heightInMM);
		overlayWidth = img.getWidth();
		overlayHeight = img.getHeight();
		if (g2dOverlay != null)
			g2dOverlay.dispose();
		imgOverlay = new BufferedImage(overlayWidth, overlayHeight, BufferedImage.TYPE_INT_ARGB);
		g2dOverlay = imgOverlay.createGraphics();
	}

	/**
	 * @return overlay image
	 */
	public BufferedImage getTemplateOverlay() {
		return imgOverlay;
	}

	/**
	 * Changes the drawing area size
	 * 
	 * @param size
	 */
	public void setDrawingDimension(int size) {
		this.rectSize = size;
	}

	/**
	 * @return true == drawing , false == erasing
	 */
	public boolean getIsDrawingState() {
		return this.drawing;
	}

	/**
	 * Sets drawing or erasing state
	 * 
	 * @param drawing
	 */
	public void setIsDrawingState(boolean drawing) {
		this.drawing = drawing;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (imgOverlay == null)
			return;

		g.drawImage(imgOverlay, MARGIN_X_LEFT, margin_y_top, widthDisplayed, heightDisplayed, null);

		if (mouseVisible) {
			// Draw mouse pointer
			g.setColor(Color.WHITE);
			g.fillRect(mouseX - rectSize / 2, mouseY - rectSize / 2, rectSize, rectSize);
		}

	}

	/**
	 * Processes mouse movements
	 * 
	 * @param p
	 */
	private void imageClickedOrDragged(Point p) {

		// WARNGING: If you change the letter colour then change LETTER_COLOR variable
		if (drawing) {
			// reset composite
			g2dOverlay.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.5f));
			g2dOverlay.setColor(Color.WHITE);
		} else {
			// http://stackoverflow.com/questions/5672697/java-filling-a-bufferedimage-with-transparent-pixels
			// clear
			g2dOverlay.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));

		}

		int width = (int) ((double) rectSize / widthDisplayed * overlayWidth) + 1;
		int height = (int) ((double) rectSize / heightDisplayed * overlayHeight) + 1;

		g2dOverlay.fillRect((int) (overlayWidth * p.x_ratio), (int) (overlayHeight * p.y_ratio), width, height);

		this.repaint();

	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		if (g2dOverlay != null)
			g2dOverlay.dispose();
	}

	/**
	 * Receives events from base class
	 * 
	 * @author Constambeys
	 *
	 */
	protected class MyClickListener implements PanelGraph.ImageClicked {
		@Override
		public void imageClicked(Point p) {
			imageClickedOrDragged(p);
		}
	}

	/**
	 * Receives events from base class
	 * 
	 * @author Constambeys
	 *
	 */
	protected class MyDraggedListener implements PanelGraph.ImageDragged {

		@Override
		public void imageDragged(Point p) {
			imageClickedOrDragged(p);
		}

	}

	/**
	 * Updates mouse coordinates
	 * 
	 * @author Constambeys
	 *
	 */
	protected class MyMouseMotionListener implements MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent e) {
			mouseVisible = false;
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseVisible = true;
			mouseX = e.getX();
			mouseY = e.getY();
			PanelGraphDraw.this.repaint();
		}
	}

}
