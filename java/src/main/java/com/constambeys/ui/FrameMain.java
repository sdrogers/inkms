package com.constambeys.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import com.constambeys.load.Pattern1;
import com.constambeys.load.MSIImage;
import com.constambeys.python.BinEventlyDistributed;
import com.constambeys.python.BinsPartsPerMillion;
import com.constambeys.python.IBinResolution;
import com.constambeys.python.ICheckLetter;
import com.constambeys.python.IProgress;
import com.constambeys.python.IsLetterV1;
import com.constambeys.python.OptimalMz;
import com.constambeys.python.OptimalMzV1;
import com.constambeys.python.OptimalMzV2;
import com.constambeys.ui.graph.PanelGraph;

import uk.ac.ebi.pride.tools.jmzreader.JMzReader;
import uk.ac.ebi.pride.tools.mzml_wrapper.MzMlWrapper;
import uk.ac.ebi.pride.tools.mzxml_parser.MzXMLFile;

/**
 * The {@code FrameMain} class is the main application frame
 * 
 * @author Constambeys
 *
 */
@SuppressWarnings("serial")
public class FrameMain extends JFrame {

	private static int THREADS = 1;
	private static int GRAPHS = 5;

	private MSIImage msiimage;
	private JProgressBar progressBar;
	private JTabbedPane tabbedPane;

	private JCheckBox jcheckSettings;
	private ICheckLetter isLetterTemplate;
	private ICheckLetter isLetterDraw;

	private JRadioButton jradRect;
	private JRadioButton jradTempl;
	private JRadioButton jradDraw;
	private JRadioButton jradEvently;
	private JRadioButton jradPPM;
	private JFormattedTextField jnumGraphs;
	private JFormattedTextField jV2PixelsWeight;

	private ExecutorService executorService;

	/**
	 * Initialises a new user interface dialog
	 */
	public FrameMain() {
		try {
			setTitle("MSI");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 800, 400);

			JPanel background = new JPanel();
			setContentPane(background);

			background.setBorder(new EmptyBorder(5, 5, 5, 5));
			// Border Layout North, South,East,West
			background.setLayout(new BorderLayout(0, 0));

			progressBar = new JProgressBar(0, 100);
			progressBar.setValue(0);
			progressBar.setStringPainted(true);
			progressBar.setVisible(false);
			background.add(BorderLayout.NORTH, progressBar);

			// Create a tabbed pane
			tabbedPane = new JTabbedPane();
			background.add(BorderLayout.CENTER, tabbedPane);

			Box boxSouth = new Box(BoxLayout.X_AXIS);
			background.add(BorderLayout.SOUTH, boxSouth);

			Box boxEast = new Box(BoxLayout.Y_AXIS);
			background.add(BorderLayout.EAST, boxEast);

			JButton btnLoad = new JButton("Load");
			boxSouth.add(btnLoad);
			boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

			JButton btnGraph = new JButton("Graph");
			boxSouth.add(btnGraph);
			boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

			JButton btnOptimalMz = new JButton("OptimalMz");
			boxSouth.add(btnOptimalMz);
			boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

			JButton btnOptimalMz2 = new JButton("OptimalMz2");
			boxSouth.add(btnOptimalMz2);
			boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

			JButton btnTemplate = new JButton("Template");
			boxSouth.add(btnTemplate);
			boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

			JButton btnDraw = new JButton("Draw");
			boxSouth.add(btnDraw);
			boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

			boxSouth.add(Box.createHorizontalGlue());
			jcheckSettings = new JCheckBox("Settings");
			jcheckSettings.setSelected(true);
			boxSouth.add(jcheckSettings);
			boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

			JLabel l;

			jradRect = new JRadioButton("Rectangle", true);
			jradTempl = new JRadioButton("Template");
			jradDraw = new JRadioButton("Draw");
			ButtonGroup group1 = new ButtonGroup();
			group1.add(jradRect);
			group1.add(jradTempl);
			group1.add(jradDraw);
			boxEast.add(jradRect);
			boxEast.add(jradTempl);
			boxEast.add(jradDraw);
			boxEast.add(Box.createRigidArea(new Dimension(0, 10)));

			jradEvently = new JRadioButton("Evently", true);
			jradPPM = new JRadioButton("PPM");
			ButtonGroup group2 = new ButtonGroup();
			group2.add(jradEvently);
			group2.add(jradPPM);
			boxEast.add(jradEvently);
			boxEast.add(jradPPM);
			boxEast.add(Box.createRigidArea(new Dimension(0, 10)));

			l = new JLabel("Graphs: ", JLabel.TRAILING);
			NumberFormat format1 = NumberFormat.getInstance();
			NumberFormatter formatter1 = new NumberFormatter(format1);
			formatter1.setValueClass(Integer.class);
			formatter1.setMinimum(0);
			formatter1.setAllowsInvalid(true);
			// If you want the value to be committed on each keystroke instead of
			// focus lost
			formatter1.setCommitsOnValidEdit(true);
			jnumGraphs = new JFormattedTextField(formatter1);
			jnumGraphs.setValue(GRAPHS);
			Dimension maxsize1 = jnumGraphs.getMaximumSize();
			Dimension prefsize1 = jnumGraphs.getPreferredSize();
			maxsize1.height = prefsize1.height;
			jnumGraphs.setMaximumSize(maxsize1);
			l.setLabelFor(jnumGraphs);
			boxEast.add(l);
			boxEast.add(jnumGraphs);

			l = new JLabel("OptMz2 Weight: ", JLabel.TRAILING);
			NumberFormat format2 = NumberFormat.getInstance();
			NumberFormatter formatter2 = new NumberFormatter(format2);
			formatter2.setValueClass(Integer.class);
			formatter2.setMinimum(0);
			formatter2.setAllowsInvalid(true);
			// If you want the value to be committed on each keystroke instead of
			// focus lost
			formatter2.setCommitsOnValidEdit(true);
			jV2PixelsWeight = new JFormattedTextField(formatter2);
			jV2PixelsWeight.setValue(2);
			Dimension maxsize2 = jnumGraphs.getMaximumSize();
			Dimension prefsize2 = jnumGraphs.getPreferredSize();
			maxsize2.height = prefsize2.height;
			jV2PixelsWeight.setMaximumSize(maxsize2);
			l.setLabelFor(jV2PixelsWeight);
			boxEast.add(l);
			boxEast.add(jV2PixelsWeight);

			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					super.windowClosing(e);
				}

			});

			btnLoad.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					btnLoad();
				}
			});

			btnGraph.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					btnGraph();
				}
			});

			btnOptimalMz.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					btnOptimalMz(1);
				}
			});

			btnOptimalMz2.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					btnOptimalMz(2);
				}
			});

			btnTemplate.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					btnTemplate();
				}
			});

			btnDraw.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					btnDraw();
				}
			});

			jcheckSettings.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					boxEast.setVisible(jcheckSettings.isSelected());
				}
			});

			executorService = Executors.newFixedThreadPool(THREADS);
			createMenuBar();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Creates {@code JMenuBar}
	 * 
	 * @throws IOException
	 */
	private void createMenuBar() throws IOException {

		JMenuBar menubar = new JMenuBar();

		JMenu fileMenu = new JMenu("Tabs");

		JMenuItem jMenuSave = new JMenuItem("Save");
		int height = (int) jMenuSave.getPreferredSize().getHeight();
		jMenuSave.setIcon(Startup.loadIcon("save128.png", height, height));

		JMenuItem jMenuClose = new JMenuItem("Close");
		jMenuClose.setIcon(Startup.loadIcon("closeBlue128.png", height, height));
		JMenuItem jMenuCloseAll = new JMenuItem("Close All");
		jMenuCloseAll.setIcon(Startup.loadIcon("closeRed128.png", height, height));

		jMenuSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					int selected = tabbedPane.getSelectedIndex();
					if (selected >= 0) {
						Component component = tabbedPane.getComponentAt(selected);
						if (component instanceof PanelGraph) {
							PanelGraph pg = (PanelGraph) component;
							BufferedImage imgGenerated = pg.getImage();
							if (imgGenerated != null) {
								JFileChooser fileChooser = new JFileChooser();
								if (fileChooser.showSaveDialog(FrameMain.this) == JFileChooser.APPROVE_OPTION) {
									File file = fileChooser.getSelectedFile();
									if (!file.getName().endsWith(".png"))
										file = new File(file.getAbsolutePath() + ".png");
									// save to file
									ImageIO.write(imgGenerated, "PNG", file);
								}
							}
						}
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		jMenuClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				int selected = tabbedPane.getSelectedIndex();
				if (selected >= 0) {
					tabbedPane.remove(selected);
				}
			}
		});

		jMenuCloseAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				tabbedPane.removeAll();
			}
		});

		fileMenu.add(jMenuSave);
		fileMenu.add(jMenuClose);
		fileMenu.add(jMenuCloseAll);

		menubar.add(fileMenu);

		setJMenuBar(menubar);
	}

	/**
	 * 
	 * ActionListener of the Load button.
	 *
	 */
	private void btnLoad() {
		if (Startup.DEBUG) {
			try {
				msiimage = Startup.loadMZML();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
			return;
		}

		DialogLoad dialog = new DialogLoad(FrameMain.this, "Set Parameters", true);
		dialog.pack();
		dialog.addOkListener(new DialogLoad.OKListener() {

			@Override
			public void actionPerformed(ActionEvent e, MSIImage msiimage) {
				try {
					FrameMain.this.msiimage = msiimage;
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		dialog.setVisible(true);
	}

	/**
	 * ActionListener of the Graph button.
	 * 
	 */
	private void btnGraph() {
		try {
			if (msiimage == null) {
				JOptionPane.showMessageDialog(null, "First load the MZXML data", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			DialogGraph dialog = new DialogGraph(FrameMain.this, "Set Parameters", true);
			dialog.setLocationRelativeTo(this);
			dialog.pack();
			dialog.addOkListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						if (dialog.massrange.size() == 0) {
							return;
						}

						StringBuilder sb = new StringBuilder();
						double massrange[] = new double[dialog.massrange.size() * 2];

						for (int i = 0; i < dialog.massrange.size(); i++) {
							massrange[2 * i] = dialog.massrange.get(i).lowerMass;
							massrange[2 * i + 1] = dialog.massrange.get(i).higherMass;
							if (i != 0) {
								sb.append(", ");
							}
							sb.append(String.format("%sm/z - %sm/z", dialog.massrange.get(i).strLowerMass, dialog.massrange.get(i).strHigherMass));
						}

						addGraphTab(sb.toString(), massrange);

					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			dialog.setVisible(true);

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Executes Optimal Mass algorithm
	 * 
	 * @param version
	 *            1 or 2
	 */
	private void btnOptimalMz(int version) {
		try {
			if (msiimage == null) {
				JOptionPane.showMessageDialog(null, "First load the MZXML data", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (jradTempl.isSelected() && isLetterTemplate == null) {
				throw new Exception("Template Settings not loaded");
			}

			if (jradDraw.isSelected() && isLetterDraw == null) {
				throw new Exception("Template Settings not loaded");
			}

			DialogOptimalMz dialog = new DialogOptimalMz(FrameMain.this, "Set Parameters", true);
			if (jradRect.isSelected()) {
				dialog.addTextBox("xstart", "x start (mm)");
				dialog.addTextBox("xstop", "x stop (mm)");
				dialog.addTextBox("ystart", "y start (mm)");
				dialog.addTextBox("ystop", "y stop (mm)");
			}
			dialog.addTextBox("lmass", "Lower Mass");
			dialog.addTextBox("hmass", "Higher Mass");

			if (jradEvently.isSelected()) {
				dialog.addTextBox("resolution", "Resolution"); // 200
			} else {
				dialog.addTextBox("ppm", "Parts Per Million"); // 1000
			}

			dialog.pack();
			dialog.addOkListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						ICheckLetter isLetter;

						if (jradRect.isSelected()) {
							double x_start = Double.parseDouble(dialog.getText("xstart")); // 30
							double x_stop = Double.parseDouble(dialog.getText("xstop"));// 40
							double y_start = Double.parseDouble(dialog.getText("ystart"));// 0
							double y_stop = Double.parseDouble(dialog.getText("ystop"));// 10

							isLetter = new IsLetterV1(msiimage, x_start, x_stop, y_start, y_stop);
						} else if (jradTempl.isSelected()) {
							isLetter = isLetterTemplate;
						} else {
							isLetter = isLetterDraw;
						}

						double lowerMass = Double.parseDouble(dialog.getText("lmass")); // 300
						double higherMass = Double.parseDouble(dialog.getText("hmass"));// 500

						IBinResolution bins;
						if (jradEvently.isSelected()) {
							int resolution = Integer.parseInt(dialog.getText("resolution"));
							bins = new BinEventlyDistributed(lowerMass, higherMass, resolution);
						} else {
							int ppm = Integer.parseInt(dialog.getText("ppm"));
							bins = new BinsPartsPerMillion(lowerMass, higherMass, ppm);
						}

						OptimalMz optimalMz;
						if (version == 1) {
							optimalMz = new OptimalMzV1(msiimage, isLetter, bins);
						} else {
							optimalMz = new OptimalMzV2(msiimage, isLetter, bins, (int) jV2PixelsWeight.getValue());
						}
						optimalMz.setProgressListener(progressTracker);

						int n = (int) jnumGraphs.getValue();
						ITask task = new ITask() {

							@Override
							public void run() throws Exception {

								optimalMz.run();

								addTextTab(optimalMz.printTopResults(n));

								for (OptimalMz.Stats r : optimalMz.getTopResults(n)) {
									createGraph(bins.getLowerMz(r.index), bins.getHigherMz(r.index));
								}
							}
						};
						submitTask(task);

					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			dialog.setVisible(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * ActionListener of the Template button.
	 * 
	 */
	private void btnTemplate() {
		try {

			if (msiimage == null) {
				JOptionPane.showMessageDialog(null, "First load the MZXML data", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			DialogTemplate frame = new DialogTemplate(msiimage, FrameMain.this, true);
			frame.addOkListener(new DialogTemplate.IOkListener() {

				@Override
				public void actionPerformed(ActionEvent e, ICheckLetter isLetter) {
					isLetterTemplate = isLetter;
					jradTempl.setSelected(true);
				}
			});
			frame.setVisible(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * ActionListener of the Draw button.
	 * 
	 */
	private void btnDraw() {
		try {

			if (msiimage == null) {
				JOptionPane.showMessageDialog(null, "First load the MZXML data", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			DialogDraw frame = new DialogDraw(msiimage, FrameMain.this, true);
			frame.addOkListener(new DialogDraw.IOkListener() {

				@Override
				public void actionPerformed(ActionEvent e, ICheckLetter isLetter) {
					isLetterDraw = isLetter;
					jradDraw.setSelected(true);
				}
			});
			frame.setVisible(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Displays a mass spectrometry image at the given interval
	 * 
	 * @param lowerMass
	 *            the lower mass per charge value
	 * @param higherMass
	 *            the upper mass per charge value
	 */
	private void createGraph(double lowerMass, double higherMass) {
		String title = String.format("%sm/z - %sm/z", Double.toString(lowerMass), Double.toString(higherMass));
		addGraphTab(title, lowerMass, higherMass);
	}

	/**
	 * Displays a mass spectrometry image at the given interval with custom title
	 * 
	 * @param title
	 *            sets graph title
	 * @param mzrange
	 *            the lower and upper mass per charge value. Arguments must be multiple of 2
	 */
	private void addGraphTab(String title, double... massrange) {

		ITask task = new ITask() {

			@Override
			public void run() throws Exception {
				double[][] intensity = msiimage.getReduceSpec(progressTracker, massrange);
				PanelGraph panelGraph = new PanelGraph();

				panelGraph.setTitle(title);
				BufferedImage image = panelGraph.calculateImage(intensity);
				panelGraph.draw(image, msiimage.getWidthMM(), msiimage.getHeightMM());

				updateUI(new Runnable() {
					@Override
					public void run() {
						tabbedPane.add("Tab " + (tabbedPane.getTabCount() + 1), panelGraph);
					}
				});
			}
		};
		submitTask(task);
	}

	/**
	 * Creates a new tab with scrollable text box
	 * 
	 * @param t
	 *            Sets the text of this TextComponent to the specified text.
	 */
	private void addTextTab(String t) {
		JPanel background = new JPanel();
		background.setBorder(new EmptyBorder(5, 5, 5, 5));
		background.setLayout(new BorderLayout(0, 0));

		JTextArea textBox = new JTextArea();
		textBox.setEditable(false);
		textBox.setText(t);
		Font f = new Font("monospaced", Font.PLAIN, 16);
		textBox.setFont(f);
		JScrollPane scroller = new JScrollPane(textBox);
		textBox.setLineWrap(true);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		background.add(BorderLayout.CENTER, scroller);

		updateUI(new Runnable() {
			@Override
			public void run() {
				tabbedPane.add("OptimalMz " + (tabbedPane.getTabCount() + 1), background);
			}
		});
	}

	/**
	 * Creates a dialog that block UI interaction
	 * 
	 * @return dialog
	 */
	private JDialog showWaitDialog() {
		JDialog loading = new JDialog(this, true);
		loading.setLayout(new BorderLayout());
		loading.add(new JLabel("Please wait..."), BorderLayout.CENTER);
		loading.setLocationRelativeTo(this);
		loading.setUndecorated(true);
		loading.pack();
		return loading;
	}

	/**
	 * Task is added to the queue and is executed in the background. Catches exceptions and shows an error message
	 * 
	 * @param task
	 */
	private void submitTask(ITask task) {

		executorService.submit(new Runnable() {

			@Override
			public void run() {
				try {
					task.run();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	/**
	 * Task that update user interface Catches exceptions and shows an error message
	 * 
	 * @param task
	 */
	private void updateUI(Runnable task) {
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

	IProgress progressTracker = new IProgress() {

		@Override
		public void update(int value) {
			if (value == 0) {
				progressBar.setVisible(true);
			} else if (value == 100) {
				progressBar.setVisible(false);
			}
			progressBar.setValue(value);
		}
	};
}
