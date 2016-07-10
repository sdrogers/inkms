package default_package;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import default_package.PanelGraph.Point;
import pyhton.ICheckLetter;
import pyhton.IsLetterV2;
import pyhton.LoadMZXML;

public class DialogDraw extends JDialog {
	enum State {
		NONE, GENERATED, TEMPLATE, BOTH
	}

	interface IOkListener {
		public void actionPerformed(ActionEvent e, ICheckLetter isLetter);
	}

	private IOkListener ok;
	private LoadMZXML loadMZXML;
	private PanelGraphDraw panelGraph;
	private JFormattedTextField jRectSize;

	public DialogDraw(Frame owner, boolean modal) throws Exception {
		super(owner, modal);
		init(Startup.loadMZML(null));
	}

	public DialogDraw(LoadMZXML loadMZXML, Frame owner, boolean modal) throws Exception {
		super(owner, modal);
		init(loadMZXML);
	}

	public void init(LoadMZXML loadMZXML) throws ParseException, IOException {

		this.loadMZXML = loadMZXML;
		setTitle("JavaBall");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 400);

		JPanel background = new JPanel();
		setContentPane(background);

		background.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Border Layout North, South,East,West
		background.setLayout(new BorderLayout(0, 0));

		panelGraph = new PanelGraphDraw();
		background.add(BorderLayout.CENTER, panelGraph);

		Box boxSouth = new Box(BoxLayout.X_AXIS);
		background.add(BorderLayout.SOUTH, boxSouth);

		Box boxEast = new Box(BoxLayout.Y_AXIS);
		background.add(BorderLayout.EAST, boxEast);

		JButton btnGraph = new JButton("Graph");
		boxSouth.add(btnGraph);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton buttonOK = new JButton("OK");
		// fills the empty gap
		boxSouth.add(Box.createHorizontalGlue());
		boxSouth.add(buttonOK);

		JButton buttonUp = new JButton(Startup.loadIcon("up128.png", 30, 30));
		// http://stackoverflow.com/questions/4898584/java-using-an-image-as-a-button
		buttonUp.setBorder(BorderFactory.createEmptyBorder());
		buttonUp.setContentAreaFilled(false);

		JButton buttonDown = new JButton(Startup.loadIcon("down128.png", 30, 30));
		// http://stackoverflow.com/questions/4898584/java-using-an-image-as-a-button
		buttonDown.setBorder(BorderFactory.createEmptyBorder());
		buttonDown.setContentAreaFilled(false);

		JLabel l = new JLabel("Size: ", JLabel.TRAILING);
		NumberFormat format = NumberFormat.getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(0);
		formatter.setAllowsInvalid(true);
		// If you want the value to be committed on each keystroke instead of focus lost
		formatter.setCommitsOnValidEdit(true);
		jRectSize = new JFormattedTextField(formatter);
		jRectSize.setText("10");
		Dimension maxsize = jRectSize.getMaximumSize();
		Dimension prefsize = jRectSize.getPreferredSize();
		maxsize.height = prefsize.height;
		jRectSize.setMaximumSize(maxsize);
		l.setLabelFor(jRectSize);

		JRadioButton radDraw = new JRadioButton("Draw", true);
		JRadioButton radErase = new JRadioButton("Erase");
		ButtonGroup group = new ButtonGroup();
		group.add(radDraw);
		group.add(radErase);
		boxEast.add(radDraw);
		boxEast.add(radErase);

		boxEast.add(Box.createRigidArea(new Dimension(0, 10)));
		boxEast.add(buttonUp);
		boxEast.add(buttonDown);
		boxEast.add(l);
		boxEast.add(jRectSize);

		btnGraph.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnGraph();
			}
		});

		buttonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				btnOK(event);
			}
		});

		buttonUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				int size = (int) jRectSize.getValue();
				jRectSize.setValue(size + 1);
			}
		});

		buttonDown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				int size = (int) jRectSize.getValue();
				jRectSize.setValue(size - 1);
			}
		});

		radDraw.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				panelGraph.setState(e.getStateChange() == ItemEvent.SELECTED);
			}
		});

		PropertyChangeListener callSyncUI = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				panelGraph.setDrawingDimension((int) evt.getNewValue());
			}
		};

		jRectSize.addPropertyChangeListener("value", callSyncUI);

	}

	private void btnGraph() {
		try {
			if (loadMZXML == null) {
				JOptionPane.showMessageDialog(null, "First load the MZXML data", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			DialogGraph dialog = new DialogGraph(this, "Set Parameters", true);
			dialog.pack();
			dialog.addOkListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						String strLowerMass = dialog.jLowerMass.getText();
						String strHigherMass = dialog.jHigherMass.getText();

						double lowerMass = Double.parseDouble(strLowerMass);
						double higherMass = Double.parseDouble(strHigherMass);

						double[][] intensity = loadMZXML.getReduceSpec(lowerMass, higherMass);
						BufferedImage imgGenerated = panelGraph.calculateImage(intensity);
						panelGraph.draw(imgGenerated, loadMZXML.getWidthMM(), loadMZXML.getHeightMM());

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

	private void btnOK(ActionEvent event) {
		try {

			BufferedImage overlay = panelGraph.getTemplateOverlay();

			ICheckLetter isLetter = new IsLetterV2(overlay, Color.WHITE);
			setVisible(false);
			dispose();
			if (ok != null)
				ok.actionPerformed(event, isLetter);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void addOkListener(IOkListener l) {
		ok = l;
	}
}
