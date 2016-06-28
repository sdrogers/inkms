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

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private LoadMZXML loadMZXML;
	private Graph graph;

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("JavaBall");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 300);

		JPanel background = new JPanel();
		background.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Border Layout North, South,East,West
		background.setLayout(new BorderLayout(0, 0));

		graph = new Graph();
		background.add(BorderLayout.CENTER, graph);

		Box buttonBox = new Box(BoxLayout.X_AXIS);
		JButton btnSearch = new JButton("Load");
		btnSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LoadMZXML.Param param = new LoadMZXML.Param();
				param.filepath = "E:\\Enironments\\data\\abcdefgh_1.mzXML";
				param.lines = 8;
				param.widthInMM = 62;
				param.heightInMM = 10;
				param.downMotionInMM = 1.25f;

				try {
					loadMZXML = new LoadMZXML(param, LoadMZXML.Type.NORMAL);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonBox.add(btnSearch);
		buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton btnAdd = new JButton("Graph");
		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (loadMZXML == null) {
						JOptionPane.showMessageDialog(null, "First load the MZXML data", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					double[][] intensity = loadMZXML.getReduceSpec(374, 376);
					graph.setTitle("374m/z - 376m/z");
					graph.draw(intensity, loadMZXML.getParameters().widthInMM, loadMZXML.getParameters().heightInMM);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonBox.add(btnAdd);
		buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton btnGraph = new JButton("Add");
		btnGraph.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		buttonBox.add(btnGraph);
		buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton btnMatch = new JButton("Match");
		btnMatch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// btnMatch();
			}
		});
		buttonBox.add(btnMatch);
		buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));

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
