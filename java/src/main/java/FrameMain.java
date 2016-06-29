import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import pyhton.LoadMZXML;
import pyhton.OptimalMzV1;

@SuppressWarnings("serial")
public class FrameMain extends JFrame {

	private static boolean DEBUG = true;

	private LoadMZXML loadMZXML;
	private PanelGraph graph;

	/**
	 * Create the frame.
	 */
	public FrameMain() {
		setTitle("JavaBall");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 300);

		JPanel background = new JPanel();
		background.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Border Layout North, South,East,West
		background.setLayout(new BorderLayout(0, 0));

		graph = new PanelGraph();
		background.add(BorderLayout.CENTER, graph);

		Box buttonBox = new Box(BoxLayout.X_AXIS);

		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
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
								JOptionPane.showMessageDialog(null, ex.getMessage(), "Error",
										JOptionPane.ERROR_MESSAGE);
							}
						}
					});
					dialog.setVisible(true);
				}

			}
		});
		buttonBox.add(btnLoad);
		buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton btnGraph = new JButton("Graph");
		btnGraph.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (loadMZXML == null) {
						JOptionPane.showMessageDialog(null, "First load the MZXML data", "Error",
								JOptionPane.ERROR_MESSAGE);
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

								// lowerMass = 374;
								// higherMass = 376;
								double[][] intensity = loadMZXML.getReduceSpec(lowerMass, higherMass);
								graph.setTitle(String.format("%sm/z - %sm/z", strLowerMass, strHigherMass));
								graph.draw(intensity, loadMZXML.getWidthMM(), loadMZXML.getHeightMM());
							} catch (Exception ex) {
								JOptionPane.showMessageDialog(null, ex.getMessage(), "Error",
										JOptionPane.ERROR_MESSAGE);
							}
						}
					});
					dialog.setVisible(true);

				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonBox.add(btnGraph);
		buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton btnOptimalMz = new JButton("OptimalMz");
		btnOptimalMz.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					OptimalMzV1 optimalMz = new OptimalMzV1(loadMZXML, 30, 40, 0, 10, 300, 500, 200);
					optimalMz.printN(5);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonBox.add(btnOptimalMz);
		buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton btnMatch = new JButton("Match");
		btnMatch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// btnMatch();
			}
		});
		// buttonBox.add(btnMatch);
		// buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));

		background.add(BorderLayout.SOUTH, buttonBox);
		setContentPane(background);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);

			}

		});

	}

}
