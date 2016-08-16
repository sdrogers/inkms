package com.constambeys.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.constambeys.ui.graph.PanelVlines;

public class FrameSpectrumPlot extends JFrame {

	public PanelVlines panelVlines;
	
	public FrameSpectrumPlot()
	{
		setTitle("Spectrum");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(800, 400);
		
		JPanel background = new JPanel();
		setContentPane(background);

		background.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Border Layout North, South,East,West
		background.setLayout(new BorderLayout(0, 0));

		panelVlines = new PanelVlines();
		background.add(panelVlines);
	}
}
