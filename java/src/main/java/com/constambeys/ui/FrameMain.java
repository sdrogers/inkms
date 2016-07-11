package com.constambeys.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import com.constambeys.python.ICheckLetter;
import com.constambeys.python.IOptimalMz;
import com.constambeys.python.IProgress;
import com.constambeys.python.IsLetterV1;
import com.constambeys.python.LoadMZXML;
import com.constambeys.python.OptimalMz;
import com.constambeys.python.OptimalMzV2;

@SuppressWarnings("serial")
public class FrameMain extends JFrame {

	private static int THREADS = 1;
	private static int GRAPHS = 5;

	private LoadMZXML loadMZXML;
	private JProgressBar progressBar;
	private JTabbedPane tabbedPane;

	private JCheckBox jcheckRectangle;
	private JCheckBox jCheckTemplate;
	private ICheckLetter templateIsLetter;

	private ExecutorService executorService;

	/**
	 * Create the frame.
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
			jcheckRectangle = new JCheckBox("Rectangle");
			jcheckRectangle.setSelected(true);
			boxSouth.add(jcheckRectangle);
			boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

			jCheckTemplate = new JCheckBox("Template");
			jCheckTemplate.setSelected(false);
			boxSouth.add(jCheckTemplate);
			boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

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

			jcheckRectangle.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					jCheckTemplate.setSelected(!jcheckRectangle.isSelected());
				}
			});

			jCheckTemplate.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					jcheckRectangle.setSelected(!jCheckTemplate.isSelected());
				}
			});

			executorService = Executors.newFixedThreadPool(THREADS);
			createMenuBar();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

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

	private void btnLoad() {
		if (Startup.DEBUG) {
			loadMZXML = Startup.loadMZML(progressTracker);
		} else {

			DialogParameters dialog = new DialogParameters(FrameMain.this, "Set Parameters", true);
			dialog.pack();
			dialog.addOkListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						LoadMZXML.Param param = new LoadMZXML.Param();
						param.filepath = dialog.jFilePath.getText();
						param.lines = Integer.parseInt(dialog.jtextLines.getText());
						param.widthInMM = Integer.parseInt(dialog.jtextWidth.getText());
						param.heightInMM = Integer.parseInt(dialog.jtextHeight.getText());
						param.downMotionInMM = Float.parseFloat(dialog.jtextDownMotion.getText());

						String stype = (String) dialog.jcomboType.getSelectedItem();
						LoadMZXML.Type type;

						if (stype.equals("Normal")) {
							type = LoadMZXML.Type.NORMAL;
						} else if (stype.equals("Positive")) {
							type = LoadMZXML.Type.POSITIVE;
						} else if (stype.equals("Negative")) {
							type = LoadMZXML.Type.NEGATIVE;
						} else {
							throw new Exception("Unsupported scan type: " + stype);
						}

						loadMZXML = new LoadMZXML(param, type);
						loadMZXML.setProgressListener(progressTracker);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			dialog.setVisible(true);
		}
	}

	private void btnGraph() {
		try {
			if (loadMZXML == null) {
				JOptionPane.showMessageDialog(null, "First load the MZXML data", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			DialogGraph dialog = new DialogGraph(FrameMain.this, "Set Parameters", true);
			dialog.pack();
			dialog.addOkListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						String strLowerMass = dialog.jLowerMass.getText();
						String strHigherMass = dialog.jHigherMass.getText();

						double lowerMass = Double.parseDouble(strLowerMass);
						double higherMass = Double.parseDouble(strHigherMass);

						createGraph(strLowerMass, strHigherMass, lowerMass, higherMass);
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

	private void btnOptimalMz(int version) {
		try {
			if (loadMZXML == null) {
				JOptionPane.showMessageDialog(null, "First load the MZXML data", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			boolean rectangle = jcheckRectangle.isSelected();
			if (!rectangle) {
				if (templateIsLetter == null)
					throw new Exception("Template Settings not loaded");
			}

			DialogOptimalMz dialog = new DialogOptimalMz(FrameMain.this, "Set Parameters", true, rectangle);
			dialog.pack();
			dialog.addOkListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						ICheckLetter isLetter;
						double lowerMass = Double.parseDouble(dialog.jLowerMass.getText()); // 300
						double higherMass = Double.parseDouble(dialog.jHigherMass.getText());// 500
						int resolution = Integer.parseInt(dialog.jResolution.getText());// 200

						if (rectangle) {
							double x_start = Double.parseDouble(dialog.jxStart.getText()); // 30
							double x_stop = Double.parseDouble(dialog.jxStop.getText());// 40
							double y_start = Double.parseDouble(dialog.jyStart.getText());// 0
							double y_stop = Double.parseDouble(dialog.jyStop.getText());// 10

							isLetter = new IsLetterV1(loadMZXML, x_start, x_stop, y_start, y_stop);
						} else {
							isLetter = templateIsLetter;
						}

						ITask task = new ITask() {

							@Override
							public void run() throws Exception {
								IOptimalMz optimalMz;
								if (version == 1) {
									OptimalMz optimalMzV1 = new OptimalMz();
									optimalMzV1.setProgressListener(progressTracker);
									optimalMzV1.run(isLetter, loadMZXML, lowerMass, higherMass, resolution);
									optimalMz = optimalMzV1;
								} else {
									OptimalMzV2 optimalMzV2 = new OptimalMzV2();
									optimalMzV2.setProgressListener(progressTracker);
									optimalMzV2.run(isLetter, loadMZXML, lowerMass, higherMass, resolution);
									optimalMz = optimalMzV2;
								}

								createTextBox(optimalMz.printN(5));

								double range = optimalMz.getRange();
								for (int index : optimalMz.getIndexesN(GRAPHS)) {
									double mz = optimalMz.getMz(index);
									createGraph(mz, mz + range);
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

	private void btnTemplate() {
		try {

			if (loadMZXML == null) {
				JOptionPane.showMessageDialog(null, "First load the MZXML data", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			DialogTemplate frame = new DialogTemplate(loadMZXML, FrameMain.this, true);
			frame.addOkListener(new DialogTemplate.IOkListener() {

				@Override
				public void actionPerformed(ActionEvent e, ICheckLetter isLetter) {
					templateIsLetter = isLetter;
					jCheckTemplate.setSelected(true);
				}
			});
			frame.setVisible(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnDraw() {
		try {

			if (loadMZXML == null) {
				JOptionPane.showMessageDialog(null, "First load the MZXML data", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			DialogDraw frame = new DialogDraw(loadMZXML, FrameMain.this, true);
			frame.addOkListener(new DialogDraw.IOkListener() {

				@Override
				public void actionPerformed(ActionEvent e, ICheckLetter isLetter) {
					templateIsLetter = isLetter;
					jCheckTemplate.setSelected(true);
				}
			});
			frame.setVisible(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void createGraph(double lowerMass, double higherMass) {

		createGraph(Double.toString(lowerMass), Double.toString(higherMass), lowerMass, higherMass);
	}

	private void createGraph(String strLowerMass, String strHigherMass, double lowerMass, double higherMass) {
		ITask task = new ITask() {

			@Override
			public void run() throws Exception {
				double[][] intensity = loadMZXML.getReduceSpec(lowerMass, higherMass);
				PanelGraph panelGraph = new PanelGraph();

				panelGraph.setTitle(String.format("%sm/z - %sm/z", strLowerMass, strHigherMass));
				BufferedImage image = panelGraph.calculateImage(intensity);
				panelGraph.draw(image, loadMZXML.getWidthMM(), loadMZXML.getHeightMM());

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

	private void createTextBox(String t) {
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

	private void updateUI(Runnable task) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					task.run();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Task Failed", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

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
