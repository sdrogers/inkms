package com.constambeys.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultComboBoxModel;
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
import com.constambeys.load.ILoadPattern;
import com.constambeys.load.MSIImage;
import com.constambeys.load.Pattern1;
import com.constambeys.load.Pattern2;

import uk.ac.ebi.pride.tools.jmzreader.JMzReader;
import uk.ac.ebi.pride.tools.mzml_wrapper.MzMlWrapper;
import uk.ac.ebi.pride.tools.mzxml_parser.MzXMLFile;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;

/**
 * The {@code DialogLoad} class allows the user to load file parameters
 * 
 * @author Constambeys
 *
 */
public class DialogLoad extends JDialog implements MouseListener {

	public interface OKListener {
		public void actionPerformed(ActionEvent e, MSIImage msiimage);
	}

	private enum Pattern {
		Pattern1, Pattern2;
	}

	/**
	 * Create the dialog.
	 * 
	 * @param result
	 */
	private OKListener ok;

	private JPanel panelSouth = new JPanel();
	private JButton buttonOK = new JButton("OK");

	private JTextField jFilePath;
	private JTextField jtextLines;
	private JTextField jtextWidth;
	private JTextField jtextHeight;
	private JTextField jtextDownMotion;
	private Pattern pattern;
	private JComboBox jcomboType;

	/**
	 * Initialises a new user interface dialog
	 * 
	 * @param owner
	 *            the Frame from which the dialog is displayed
	 * @param title
	 *            the {@code String} to display in the dialog's title bar
	 * @param modal
	 *            specifies whether dialog blocks user input to other top-level windows when shown
	 */
	public DialogLoad(FrameMain owner, String title, boolean modal) {
		super(owner, title, modal);
		setupUI();
	}

	/**
	 * Initialises the UI elements
	 * 
	 */
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
		jcomboType = new JComboBox<Object>();
		DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(Pattern2.Type.values());
		jcomboType.setModel(model);
		pattern = Pattern.Pattern2;
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
					long startTime = System.nanoTime();
					String filepath = jFilePath.getText();
					JMzReader reader;
					if (filepath.toLowerCase().endsWith("mzxml")) {
						reader = new MzXMLFile(new File(filepath));
					} else {
						reader = new MzMlWrapper(new File(filepath));
					}
					long estimatedTime = System.nanoTime() - startTime;
					System.out.println(String.format("%.3fs", estimatedTime / 1000000000.0));

					ILoadPattern p;
					if (pattern == Pattern.Pattern1) {
						Pattern1.Param param = new Pattern1.Param();
						param.lines = Integer.parseInt(jtextLines.getText());
						param.widthInMM = Integer.parseInt(jtextWidth.getText());
						param.heightInMM = Integer.parseInt(jtextHeight.getText());
						param.downMotionInMM = Float.parseFloat(jtextDownMotion.getText());
						Pattern1.Type type = (Pattern1.Type) jcomboType.getSelectedItem();
						p = new Pattern1(reader, param, type);
					} else {
						Pattern2.Param param = new Pattern2.Param();
						param.lines = Integer.parseInt(jtextLines.getText());
						param.widthInMM = Integer.parseInt(jtextWidth.getText());
						param.heightInMM = Integer.parseInt(jtextHeight.getText());
						Pattern2.Type type = (Pattern2.Type) jcomboType.getSelectedItem();
						p = new Pattern2(reader, param, type);
					}
					MSIImage msiimage = new MSIImage(reader, p);

					setVisible(false);
					dispose();
					if (ok != null)
						ok.actionPerformed(event, msiimage);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		panelSouth.add(buttonOK);

		getRootPane().setDefaultButton(buttonOK);
		getContentPane().add(panelSouth, BorderLayout.SOUTH);
	}

	/**
	 * Set OK button listener
	 * 
	 * @param l
	 *            callback listener
	 */
	public void addOkListener(OKListener l) {
		ok = l;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Settings settings = new Settings();

		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("mzML | mzXML files", "mzML", "mzXML");
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
