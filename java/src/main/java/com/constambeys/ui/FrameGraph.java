package com.constambeys.ui;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.constambeys.load.MSIImage;
import com.constambeys.ui.graph.PanelGraph;

public class FrameGraph extends JFrame {

	private MSIImage msiimage;
	private PanelGraph panelGraph;

	public FrameGraph(MSIImage msiimage) {
		super();
		setupUI(msiimage);
	}

	/**
	 * Initialises the UI elements
	 * 
	 * @param msiimage
	 *            the loaded mass spectrometry image
	 * @throws ParseException
	 * @throws IOException
	 */
	public void setupUI(MSIImage msiimage) {
		this.msiimage = msiimage;
		setTitle("MSI");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 400);

		JPanel background = new JPanel();
		setContentPane(background);

		background.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Border Layout North, South,East,West
		background.setLayout(new BorderLayout(0, 0));

		panelGraph = new PanelGraph();
		background.add(BorderLayout.CENTER, panelGraph);

	}

	public void setGraph(String title, BufferedImage imgGenerated) {
		try {
			setTitle(title);
			panelGraph.setTitle(title);
			panelGraph.draw(imgGenerated, msiimage.getWidthMM(), msiimage.getHeightMM());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
