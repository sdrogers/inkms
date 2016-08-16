package com.constambeys.ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import com.constambeys.python.IProgress;
import com.constambeys.readers.MSIImage;
import com.constambeys.readers.Spectrum;
import com.constambeys.ui.graph.PanelGraphDraw;
import com.constambeys.ui.graph.PanelVlines;
import com.constambeys.ui.graph.PanelVlines.TooFewDataException;
import com.constambeys.ui.graph.Utilities;

public class FrameSpectrum extends JFrame {

	private SyncUI s0;
	private Thread t0;
	private SyncUI s1;
	private Thread t1;
	private JProgressBar progressBar;
	private ProgressTracker p0;
	private ProgressTracker p1;

	private JButton buttonUp;
	private JButton buttonDown;
	private JButton buttonClose;

	private JRadioButton radInterestedRegion;
	private JRadioButton radReferenceRegion;
	private JRadioButton radDraw;
	private JRadioButton radErase;
	private JFormattedTextField jRectSize;

	private MSIImage msiimage;
	private BufferedImage interestedOverlay;
	private BufferedImage referenceOverlay;
	private PanelGraphDraw panelGraphInterestedRegion;
	private PanelGraphDraw panelGraphReferenceRegion;
	private PanelVlines panelVlines;

	public FrameSpectrum(MSIImage msiimage) throws IOException {
		super();
		setupUI(msiimage);
	}

	private void setupUI(MSIImage msiimage) throws IOException {
		this.msiimage = msiimage;
		setTitle("MSI");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
		p0 = new ProgressTracker();
		p1 = new ProgressTracker();

		Box boxCenter = new Box(BoxLayout.Y_AXIS);
		background.add(BorderLayout.CENTER, boxCenter);

		Box boxSouth = new Box(BoxLayout.X_AXIS);
		background.add(BorderLayout.SOUTH, boxSouth);

		Box boxEast = createSettings();
		background.add(BorderLayout.EAST, boxEast);

		panelGraphInterestedRegion = new PanelGraphDraw();
		boxCenter.add(panelGraphInterestedRegion);

		panelGraphReferenceRegion = new PanelGraphDraw();
		panelGraphReferenceRegion.setVisible(false);

		boxCenter.add(panelGraphReferenceRegion);
		panelVlines = new PanelVlines();
		boxCenter.add(panelVlines);

		buttonClose = new JButton("OK");
		// fills the empty gap
		boxSouth.add(Box.createHorizontalGlue());
		boxSouth.add(buttonClose);

		addListeners();
	}

	private Box createSettings() throws IOException {

		Box box1 = new Box(BoxLayout.Y_AXIS);
		box1.setBorder(BorderFactory.createTitledBorder("Spectrum:"));
		radInterestedRegion = new JRadioButton("Interested Region", true);
		radReferenceRegion = new JRadioButton("Reference Region");
		ButtonGroup group1 = new ButtonGroup();
		group1.add(radInterestedRegion);
		group1.add(radReferenceRegion);
		box1.add(radInterestedRegion);
		box1.add(radReferenceRegion);

		Box box2 = new Box(BoxLayout.Y_AXIS);
		box2.setBorder(BorderFactory.createTitledBorder("Controls:"));
		radDraw = new JRadioButton("Draw", true);
		radErase = new JRadioButton("Erase");
		ButtonGroup group2 = new ButtonGroup();
		group2.add(radDraw);
		group2.add(radErase);
		box2.add(radDraw);
		box2.add(radErase);

		buttonUp = new JButton(Startup.loadIcon("up128.png", 30, 30));
		// http://stackoverflow.com/questions/4898584/java-using-an-image-as-a-button
		buttonUp.setBorder(BorderFactory.createEmptyBorder());
		buttonUp.setContentAreaFilled(false);
		box2.add(Box.createRigidArea(new Dimension(0, 10)));
		box2.add(buttonUp);

		buttonDown = new JButton(Startup.loadIcon("down128.png", 30, 30));
		// http://stackoverflow.com/questions/4898584/java-using-an-image-as-a-button
		buttonDown.setBorder(BorderFactory.createEmptyBorder());
		buttonDown.setContentAreaFilled(false);
		box2.add(buttonDown);

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
		box2.add(l);
		box2.add(jRectSize);

		Box main = new Box(BoxLayout.Y_AXIS);
		main.add(box1);
		main.add(Box.createRigidArea(new Dimension(0, 10)));
		main.add(box2);

		return main;
	}

	private void addListeners() {

		radInterestedRegion.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				panelGraphInterestedRegion.setVisible(e.getStateChange() == ItemEvent.SELECTED);
				panelGraphReferenceRegion.setVisible(e.getStateChange() != ItemEvent.SELECTED);
				p0.showProgress(e.getStateChange() == ItemEvent.SELECTED);
				p1.showProgress(e.getStateChange() != ItemEvent.SELECTED);
			}
		});

		radDraw.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				panelGraphInterestedRegion.setIsDrawingState(e.getStateChange() == ItemEvent.SELECTED);
				panelGraphReferenceRegion.setIsDrawingState(e.getStateChange() == ItemEvent.SELECTED);
			}
		});

		buttonClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				// btnOK(event);
				setVisible(false);
				dispose();
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

		PropertyChangeListener callSyncUI = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				panelGraphInterestedRegion.setDrawingDimension((int) evt.getNewValue());
				panelGraphReferenceRegion.setDrawingDimension((int) evt.getNewValue());
			}
		};

		jRectSize.addPropertyChangeListener("value", callSyncUI);

		panelGraphInterestedRegion.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (t0 != null) {
					s0.setRestart();
					if (!t0.isAlive()) {
						s0 = new SyncUI(true, p0);
						t0 = new Thread(s0);
						t0.start();
					}
				} else {
					s0 = new SyncUI(true, p0);
					t0 = new Thread(s0);
					t0.start();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (t0 != null) {
					s0.setRestart();
					if (!t0.isAlive()) {
						s0 = new SyncUI(true, p0);
						t0 = new Thread(s0);
						t0.start();
					}
				} else {
					s0 = new SyncUI(true, p0);
					t0 = new Thread(s0);
					t0.start();
				}
			}
		});

		panelGraphReferenceRegion.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (t1 != null) {
					s1.setRestart();
					if (!t1.isAlive()) {
						s1 = new SyncUI(false, p1);
						t1 = new Thread(s1);
						t1.start();
					}
				} else {
					s1 = new SyncUI(false, p1);
					t1 = new Thread(s1);
					t1.start();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (t1 != null) {
					s1.setRestart();
					if (!t1.isAlive()) {
						s1 = new SyncUI(false, p1);
						t1 = new Thread(s1);
						t1.start();
					}
				} else {
					s1 = new SyncUI(false, p1);
					t1 = new Thread(s1);
					t1.start();
				}
			}
		});

	}

	/**
	 * Sets the generated image and the window title
	 * 
	 * @param imgGenerated
	 */
	public void setGraph(String title, BufferedImage imgGenerated) {
		try {
			setTitle(title);
			panelGraphInterestedRegion.setGraphTitle(title);
			panelGraphInterestedRegion.draw(imgGenerated, msiimage.getWidthMM(), msiimage.getHeightMM());

			panelGraphReferenceRegion.setGraphTitle(title);
			panelGraphReferenceRegion.draw(imgGenerated, msiimage.getWidthMM(), msiimage.getHeightMM());

			interestedOverlay = Utilities.copyImage(panelGraphReferenceRegion.getTemplateOverlay());
			referenceOverlay = Utilities.copyImage(panelGraphReferenceRegion.getTemplateOverlay());

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

	class ProgressTracker implements IProgress {

		boolean showProgress = false;
		int lastvalue;

		public void showProgress(boolean v) {
			showProgress = v;
			if (showProgress) {
				update(lastvalue);
			}
		}

		@Override
		public void update(int value) {
			lastvalue = value;
			if (showProgress) {
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
		}
	};

	class SyncUI implements Runnable {

		private volatile boolean restart = false;
		private boolean isInterestedRegion;
		private IProgress progressTracker;

		SyncUI(boolean isInterestedRegion, IProgress progressTracker) {
			this.isInterestedRegion = isInterestedRegion;
			this.progressTracker = progressTracker;
		}

		private synchronized boolean resetRestart() {
			boolean restart_ = restart;
			restart = false;
			return restart_;
		}

		public synchronized void setRestart() {
			this.restart = true;
		}

		@Override
		public void run() {
			try {
				do {
					boolean drawing;

					if (isInterestedRegion) {
						drawing = panelGraphInterestedRegion.getIsDrawingState();
					} else {
						drawing = panelGraphReferenceRegion.getIsDrawingState();
					}

					if (drawing) {
						if (isInterestedRegion) {
							interestedOverlay = addSpectrumHelp(panelGraphInterestedRegion, interestedOverlay, true);
						} else {
							referenceOverlay = addSpectrumHelp(panelGraphReferenceRegion, referenceOverlay, false);
						}
					} else {
						panelVlines.clear();
						Graphics2D g0 = interestedOverlay.createGraphics();
						g0.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
						g0.fillRect(0, 0, interestedOverlay.getWidth(), interestedOverlay.getHeight());
						g0.dispose();

						Graphics2D g1 = referenceOverlay.createGraphics();
						g1.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
						g1.fillRect(0, 0, referenceOverlay.getWidth(), referenceOverlay.getHeight());
						g1.dispose();

						interestedOverlay = addSpectrumHelp(panelGraphInterestedRegion, interestedOverlay, true);
						referenceOverlay = addSpectrumHelp(panelGraphReferenceRegion, referenceOverlay, false);
					}
				} while (resetRestart());

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}

		}

		private BufferedImage addSpectrumHelp(PanelGraphDraw panel, BufferedImage imageOverlayOld, boolean isBlue) throws Exception {

			BufferedImage imageOverlayNew = Utilities.copyImage(panel.getTemplateOverlay());

			for (int x = 0; x < imageOverlayNew.getWidth(); x++) {
				if (progressTracker != null)
					progressTracker.update((int) ((float) x / interestedOverlay.getWidth() * 100));

				for (int y = 0; y < imageOverlayNew.getHeight(); y++) {
					int newValue = imageOverlayNew.getRGB(x, y);
					int oldValue = imageOverlayOld.getRGB(x, y);

					// Add only new pixels in drawing mode
					if (newValue != oldValue) {
						Spectrum spectrum = msiimage.getSpectrum(y, x);

						for (int s = 0; s < spectrum.mzs.length; s++) {
							double mz = spectrum.mzs[s];
							double i = spectrum.ints[s];
							panelVlines.add(mz, i, isBlue);
						}
					}
				}
			}

			try {
				panelVlines.addCommit();
			} catch (TooFewDataException ex) {
				System.err.println("Too few data exception by spectrum plot");
			}

			if (progressTracker != null)
				progressTracker.update(100);

			return imageOverlayNew;
		}
	};
}
