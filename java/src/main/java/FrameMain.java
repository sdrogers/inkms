import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import pyhton.LoadMZXML;
import pyhton.OptimalMz;
import pyhton.OptimalMzV2;
import pyhton.IOptimalMz;
import pyhton.IsLetterV1;

@SuppressWarnings("serial")
public class FrameMain extends JFrame {

	private static boolean DEBUG = true;
	private static int THREADS = 1;
	private static int GRAPHS = 5;

	private LoadMZXML loadMZXML;

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

		// Create a tabbed pane
		tabbedPane = new JTabbedPane();
		background.add(BorderLayout.CENTER, tabbedPane);

		Box buttonBox = new Box(BoxLayout.X_AXIS);
		background.add(BorderLayout.SOUTH, buttonBox);

		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnLoad();
			}
		});
		buttonBox.add(btnLoad);
		buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton btnGraph = new JButton("Graph");
		btnGraph.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnGraph();
			}
		});
		buttonBox.add(btnGraph);
		buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton btnOptimalMz = new JButton("OptimalMz");
		btnOptimalMz.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnOptimalMz(1);
			}
		});
		buttonBox.add(btnOptimalMz);
		buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton btnOptimalMz2 = new JButton("OptimalMz2");
		btnOptimalMz2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnOptimalMz(2);
			}
		});
		buttonBox.add(btnOptimalMz2);
		buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
			}

		});

		executorService = Executors.newFixedThreadPool(THREADS);

	}

	private void btnLoad() {
		if (DEBUG) {
			LoadMZXML.Param param = new LoadMZXML.Param();
			param.filepath = "E:\\Enironments\\data\\abcdefgh_1.mzXML";
			param.lines = 8;
			param.widthInMM = 62;
			param.heightInMM = 10;
			param.downMotionInMM = 1.25f;
			try {
				loadMZXML = new LoadMZXML(param, LoadMZXML.Type.NORMAL);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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

						IsLetterV1 isLetter = new IsLetterV1(loadMZXML, x_start, x_stop, y_start, y_stop);
						IOptimalMz optimalMz;
						if (version == 1) {
							optimalMz = new OptimalMz(isLetter, loadMZXML, lowerMass, higherMass, resolution);
						} else {
							optimalMz = new OptimalMzV2(isLetter, loadMZXML, lowerMass, higherMass, resolution);
						}
						optimalMz.printN(5);

						double range = optimalMz.getRange();
						for (int index : optimalMz.getIndexesN(GRAPHS)) {
							double mz = optimalMz.getMz(index);
							createGraph(mz, mz + range);
						}

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
		Runnable task = new Runnable() {

			@Override
			public void run() {
				try {

					double[][] intensity = loadMZXML.getReduceSpec(lowerMass, higherMass);
					PanelGraph graph = new PanelGraph();

					graph.setTitle(String.format("%sm/z - %sm/z", strLowerMass, strHigherMass));
					graph.draw(intensity, loadMZXML.getWidthMM(), loadMZXML.getHeightMM());

					updateUI(new Runnable() {
						@Override
						public void run() {
							tabbedPane.add("Tab " + (tabbedPane.getTabCount() + 1), graph);
						}
					});
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Task Failed", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		executorService.submit(task);
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
}
