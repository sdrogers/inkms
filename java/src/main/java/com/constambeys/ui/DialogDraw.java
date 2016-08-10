package com.constambeys.ui;

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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import com.constambeys.python.ICheckLetter;
import com.constambeys.python.IProgress;
import com.constambeys.python.IsLetterV2;
import com.constambeys.readers.MSIImage;
import com.constambeys.ui.graph.PanelGraphDraw;

/**
 * The {@code DialogDraw} class allows the user to draw the interested area on the screen
 * 
 * @author Constambeys
 *
 */
public class DialogDraw extends JDialog {

	interface IOkListener {
		public void actionPerformed(ActionEvent e, ICheckLetter isLetter);
	}

	private JProgressBar progressBar;

	private IOkListener ok;
	private JButton buttonOK;
	private JButton buttonUp;
	private JButton buttonDown;

	private JRadioButton radDraw;
	private JRadioButton radErase;

	private MSIImage msiimage;
	private PanelGraphDraw panelGraph;
	private JFormattedTextField jRectSize;

	/**
	 * Initialises a new user interface dialog using the given image file
	 * 
	 * @param msiimage
	 *            the loaded mass spectrometry image
	 * @param owner
	 *            the Frame from which the dialog is displayed
	 * @param modal
	 *            specifies whether dialog blocks user input to other top-level windows when shown.
	 * @throws Exception
	 */
	public DialogDraw(MSIImage msiimage, Frame owner, boolean modal) throws Exception {
		super(owner, modal);
		setupUI(msiimage);
	}

	/**
	 * Initialises the UI elements
	 * 
	 * @param msiimage
	 *            the loaded mass spectrometry image
	 * @throws ParseException
	 * @throws IOException
	 */
	public void setupUI(MSIImage msiimage) throws ParseException, IOException {

		this.msiimage = msiimage;
		setTitle("MSI");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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

		panelGraph = new PanelGraphDraw();
		background.add(BorderLayout.CENTER, panelGraph);

		Box boxSouth = new Box(BoxLayout.X_AXIS);
		background.add(BorderLayout.SOUTH, boxSouth);

		Box boxEast = new Box(BoxLayout.Y_AXIS);
		background.add(BorderLayout.EAST, boxEast);

		buttonOK = new JButton("OK");
		// fills the empty gap
		boxSouth.add(Box.createHorizontalGlue());
		boxSouth.add(buttonOK);

		radDraw = new JRadioButton("Draw", true);
		radErase = new JRadioButton("Erase");
		ButtonGroup group = new ButtonGroup();
		group.add(radDraw);
		group.add(radErase);
		boxEast.add(radDraw);
		boxEast.add(radErase);

		buttonUp = new JButton(Startup.loadIcon("up128.png", 30, 30));
		// http://stackoverflow.com/questions/4898584/java-using-an-image-as-a-button
		buttonUp.setBorder(BorderFactory.createEmptyBorder());
		buttonUp.setContentAreaFilled(false);
		boxEast.add(Box.createRigidArea(new Dimension(0, 10)));
		boxEast.add(buttonUp);

		buttonDown = new JButton(Startup.loadIcon("down128.png", 30, 30));
		// http://stackoverflow.com/questions/4898584/java-using-an-image-as-a-button
		buttonDown.setBorder(BorderFactory.createEmptyBorder());
		buttonDown.setContentAreaFilled(false);
		boxEast.add(buttonDown);

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
		boxEast.add(l);
		boxEast.add(jRectSize);

		addListeners();

	}

	private void addListeners() {
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
				panelGraph.setIsDrawingState(e.getStateChange() == ItemEvent.SELECTED);
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

	public void setGraph(BufferedImage imgGenerated) {
		try {
			panelGraph.draw(imgGenerated, msiimage.getWidthMM(), msiimage.getHeightMM());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * ActionListener of the OK button.
	 * 
	 * @param event
	 *            the event data
	 */
	private void btnOK(ActionEvent event) {
		try {

			BufferedImage overlay = panelGraph.getTemplateOverlay();
			// Check Panel Graph Draw colour
			ICheckLetter isLetter = new IsLetterV2(overlay, PanelGraphDraw.LETTER_COLOR);
			setVisible(false);
			dispose();
			if (ok != null)
				ok.actionPerformed(event, isLetter);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Create a dialog that block UI interaction
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

	/**
	 * Set OK button listener
	 * 
	 * @param l
	 *            callback listener
	 */
	public void addOkListener(IOkListener l) {
		ok = l;
	}
}
