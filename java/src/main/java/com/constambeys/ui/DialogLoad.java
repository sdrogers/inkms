package com.constambeys.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.constambeys.filtering.IFiltering;
import com.constambeys.filtering.NoFiltering;
import com.constambeys.filtering.PolarityFiltering;
import com.constambeys.layout.SpringUtilities;
import com.constambeys.patterns.ILoadPattern;
import com.constambeys.patterns.Pattern1;
import com.constambeys.patterns.Pattern2;
import com.constambeys.readers.IReader;
import com.constambeys.readers.IReader.ScanType;
import com.constambeys.readers.ImzMLProxy;
import com.constambeys.readers.MSIImage;
import com.constambeys.readers.MzJavaProxy;
import com.constambeys.readers.MzMLProxy;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;

/**
 * The {@code DialogLoad} class allows the user to load file parameters
 * 
 * @author Constambeys
 *
 */
public class DialogLoad extends JDialog {

	public interface OKListener {
		public void actionPerformed(ActionEvent e, MSIImage msiimage);
	}

	/**
	 * Specifies the spectrum selection algorithm
	 */
	private static enum Filtering {
		ALL("ALL"), POSITIVE("POSITIVE"), NEGATIVE("NEGATIVE");

		String name;

		private Filtering(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
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

	private JButton buttonOK = new JButton("OK");

	private JTextField jFilePath;
	private JTextField jtextLines;
	private JTextField jtextWidth;
	private JTextField jtextHeight;
	private JTextField jtextDownMotion;
	private JComboBox<Filtering> jcomboType;

	private IReader reader;
	private Thread tread;
	private Pattern pattern;

	private JRadioButton jradMeandering;
	private JRadioButton jradNormal;
	private JLabel jlabelImage;

	/**
	 * Initialises a new user interface dialog
	 * 
	 * @param owner
	 *            the Frame from which the dialog is displayed
	 * @param title
	 *            the {@code String} to display in the dialog's title bar
	 * @param modal
	 *            specifies whether dialog blocks user input to other top-level windows when shown
	 * @throws IOException
	 */
	public DialogLoad(FrameMain owner, boolean canClose) throws IOException {
		super(owner, "Set Parameters", true);
		setupUI(canClose);
	}

	/**
	 * Initialises the UI elements
	 * 
	 * @throws IOException
	 * 
	 */
	private void setupUI(boolean canClose) throws IOException {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setBounds(100, 100, 200, 185);

		setMinimumSize(getSize());
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(createPanelCenter(), BorderLayout.CENTER);

		JPanel panelSouth = new JPanel();
		panelSouth.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelSouth.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panelSouth, BorderLayout.SOUTH);

		Box boxEast = new Box(BoxLayout.Y_AXIS);
		getContentPane().add(boxEast, BorderLayout.EAST);

		if (canClose) {
			JButton buttonCancel = new JButton("Cancel");
			buttonCancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					dispose();
				}
			});
			panelSouth.add(buttonCancel);
		} else {
			// Close button is clicked
			addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent windowEvent) {
					System.exit(0);
				}
			});
		}

		panelSouth.add(buttonOK);

		jradMeandering = new JRadioButton("Meandering", true);
		jradNormal = new JRadioButton("Normal");
		ButtonGroup group1 = new ButtonGroup();
		group1.add(jradMeandering);
		group1.add(jradNormal);
		boxEast.add(jradMeandering);
		boxEast.add(jradNormal);
		boxEast.add(Box.createRigidArea(new Dimension(0, 10)));

		jlabelImage = new JLabel();
		boxEast.add(jlabelImage);
		setPattern(Pattern.Pattern1);

		addListeners();
		getRootPane().setDefaultButton(buttonOK);
	}

	private JPanel createPanelCenter() {
		// Create and populate the panel.
		JPanel panelCenter = new JPanel(new SpringLayout());

		JLabel l;

		l = new JLabel("File", JLabel.TRAILING);
		panelCenter.add(l);
		jFilePath = new JTextField(10);
		jFilePath.setEditable(false);
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

		l = new JLabel("Drop (mm)", JLabel.TRAILING);
		panelCenter.add(l);
		jtextDownMotion = new JTextField(10);
		l.setLabelFor(jtextDownMotion);
		panelCenter.add(jtextDownMotion);

		l = new JLabel("Filtering:", JLabel.TRAILING);
		panelCenter.add(l);
		DefaultComboBoxModel<Filtering> model = new DefaultComboBoxModel<Filtering>(Filtering.values());
		jcomboType = new JComboBox<Filtering>();
		jcomboType.setModel(model);
		l.setLabelFor(jcomboType);
		panelCenter.add(jcomboType);

		// Lay out the panel.
		SpringUtilities.makeCompactGrid(panelCenter, 6, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad
		return panelCenter;
	}

	private void addListeners() {

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
				try {
					if (tread != null && tread.isAlive()) {
						JOptionPane.showMessageDialog(null, "Please wait until file loading is completed", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					Settings settings = new Settings();
					JFileChooser fileChooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("mzML | mzXML | imzML files", "mzML", "mzXML", "imzML");
					fileChooser.setFileFilter(filter);
					if (settings.get("load_dir") != null) {
						fileChooser.setCurrentDirectory(new File(settings.get("load_dir")));
					}
					int result = fileChooser.showOpenDialog(DialogLoad.this);
					if (result == JFileChooser.APPROVE_OPTION) {

						jFilePath.setText("Loading ...");

						File selectedFile = fileChooser.getSelectedFile();

						tread = new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									loadfile(selectedFile);
								} catch (Exception ex) {
									JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
								}
							}
						});
						tread.start();

						settings.put("load_dir", fileChooser.getCurrentDirectory().getAbsolutePath());
						try {
							settings.save();
						} catch (IOException ex) {
							System.err.println("Error saving settings file");
						}
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		jradMeandering.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					setPattern(Pattern.Pattern1);
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					setPattern(Pattern.Pattern2);
				}
			}
		});

		buttonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					if (tread != null && tread.isAlive()) {
						JOptionPane.showMessageDialog(null, "Please wait until file loading is completed", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					} else if (reader == null) {
						JOptionPane.showMessageDialog(null, "Click file to select an input file", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					Filtering type = (Filtering) jcomboType.getSelectedItem();
					IFiltering readerWithFiltering;
					if (type == Filtering.ALL) {
						readerWithFiltering = new NoFiltering(reader);
					} else if (type == Filtering.POSITIVE) {
						readerWithFiltering = new PolarityFiltering(reader, ScanType.POSITIVE);
					} else if (type == Filtering.NEGATIVE) {
						readerWithFiltering = new PolarityFiltering(reader, ScanType.NEGATIVE);
					} else {
						throw new Exception(String.format("Filtering type %s not supported", type.toString()));
					}

					if (readerWithFiltering.getSpectraCount() == 0) {
						throw new Exception(String.format("File does not contain %s scans", type.toString()));
					}

					ILoadPattern p;
					if (pattern == Pattern.Pattern1) {
						Pattern1.Param param = new Pattern1.Param();
						param.lines = Integer.parseInt(jtextLines.getText());
						param.widthInMM = Integer.parseInt(jtextWidth.getText());
						param.heightInMM = Integer.parseInt(jtextHeight.getText());
						param.downMotionInMM = Float.parseFloat(jtextDownMotion.getText());
						p = new Pattern1(readerWithFiltering, param);
					} else {
						Pattern2.Param param = new Pattern2.Param();
						param.lines = Integer.parseInt(jtextLines.getText());
						param.widthInMM = Integer.parseInt(jtextWidth.getText());
						param.heightInMM = Integer.parseInt(jtextHeight.getText());
						p = new Pattern2(readerWithFiltering, param);
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
	}

	/**
	 * Load the selected file and update UI
	 * 
	 * @param selectedFile
	 * @throws Exception
	 */
	private void loadfile(File selectedFile) throws Exception {

		long startTime = System.nanoTime();
		String filepath = selectedFile.getAbsolutePath();

		if (filepath.toLowerCase().endsWith("mzxml")) {
			reader = new MzJavaProxy(new File(filepath));
		} else if (filepath.toLowerCase().endsWith("imzml")) {
			reader = new ImzMLProxy(new File(filepath));
		} else if (filepath.toLowerCase().endsWith("mzml")) {
			reader = new MzMLProxy(new File(filepath));
		} else {
			throw new Exception("File format not supprted");
		}

		long estimatedTime = System.nanoTime() - startTime;
		System.out.println(String.format("%.3fs", estimatedTime / 1000000000.0));

		updateUI(new ITask() {

			@Override
			public void run() throws Exception {
				jFilePath.setText(selectedFile.getAbsolutePath());

				if (reader instanceof ImzMLProxy) {
					jtextLines.setText(Integer.toString(((ImzMLProxy) reader).getLines()));
					jtextLines.setEditable(false);
				} else {
					jtextLines.setEditable(true);
				}
			}
		});

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

	private void setPattern(Pattern p) {
		if (p == Pattern.Pattern1) {
			pattern = Pattern.Pattern1;
			jtextDownMotion.setText("");
			jtextDownMotion.setEditable(true);
			try {
				jlabelImage.setIcon(Startup.loadIcon("pattern1.png", 200, 200));
			} catch (IOException e1) {
				System.err.println("Cannot load pattern1.png");
			}
		} else if (p == Pattern.Pattern2) {
			pattern = Pattern.Pattern2;
			jtextDownMotion.setText("0");
			jtextDownMotion.setEditable(false);
			try {
				jlabelImage.setIcon(Startup.loadIcon("pattern2.png", 200, 200));
			} catch (IOException e1) {
				System.err.println("Cannot load pattern2.png");
			}
		}
	}

	/**
	 * Task that update user interface Catches exceptions and shows an error message
	 * 
	 * @param task
	 */
	private void updateUI(ITask task) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					task.run();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Task Failed", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	/**
	 * Background tasks interface
	 */
	interface ITask {
		public void run() throws Exception;
	}

}
