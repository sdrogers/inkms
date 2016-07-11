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
import com.constambeys.load.LoadPattern;
import com.constambeys.load.MSIImage;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;

public class DialogLoad extends JDialog implements MouseListener {

	/**
	 * Create the dialog.
	 * 
	 * @param result
	 */
	ActionListener ok;

	JPanel panelSouth = new JPanel();
	JButton buttonOK = new JButton("OK");

	JTextField jFilePath;
	JTextField jtextLines;
	JTextField jtextWidth;
	JTextField jtextHeight;
	JTextField jtextDownMotion;
	JComboBox<LoadPattern.Type> jcomboType;

	public DialogLoad(FrameMain mainFrame, String title, boolean modal) {
		super(mainFrame, title, modal);
		setupUI();
	}

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
		jFilePath = new JTextField(10);
		jFilePath.setEditable(false);
		jFilePath.addMouseListener(this);
		l.setLabelFor(jFilePath);
		panelCenter.add(jFilePath);

		l = new JLabel("Lines", JLabel.TRAILING);
		panelCenter.add(l);
		jtextLines = new JTextField(10);
		l.setLabelFor(jtextLines);
		panelCenter.add(jtextLines);

		l = new JLabel("Width (mm)", JLabel.TRAILING);
		panelCenter.add(l);
		jtextWidth = new JTextField(10);
		l.setLabelFor(jtextWidth);
		panelCenter.add(jtextWidth);

		l = new JLabel("Height (mm)", JLabel.TRAILING);
		panelCenter.add(l);
		jtextHeight = new JTextField(10);
		l.setLabelFor(jtextHeight);
		panelCenter.add(jtextHeight);

		l = new JLabel("Down (mm)", JLabel.TRAILING);
		panelCenter.add(l);
		jtextDownMotion = new JTextField(10);
		l.setLabelFor(jtextDownMotion);
		panelCenter.add(jtextDownMotion);

		l = new JLabel("Indexes:", JLabel.TRAILING);
		panelCenter.add(l);
		jcomboType = new JComboBox<LoadPattern.Type>(LoadPattern.Type.values());
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

	public void addOkListener(ActionListener l) {
		ok = l;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Settings settings = Settings.getAppSettings();

		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("mzML files", "mzML");
		fileChooser.setFileFilter(filter);
		if (settings.get("load_dir") != null) {
			fileChooser.setCurrentDirectory(new File(settings.get("load_dir")));
		}
		int result = fileChooser.showOpenDialog(DialogLoad.this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			jFilePath.setText(selectedFile.getAbsolutePath());
			settings.put("load_dir", fileChooser.getCurrentDirectory().getAbsolutePath());
			try {
				settings.save();
			} catch (IOException e1) {
				System.err.println("Error saving");
			}
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
}
