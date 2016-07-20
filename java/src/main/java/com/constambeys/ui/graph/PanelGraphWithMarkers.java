package com.constambeys.ui.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Extends {@link PanelGraph} and displays markers
 * 
 * @author Constambeys
 * 
 */
public class PanelGraphWithMarkers extends PanelGraph {

	protected List<Marker> markers = new ArrayList<Marker>(1);

	/**
	 * * Initialises a {@code PanelGraphWithMarkers }
	 */
	public PanelGraphWithMarkers() {
		super();
	}

	/**
	 * Adds marker m
	 * 
	 * @param m
	 */
	public void addMarker(Marker m) {
		markers.add(m);
	}

	/**
	 * Removes marker m
	 * 
	 * @param m
	 */
	public void removeMarker(Marker m) {
		markers.remove(m);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		for (Marker m : markers) {
			if (m.visible) {

				int x = (int) (m.x_ratio * widthDisplayed + MARGIN_X_LEFT);
				int y = (int) (m.y_ratio * heightDisplayed + margin_y_top);

				// Axis
				g2d.setColor(Color.WHITE);
				g2d.setStroke(new BasicStroke(3));
				// X Axis left -> right
				g2d.drawLine(x - 10, y, x + 10, y);
				// Y Axis top -> bottom
				g2d.drawLine(x, y - 10, x, y + 10);
			}
		}
	}

	public static class Marker extends PanelGraph.Point {

		public Marker() {
			super(0, 0);
		}

		public Marker(PanelGraph.Point p) {
			super(p.x_ratio, p.y_ratio);
		}

		boolean visible = true;

		public void setVisible(boolean visible) {
			this.visible = visible;
		}
	}
}
