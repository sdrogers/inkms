package com.constambeys.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import com.constambeys.layout.SpringUtilities;

public class DialogOptimalMz extends JDialog {

	/**
	 * Create the dialog.
	 * 
	 * @param result
	 */
	ActionListener ok;

	JPanel panelSouth = new JPanel();
	JButton buttonOK = new JButton("OK");

	JPanel panelCenter;
	Map<String, JTextField> map = new HashMap<String, JTextField>();

	public DialogOptimalMz(FrameMain mainFrame, String title, boolean modal) {
		super(mainFrame, title, modal);
		setupUI();
	}

	private void setupUI() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 200, 100);
		setMinimumSize(getSize());
		getContentPane().setLayout(new BorderLayout());

		// Create and populate the panel.
		panelCenter = new JPanel(new SpringLayout());
		getContentPane().add(panelCenter, BorderLayout.CENTER);

		panelSouth.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelSouth.setBorder(new EmptyBorder(5, 5, 5, 5));

		JButton buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		panelSouth.add(buttonCancel);

		buttonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					setVisible(false);
					dispose();
					if (ok != null)
						ok.actionPerformed(event);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		panelSouth.add(buttonOK);

		getRootPane().setDefaultButton(buttonOK);
		getContentPane().add(panelSouth, BorderLayout.SOUTH);
	}

	public void addOkListener(ActionListener l) {
		ok = l;
	}

	public void addTextBox(String name, String title) {
		JTextField jtextField = new JTextField(10);
		JLabel l = new JLabel(title, JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jtextField);
		panelCenter.add(jtextField);

		map.put(name, jtextField);

		// Lay out the panel.
		SpringUtilities.makeCompactGrid(panelCenter, map.size(), 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad
	}

	public String getText(String name) {
		return map.get(name).getText();
	}
}
