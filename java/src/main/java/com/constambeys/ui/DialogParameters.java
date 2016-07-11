package com.constambeys.ui;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.constambeys.layout.SpringUtilities;

import javax.swing.JFileChooser;
import java.io.File;

public class DialogParameters extends JDialog {

	/**
	 * Create the dialog.
	 * 
	 * @param result
	 */
	ActionListener ok;

	JPanel panelSouth = new JPanel();
	JButton buttonOK = new JButton("OK");

	JTextField jFilePath = new JTextField(10);
	JTextField jtextLines = new JTextField(10);
	JTextField jtextWidth = new JTextField(10);
	JTextField jtextHeight = new JTextField(10);
	JTextField jtextDownMotion = new JTextField(10);
	String[] types = new String[] { "Normal", "Positive", "Negative" };
	JComboBox<String> jcomboType = new JComboBox<String>(types);

	private void setupUI() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 200, 185);
		setMinimumSize(getSize());
		getContentPane().setLayout(new BorderLayout());

		// Create and populate the panel.
		JPanel panelCenter = new JPanel(new SpringLayout());
		JLabel l;

		l = new JLabel("File", JLabel.TRAILING);
		panelCenter.add(l);
		jFilePath.setEditable(false);
		jFilePath.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("mzXML files", "mzXML");
				fileChooser.setFileFilter(filter);
				int result = fileChooser.showOpenDialog(DialogParameters.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					jFilePath.setText(selectedFile.getAbsolutePath());
				}
			}
		});
		l.setLabelFor(jFilePath);
		panelCenter.add(jFilePath);

		l = new JLabel("Lines", JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jtextLines);
		panelCenter.add(jtextLines);

		l = new JLabel("Width", JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jtextWidth);
		panelCenter.add(jtextWidth);

		l = new JLabel("Height", JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jtextHeight);
		panelCenter.add(jtextHeight);

		l = new JLabel("Down", JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jtextDownMotion);
		panelCenter.add(jtextDownMotion);

		l = new JLabel("Type", JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jcomboType);
		panelCenter.add(jcomboType);

		// Lay out the panel.
		SpringUtilities.makeCompactGrid(panelCenter, 6, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

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

	public DialogParameters(FrameMain mainFrame, String title, boolean modal) {
		super(mainFrame, title, modal);
		setupUI();
	}

	public void addOkListener(ActionListener l) {
		ok = l;
	}
}
