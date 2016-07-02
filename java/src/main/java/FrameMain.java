import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import pyhton.LoadMZXML;
import pyhton.OptimalMz;
import pyhton.IOptimalMz;
import pyhton.IProgress;
import pyhton.IsLetterV1;

@SuppressWarnings("serial")
public class FrameMain extends JFrame {

	private static int THREADS = 1;
	private static int GRAPHS = 5;

	private LoadMZXML loadMZXML;
	private JProgressBar progressBar;
	private JTabbedPane tabbedPane;

	private ExecutorService executorService;

	/**
	 * Create the frame.
	 */
	public FrameMain() {
		setTitle("JavaBall");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 300);

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
		btnLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnLoad();
			}
		});
		boxSouth.add(btnLoad);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton btnGraph = new JButton("Graph");
		btnGraph.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnGraph();
			}
		});
		boxSouth.add(btnGraph);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton btnOptimalMz = new JButton("OptimalMz");
		btnOptimalMz.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnOptimalMz(1);
			}
		});
		boxSouth.add(btnOptimalMz);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton btnOptimalMz2 = new JButton("OptimalMz2");
		btnOptimalMz2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnOptimalMz(2);
			}
		});
		boxSouth.add(btnOptimalMz2);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
			}

		});

		executorService = Executors.newFixedThreadPool(THREADS);
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

			DialogOptimalMz dialog = new DialogOptimalMz(FrameMain.this, "Set Parameters", true);
			dialog.pack();
			dialog.addOkListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						int x_start = Integer.parseInt(dialog.jxStart.getText()); // 30
						int x_stop = Integer.parseInt(dialog.jxStop.getText());// 40
						int y_start = Integer.parseInt(dialog.jyStart.getText());// 0
						int y_stop = Integer.parseInt(dialog.jyStop.getText());// 10
						double lowerMass = Double.parseDouble(dialog.jLowerMass.getText()); // 300
						double higherMass = Double.parseDouble(dialog.jHigherMass.getText());// 500
						int resolution = Integer.parseInt(dialog.jResolution.getText());// 200

						ITask task = new ITask() {

							@Override
							public void run() throws Exception {
								IsLetterV1 isLetter = new IsLetterV1(loadMZXML, x_start, x_stop, y_start, y_stop);
								IOptimalMz optimalMz;
								if (version == 1) {
									OptimalMz optimalMzV1 = new OptimalMz();
									optimalMzV1.setProgressListener(progressTracker);
									optimalMzV1.run(isLetter, loadMZXML, lowerMass, higherMass, resolution);
									optimalMz = optimalMzV1;
								} else {
									OptimalMz optimalMzV2 = new OptimalMz();
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
				BufferedImage image = panelGraph.getImage(intensity);
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
