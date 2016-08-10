package com.constambeys.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;

import org.apache.commons.io.FilenameUtils;

import com.constambeys.python.BinEvenlyDistributed;
import com.constambeys.python.BinsPartsPerMillion;
import com.constambeys.python.IBinResolution;
import com.constambeys.python.ICheckLetter;
import com.constambeys.python.IProgress;
import com.constambeys.python.IsLetterV1;
import com.constambeys.python.OptimalMz;
import com.constambeys.python.OptimalMzV1;
import com.constambeys.python.OptimalMzV2;
import com.constambeys.readers.MSIImage;
import com.constambeys.readers.Spectrum;
import com.constambeys.ui.colormaps.Colormaps;
import com.constambeys.ui.colormaps.Hot;
import com.constambeys.ui.colormaps.IColormap;
import com.constambeys.ui.graph.PanelGraph;
import com.constambeys.ui.graph.PanelVlines;

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

	private ICheckLetter isLetterTemplate;
	private ICheckLetter isLetterDraw;

	// Region
	private JRadioButton jradRect;
	private JRadioButton jradOverlay;
	private JRadioButton jradDraw;

	// Bins
	private JRadioButton jradEvenly;
	private JRadioButton jradPPM;

	// Parameters
	private JFormattedTextField jnumGraphs;
	private JFormattedTextField jV2PixelsWeight;

	// Buttons
	private JButton btnGraph;
	private JButton btnOptimalMz;
	private JButton btnOptimalMz2;
	private JButton btnOverlay;
	private JButton btnDraw;
	private JButton btnSpectrum;
	private JCheckBox jcheckSettings;

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

			Box boxEast = createSettings();
			background.add(BorderLayout.EAST, boxEast);

			createJButtons(boxSouth);
			addListeners(boxEast);
			createMenuBar();

			executorService = Executors.newFixedThreadPool(THREADS);

			// User must load a file once the program is started
			updateUI(new ITask() {

				@Override
				public void run() throws Exception {
					btnLoad(false);
				}
			});

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * @return the settings UI
	 */
	private Box createSettings() {

		JLabel l;

		Box box1 = new Box(BoxLayout.Y_AXIS);
		box1.setBorder(BorderFactory.createTitledBorder("Region"));
		jradRect = new JRadioButton("Rectangle", true);
		jradOverlay = new JRadioButton("Overlay");
		jradDraw = new JRadioButton("Draw Region");
		ButtonGroup group1 = new ButtonGroup();
		group1.add(jradRect);
		group1.add(jradOverlay);
		group1.add(jradDraw);
		box1.add(jradRect);
		box1.add(jradOverlay);
		box1.add(jradDraw);

		Box box2 = new Box(BoxLayout.Y_AXIS);
		box2.setBorder(BorderFactory.createTitledBorder("Bins"));
		jradEvenly = new JRadioButton("Evenly", true);
		jradPPM = new JRadioButton("PPM");
		ButtonGroup group2 = new ButtonGroup();
		group2.add(jradEvenly);
		group2.add(jradPPM);
		box2.add(jradEvenly);
		box2.add(jradPPM);

		Box box3 = new Box(BoxLayout.Y_AXIS);
		box3.setBorder(BorderFactory.createTitledBorder("Parameters"));
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
		box3.add(l);
		box3.add(jnumGraphs);

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
		box3.add(l);
		box3.add(jV2PixelsWeight);

		Box main = new Box(BoxLayout.Y_AXIS);
		main.add(box1);
		main.add(Box.createRigidArea(new Dimension(0, 10)));
		main.add(box2);
		main.add(Box.createRigidArea(new Dimension(0, 10)));
		main.add(box3);

		return main;
	}

	/**
	 * initialises the UI buttons
	 * 
	 * @param boxSouth
	 */
	private void createJButtons(Box boxSouth) {
		// JButton btnLoad = new JButton("Load");
		// boxSouth.add(btnLoad);
		// boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		btnGraph = new JButton("Graph");
		boxSouth.add(btnGraph);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		btnOptimalMz = new JButton("OptimalMz");
		boxSouth.add(btnOptimalMz);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		btnOptimalMz2 = new JButton("OptimalMz2");
		boxSouth.add(btnOptimalMz2);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		btnOverlay = new JButton("Overlay");
		boxSouth.add(btnOverlay);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		btnDraw = new JButton("Draw");
		boxSouth.add(btnDraw);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		btnSpectrum = new JButton("Spectrum");
		boxSouth.add(btnSpectrum);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		boxSouth.add(Box.createHorizontalGlue());
		jcheckSettings = new JCheckBox("Settings");
		jcheckSettings.setSelected(true);
		boxSouth.add(jcheckSettings);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));
	}

	/**
	 * initialises the UI listeners
	 * 
	 * @param boxEast
	 */
	private void addListeners(Box boxEast) {
		// btnLoad.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// btnLoad(false);
		// }
		// });

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

		btnOverlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnOverlay();
			}
		});

		btnDraw.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnDraw();
			}
		});

		btnSpectrum.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnSpectrum();
			}
		});

		jcheckSettings.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boxEast.setVisible(jcheckSettings.isSelected());
			}
		});
	}

	/**
	 * Creates {@code JMenuBar}
	 * 
	 * @throws IOException
	 */
	private void createMenuBar() throws IOException {

		JMenuBar menubar = new JMenuBar();

		JMenu fileMenuTabs = new JMenu("Tabs");

		JMenuItem jMenuClose = new JMenuItem("Close");
		int height = (int) jMenuClose.getPreferredSize().getHeight();
		jMenuClose.setIcon(Startup.loadIcon("closeBlue128.png", height, height));

		JMenuItem jMenuCloseAll = new JMenuItem("Close All");
		jMenuCloseAll.setIcon(Startup.loadIcon("closeRed128.png", height, height));

		JMenu fileMenuGraph = new JMenu("Graph");

		JMenuItem jMenuNewWindow = new JMenuItem("Open");
		jMenuNewWindow.setIcon(Startup.loadIcon("open64.png", height, height));

		JMenuItem jMenuExport = new JMenuItem("Export");
		jMenuExport.setIcon(Startup.loadIcon("export128.png", height, height));

		JMenuItem jMenuSave = new JMenuItem("Save");
		jMenuSave.setIcon(Startup.loadIcon("save128.png", height, height));

		jMenuNewWindow.addActionListener(new ActionListener() {
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
								FrameGraph window = new FrameGraph(msiimage);
								window.setGraph(pg.getTitle(), imgGenerated);
								window.setVisible(true);
							}
						} else {
							JOptionPane.showMessageDialog(null, "Before open load a graph", "Error", JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Before open load a graph", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		jMenuExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					int selected = tabbedPane.getSelectedIndex();
					if (selected >= 0) {
						Component component = tabbedPane.getComponentAt(selected);
						if (component instanceof PanelGraph) {
							PanelGraph pg = (PanelGraph) component;
							double[][] intensity = ((GraphMetadata) pg.getMetadata()).intensity;
							if (intensity != null) {
								JFileChooser fileChooser = new JFileChooser();
								FileNameExtensionFilter filter = new FileNameExtensionFilter("csv file", "csv");
								fileChooser.setFileFilter(filter);

								// fileChooser
								if (fileChooser.showSaveDialog(FrameMain.this) == JFileChooser.APPROVE_OPTION) {

									File file = fileChooser.getSelectedFile();
									if (!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase(".csv")) {
										// ALTERNATIVELY: remove the extension (if any) and replace it with ".csv"
										file = new File(file.getParentFile(), FilenameUtils.getBaseName(file.getName()) + ".csv");
									}

									try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
										for (int line = 0; line < intensity.length; line++) {
											for (int column = 0; column < intensity[line].length; column++) {
												if (column != 0) {
													writer.write(",");
												}
												writer.write(Double.toString(intensity[line][column]));
											}
											writer.write("\n");
										}
									} catch (IOException ex) {
										JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
									}
								}
							}
						} else {
							JOptionPane.showMessageDialog(null, "Before export load a graph", "Error", JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Before export load a graph", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		jMenuSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					int selected = tabbedPane.getSelectedIndex();
					if (selected >= 0) {
						Component component = tabbedPane.getComponentAt(selected);
						if (component instanceof PanelGraph) {
							PanelGraph pg = (PanelGraph) component;
							PanelGraph pgNew = new PanelGraph();
							pgNew.setGraphTitle(pg.getTitle());
							pgNew.draw(pg.getImage(), msiimage.getWidthMM(), msiimage.getHeightMM());
							pgNew.setSize(new Dimension(1920, 1080));

							BufferedImage imgGenerated = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_ARGB);
							Graphics g = imgGenerated.createGraphics();
							pgNew.paint(g);
							g.dispose();
							if (imgGenerated != null) {
								JFileChooser fileChooser = new JFileChooser();
								FileNameExtensionFilter filter = new FileNameExtensionFilter("png file", ".png");
								fileChooser.setFileFilter(filter);

								if (fileChooser.showSaveDialog(FrameMain.this) == JFileChooser.APPROVE_OPTION) {
									File file = fileChooser.getSelectedFile();
									if (!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase(".png")) {
										// ALTERNATIVELY: remove the extension (if any) and replace it with ".csv"
										file = new File(file.getParentFile(), FilenameUtils.getBaseName(file.getName()) + ".png");
									}

									// save to file
									ImageIO.write(imgGenerated, "PNG", file);
								}
							}
						} else {
							JOptionPane.showMessageDialog(null, "Before save load a graph", "Error", JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Before save load a graph", "Error", JOptionPane.ERROR_MESSAGE);
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

		fileMenuTabs.add(jMenuClose);
		fileMenuTabs.add(jMenuCloseAll);

		fileMenuGraph.add(jMenuNewWindow);
		fileMenuGraph.add(jMenuExport);
		fileMenuGraph.add(jMenuSave);

		menubar.add(fileMenuTabs);
		menubar.add(fileMenuGraph);

		setJMenuBar(menubar);
	}

	/**
	 * 
	 * ActionListener of the Load button.
	 *
	 */
	private void btnLoad(boolean canClose) {
		try {
			DialogLoad dialog = new DialogLoad(FrameMain.this, canClose);
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

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
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

			GraphMetadata metadata = null;
			int selected = tabbedPane.getSelectedIndex();
			if (selected >= 0) {
				Component component = tabbedPane.getComponentAt(selected);
				if (component instanceof PanelGraph) {
					PanelGraph pg = (PanelGraph) component;
					metadata = (GraphMetadata) pg.getMetadata();
				}
			}

			DialogGraph dialog = new DialogGraph(FrameMain.this, "Set Parameters", true);
			if (metadata != null && metadata.massrange != null) {
				dialog.addMassRange(metadata.massrange);
			}
			if (metadata != null && metadata.colormap != null) {
				dialog.setColormap(metadata.colormap);
			}
			dialog.setLocationRelativeTo(this);
			dialog.pack();
			dialog.addOkListener(new DialogGraph.IOkListener() {

				@Override
				public void actionPerformed(ActionEvent e, MassRange ranges[], IColormap colormap) {
					try {
						if (ranges.length == 0) {
							return;
						}

						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < ranges.length; i++) {
							MassRange range = ranges[i];
							if (i != 0) {
								sb.append(", ");
							}
							sb.append(range.toStringV2());
						}

						addGraphTab(sb.toString(), colormap, ranges);

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

			if (jradOverlay.isSelected() && isLetterTemplate == null) {
				throw new Exception("Click Overlay before OptimalMz to specify overlay");
			}

			if (jradDraw.isSelected() && isLetterDraw == null) {
				throw new Exception("Click Draw before OptimalMz to specify draw");
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

			if (jradEvenly.isSelected()) {
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
						} else if (jradOverlay.isSelected()) {
							isLetter = isLetterTemplate;
						} else {
							isLetter = isLetterDraw;
						}

						double lowerMass = Double.parseDouble(dialog.getText("lmass")); // 300
						double higherMass = Double.parseDouble(dialog.getText("hmass"));// 500

						IBinResolution bins;
						if (jradEvenly.isSelected()) {
							int resolution = Integer.parseInt(dialog.getText("resolution"));
							bins = new BinEvenlyDistributed(lowerMass, higherMass, resolution);
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

								for (OptimalMz.BinStatistics r : optimalMz.getTopResults(n)) {
									addGraphTab(new Hot(), bins.getLowerMz(r.index), bins.getHigherMz(r.index));
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
	 * ActionListener of the Overlay button.
	 * 
	 */
	private void btnOverlay() {

		try {

			if (msiimage == null) {
				JOptionPane.showMessageDialog(null, "First load the MZXML data", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			int selected = tabbedPane.getSelectedIndex();
			if (selected >= 0) {
				Component component = tabbedPane.getComponentAt(selected);
				if (component instanceof PanelGraph) {
					PanelGraph pg = (PanelGraph) component;
					BufferedImage imgGenerated = pg.getImage();
					if (imgGenerated != null) {

						DialogOverlay frame = new DialogOverlay(msiimage, FrameMain.this, true);
						frame.setGraph(imgGenerated);
						frame.addOkListener(new DialogOverlay.IOkListener() {

							@Override
							public void actionPerformed(ActionEvent e, ICheckLetter isLetter) {
								isLetterTemplate = isLetter;
								jradOverlay.setSelected(true);
							}
						});
						frame.setVisible(true);
					} else {
						JOptionPane.showMessageDialog(null, "Something is wrong with the graph", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Draw a graph before overlay", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(null, "Draw a graph before overlay", "Error", JOptionPane.ERROR_MESSAGE);
			}

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

			int selected = tabbedPane.getSelectedIndex();
			if (selected >= 0) {
				Component component = tabbedPane.getComponentAt(selected);
				if (component instanceof PanelGraph) {
					PanelGraph pg = (PanelGraph) component;
					BufferedImage imgGenerated = pg.getImage();
					if (imgGenerated != null) {
						DialogDraw frame = new DialogDraw(msiimage, FrameMain.this, true);
						frame.setGraph(imgGenerated);
						frame.addOkListener(new DialogDraw.IOkListener() {

							@Override
							public void actionPerformed(ActionEvent e, ICheckLetter isLetter) {
								isLetterDraw = isLetter;
								jradDraw.setSelected(true);
							}
						});
						frame.setVisible(true);
					} else {
						JOptionPane.showMessageDialog(null, "Something is wrong with the graph", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Draw a graph before draw", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(null, "Draw a graph before draw", "Error", JOptionPane.ERROR_MESSAGE);
			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * ActionListener of the Spectrum button.
	 * 
	 */
	private void btnSpectrum() {
		try {
			if (Startup.DEBUG_SPECTRUM) {
				PanelVlines panelVlines = new PanelVlines();
				panelVlines.add(0, 0, true);
				panelVlines.add(40, 40, true);
				panelVlines.add(60, 60, false);
				panelVlines.add(100, 40, false);
				panelVlines.addCommit();
				tabbedPane.add("Tab " + (tabbedPane.getTabCount() + 1), panelVlines);
				return;
			}

			if (msiimage == null) {
				JOptionPane.showMessageDialog(null, "First load the MZXML data", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (jradOverlay.isSelected() && isLetterTemplate == null) {
				throw new Exception("Click Overlay before Spectrum to specify overlay");
			}

			if (jradDraw.isSelected() && isLetterDraw == null) {
				throw new Exception("Click Draw before Spectrum to specify draw");
			}

			int selected = tabbedPane.getSelectedIndex();
			if (selected >= 0) {
				Component component = tabbedPane.getComponentAt(selected);
				if (component instanceof PanelGraph) {
					PanelGraph pg = (PanelGraph) component;
					BufferedImage imgGenerated = pg.getImage();
					if (imgGenerated != null) {
						FrameSpectrum window = new FrameSpectrum(msiimage);
						window.setGraph(pg.getTitle(), imgGenerated);
						window.setVisible(true);

						/*
						 * DialogDraw frame = new DialogDraw(msiimage, FrameMain.this, true); frame.setTitle("Draw reference region"); frame.setGraph(imgGenerated); frame.addOkListener(new DialogDraw.IOkListener() {
						 * 
						 * @Override public void actionPerformed(ActionEvent e, ICheckLetter isLetter1) { btnSpectrumHelp(isLetter1); } }); frame.setVisible(true);
						 */
					} else {
						JOptionPane.showMessageDialog(null, "Something is wrong with the graph", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Draw a graph before spectrum", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(null, "Draw a graph before spectrum", "Error", JOptionPane.ERROR_MESSAGE);
			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}

	}

	private void btnSpectrumHelp(ICheckLetter isLetter1) {
		DialogOptimalMz dialog = new DialogOptimalMz(FrameMain.this, "Set Parameters", true);

		if (jradRect.isSelected()) {
			dialog.addTextBox("xstart", "x start (mm)");
			dialog.addTextBox("xstop", "x stop (mm)");
			dialog.addTextBox("ystart", "y start (mm)");
			dialog.addTextBox("ystop", "y stop (mm)");
		}

		dialog.addTextBox("ymin", "min intensity value", "0");

		dialog.pack();
		dialog.addOkListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ICheckLetter isLetter0;

					if (jradRect.isSelected()) {
						double x_start = Double.parseDouble(dialog.getText("xstart")); // 30
						double x_stop = Double.parseDouble(dialog.getText("xstop"));// 40
						double y_start = Double.parseDouble(dialog.getText("ystart"));// 0
						double y_stop = Double.parseDouble(dialog.getText("ystop"));// 10

						isLetter0 = new IsLetterV1(msiimage, x_start, x_stop, y_start, y_stop);
					} else if (jradOverlay.isSelected()) {
						isLetter0 = isLetterTemplate;
					} else {
						isLetter0 = isLetterDraw;
					}

					int ymin = Integer.parseInt(dialog.getText("ymin"));

					ITask task = new ITask() {

						@Override
						public void run() throws Exception {
							PanelVlines panelVlines = new PanelVlines();

							for (int line = 0; line < msiimage.getLines(); line++) {
								if (progressTracker != null)
									progressTracker.update((int) ((float) line / msiimage.getLines() * 100));

								for (int x = 0; x < msiimage.getWidthPixels(); x++) {
									Spectrum spectrum = msiimage.getSpectrum(line, x);
									boolean isRegion0 = isLetter0.check(x, line);
									boolean isRegion1 = isLetter1.check(x, line);

									if (isRegion0 || isRegion1) {
										for (int s = 0; s < spectrum.mzs.length; s++) {
											double mz = spectrum.mzs[s];
											double i = spectrum.ints[s];
											if (i >= ymin)
												panelVlines.add(mz, i, isRegion0);
										}
									}
								}
							}

							panelVlines.addCommit();

							if (progressTracker != null)
								progressTracker.update(100);

							updateUI(new ITask() {
								@Override
								public void run() {
									tabbedPane.add("Tab " + (tabbedPane.getTabCount() + 1), panelVlines);
								}
							});
						}
					};
					submitTask(task);

				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		dialog.setVisible(true);
	}

	/**
	 * Displays a mass spectrometry image at the given interval
	 * 
	 * @param lowerMass
	 *            the lower mass per charge value
	 * @param higherMass
	 *            the upper mass per charge value
	 */
	private void addGraphTab(IColormap colormap, double lowerMass, double higherMass) {
		MassRange range = new MassRange(lowerMass, higherMass);
		addGraphTab(range.toStringV2(), colormap, range);
	}

	/**
	 * Displays a mass spectrometry image at the given interval with custom title
	 * 
	 * @param title
	 *            sets graph title
	 * @param mzrange
	 *            the lower and upper mass per charge value. Arguments must be multiple of 2
	 */
	private void addGraphTab(String title, IColormap colormap, MassRange... massrange) {

		ITask task = new ITask() {

			@Override
			public void run() throws Exception {
				double[][] intensity = msiimage.getTotalIntensity(progressTracker, MassRange.convertToDouble(massrange));

				GraphMetadata metadata = new GraphMetadata();
				metadata.intensity = intensity;
				metadata.colormap = colormap;
				metadata.massrange = massrange;

				PanelGraph panelGraph = new PanelGraph();
				panelGraph.setGraphTitle(title);
				BufferedImage image = Colormaps.calculateImage(intensity, colormap);
				panelGraph.draw(image, msiimage.getWidthMM(), msiimage.getHeightMM());
				panelGraph.setMetadata(metadata);

				updateUI(new ITask() {
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

		updateUI(new ITask() {
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

	IProgress progressTracker = new IProgress() {

		@Override
		public void update(int value) {
			if (value == 0) {
				updateUI(new ITask() {

					@Override
					public void run() throws Exception {
						progressBar.setVisible(true);
					}
				});
			} else if (value == 100) {
				updateUI(new ITask() {

					@Override
					public void run() throws Exception {
						progressBar.setVisible(false);
					}
				});
			}

			updateUI(new ITask() {

				@Override
				public void run() throws Exception {
					progressBar.setValue(value);
				}

			});
		}
	};
}
