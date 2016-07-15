package com.constambeys.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;

import com.constambeys.load.MSIImage;
import com.constambeys.python.ICheckLetter;
import com.constambeys.python.IProgress;
import com.constambeys.python.IsLetterV2;
import com.constambeys.ui.graph.PanelGraph;
import com.constambeys.ui.graph.PanelGraphWithMarkers;

public class DialogTemplate extends JDialog implements PanelGraph.ImageClicked {
	enum State {
		NONE, GENERATED, TEMPLATE, BOTH
	}

	interface IOkListener {
		public void actionPerformed(ActionEvent e, ICheckLetter isLetter);
	}

	private JProgressBar progressBar;

	private IOkListener ok;
	private MSIImage msiimage;
	private State state = State.NONE;
	private PanelGraphWithMarkers panelGraph;

	private BufferedImage imgGenerated;
	private BufferedImage imgTemplate;
	private BufferedImage imgTemplateBW;
	private BufferedImage imgTemplateApha;

	private PanelGraphWithMarkers.Marker mTemplateP1 = new PanelGraphWithMarkers.Marker();
	private PanelGraphWithMarkers.Marker mTemplateP2 = new PanelGraphWithMarkers.Marker();
	private PanelGraphWithMarkers.Marker mGeneratedP1 = new PanelGraphWithMarkers.Marker();
	private PanelGraphWithMarkers.Marker mGeneratedP2 = new PanelGraphWithMarkers.Marker();

	private JFormattedTextField jtemplateP1;
	private JFormattedTextField jtemplateP2;
	private JFormattedTextField jgeneratedP1;
	private JFormattedTextField jgeneratedP2;
	private JFormattedTextField jTransparency;

	private JCheckBox jcheckEnable = new JCheckBox("Enable:");
	private JCheckBox jCheckTemplate = new JCheckBox("Template");
	private JCheckBox jCheckGenerated = new JCheckBox("Generated");
	private JCheckBox jCheckBlackWhite = new JCheckBox("Black & White");

	public DialogTemplate(Frame owner, boolean modal) throws Exception {
		super(owner, modal);
		init(Startup.loadMZML());
	}

	public DialogTemplate(MSIImage loadMZXML, Frame owner, boolean modal) throws Exception {
		super(owner, modal);
		init(loadMZXML);
	}

	public void init(MSIImage loadMZXML) throws ParseException {

		this.msiimage = loadMZXML;
		setTitle("JavaBall");
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

		panelGraph = new PanelGraphWithMarkers();
		panelGraph.addClickListener(this);
		background.add(BorderLayout.CENTER, panelGraph);

		Box boxSouth = new Box(BoxLayout.X_AXIS);
		background.add(BorderLayout.SOUTH, boxSouth);

		Box boxEast = new Box(BoxLayout.Y_AXIS);
		background.add(BorderLayout.EAST, boxEast);

		JButton btnLoad = new JButton("Template");
		boxSouth.add(btnLoad);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton btnGraph = new JButton("Graph");
		boxSouth.add(btnGraph);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton btnSaveGraph = new JButton("Save Graph");
		boxSouth.add(btnSaveGraph);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton btnSaveTemp = new JButton("Save Temp");
		boxSouth.add(btnSaveTemp);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		JButton buttonOK = new JButton("OK");
		// fills the empty gap
		boxSouth.add(Box.createHorizontalGlue());
		boxSouth.add(buttonOK);

		JLabel l;

		boxEast.add(jcheckEnable);

		l = new JLabel("Template P1 (x,y): ", JLabel.TRAILING);
		boxEast.add(l);
		jtemplateP1 = new JFormattedTextField();
		jtemplateP1.setText("0,0");
		l.setLabelFor(jtemplateP1);
		boxEast.add(jtemplateP1);

		l = new JLabel("Template P2 (x,y): ", JLabel.TRAILING);
		boxEast.add(l);
		jtemplateP2 = new JFormattedTextField();
		jtemplateP2.setText("0,0");
		l.setLabelFor(jtemplateP2);
		boxEast.add(jtemplateP2);

		l = new JLabel("Generated P1 (x,y): ", JLabel.TRAILING);
		boxEast.add(l);
		jgeneratedP1 = new JFormattedTextField();
		jgeneratedP1.setText("0,0");
		l.setLabelFor(jgeneratedP1);
		boxEast.add(jgeneratedP1);

		l = new JLabel("Generated P2 (x,y): ", JLabel.TRAILING);
		boxEast.add(l);
		jgeneratedP2 = new JFormattedTextField();
		jgeneratedP2.setText("0,0");
		l.setLabelFor(jgeneratedP2);
		boxEast.add(jgeneratedP2);

		boxEast.add(jCheckTemplate);
		boxEast.add(jCheckGenerated);
		boxEast.add(jCheckBlackWhite);

		l = new JLabel("Transparency: ", JLabel.TRAILING);
		NumberFormat format = NumberFormat.getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(0);
		formatter.setMaximum(255);
		formatter.setAllowsInvalid(true);
		// If you want the value to be committed on each keystroke instead of
		// focus lost
		formatter.setCommitsOnValidEdit(true);
		jTransparency = new JFormattedTextField(formatter);
		jTransparency.setText("100");
		l.setLabelFor(jTransparency);
		boxEast.add(l);
		boxEast.add(jTransparency);

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

		btnSaveGraph.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnSaveGraph();
			}
		});

		btnSaveTemp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnSaveTemp();
			}
		});

		jcheckEnable.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (jcheckEnable.isSelected()) {
					jenable(true);
				} else {
					jenable(false);
				}
			}
		});

		jCheckTemplate.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				syncUI();
			}
		});

		jCheckGenerated.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				syncUI();
			}

		});

		jCheckBlackWhite.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				syncUI();
			}
		});

		buttonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				btnOK(event);
			}
		});

		PropertyChangeListener callSyncUI = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// System.out.println(evt.getPropertyName());
				// System.out.println(evt.getNewValue());
				// System.out.println(evt.getNewValue().getClass());
				syncUI();
			}
		};

		jenable(false);
		jtemplateP1.addPropertyChangeListener("value", callSyncUI);
		jtemplateP2.addPropertyChangeListener("value", callSyncUI);
		jgeneratedP1.addPropertyChangeListener("value", callSyncUI);
		jgeneratedP2.addPropertyChangeListener("value", callSyncUI);
		jTransparency.addPropertyChangeListener("value", callSyncUI);
	}

	private void btnLoad() {

		try {
			File selectedFile;
			if (Startup.DEBUG) {
				selectedFile = new File("E:\\Enironments\\data\\abcdef.png");
			} else {

				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
				fileChooser.setFileFilter(imageFilter);
				int result = fileChooser.showOpenDialog(DialogTemplate.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					selectedFile = fileChooser.getSelectedFile();
				} else {
					return;
				}
			}

			imgTemplate = ImageIO.read(selectedFile);
			imgTemplateBW = loadBW();

			state = State.TEMPLATE;
			panelGraph.draw(imgTemplate, msiimage.getWidthMM(), msiimage.getHeightMM());

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private BufferedImage loadBW() {

		BufferedImage imgTemplateBW = new BufferedImage(imgTemplate.getWidth(), imgTemplate.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

		for (int y = 0; y < imgTemplateBW.getHeight(); y++) {
			for (int x = 0; x < imgTemplateBW.getWidth(); x++) {
				Color color = new Color(imgTemplate.getRGB(x, y));
				if (color.getRed() * 0.299f + color.getGreen() * 0.587f + color.getBlue() * 0.114f >= 128) {
					imgTemplateBW.setRGB(x, y, Color.WHITE.getRGB());
				} else {
					imgTemplateBW.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
		}

		return imgTemplateBW;
	}

	private void btnGraph() {
		try {
			if (msiimage == null) {
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

						JDialog wait = showWaitDialog();
						Thread t = new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									double[][] intensity = msiimage.getReduceSpec(lowerMass, higherMass, progressTracker);
									imgGenerated = panelGraph.calculateImage(intensity);
									state = State.GENERATED;
									panelGraph.draw(imgGenerated, msiimage.getWidthMM(), msiimage.getHeightMM());

								} catch (Exception ex) {
									JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
								} finally {
									updateUI(new Runnable() {

										@Override
										public void run() {
											wait.setVisible(false);
											wait.dispose();
										}
									});
								}
							}
						});
						t.start();
						wait.setVisible(true);

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

	private void btnSaveGraph() {
		try {
			if (imgGenerated != null) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showSaveDialog(DialogTemplate.this) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					if (!file.getName().endsWith(".png"))
						file = new File(file.getAbsolutePath() + ".png");
					// save to file
					ImageIO.write(imgGenerated, "PNG", file);
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnSaveTemp() {
		try {
			if (imgGenerated != null) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showSaveDialog(DialogTemplate.this) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					if (!file.getName().endsWith(".png"))
						file = new File(file.getAbsolutePath() + ".png");
					// save to file
					ImageIO.write(getTemplateOverlay(), "PNG", file);
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnOK(ActionEvent event) {
		try {
			if (imgGenerated == null) {
				JOptionPane.showMessageDialog(null, "Load a generated image", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (imgTemplateBW == null) {
				JOptionPane.showMessageDialog(null, "Load a template image", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			ICheckLetter isLetter = new IsLetterV2(getTemplateOverlay(), Color.BLACK);
			setVisible(false);
			dispose();
			if (ok != null)
				ok.actionPerformed(event, isLetter);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void syncUI() {
		try {
			int tempB = 0;
			int tempE = 0;
			int genB = 0;
			int genE = 0;

			if (imgTemplate != null) {
				tempB = Integer.parseInt(jtemplateP1.getText().split(",")[0]);
				tempE = Integer.parseInt(jtemplateP2.getText().split(",")[0]);
			}

			if (imgGenerated != null) {
				genB = Integer.parseInt(jgeneratedP1.getText().split(",")[0]);
				genE = Integer.parseInt(jgeneratedP2.getText().split(",")[0]);
			}

			if (imgGenerated != null && jCheckGenerated.isSelected() && ((imgTemplate != null && jCheckTemplate.isSelected()) || (imgTemplateBW != null && jCheckBlackWhite.isSelected()))) {
				state = State.BOTH;
				showMarkers(false, false);
				updateMarkersGen(genB, genE, tempB, tempE);
				updateMarkersTemp(genB, genE, tempB, tempE);

				if (jCheckTemplate.isSelected())
					setAlpha(imgTemplate, (Integer) jTransparency.getValue());
				else
					setAlpha(imgTemplateBW, (Integer) jTransparency.getValue());

				merge(genB, genE, tempB, tempE);

			} else {
				if (imgTemplate != null && jCheckTemplate.isSelected()) {
					jCheckBlackWhite.setSelected(false);
					state = State.TEMPLATE;
					showMarkers(true, false);
					updateMarkersTemp(genB, genE, tempB, tempE);
					panelGraph.draw(imgTemplate, msiimage.getWidthMM(), msiimage.getHeightMM());
				} else if (imgTemplateBW != null && jCheckBlackWhite.isSelected()) {
					jCheckTemplate.setSelected(false);
					state = State.TEMPLATE;
					showMarkers(true, false);
					updateMarkersTemp(genB, genE, tempB, tempE);
					panelGraph.draw(imgTemplateBW, msiimage.getWidthMM(), msiimage.getHeightMM());
				} else if (imgGenerated != null && jCheckGenerated.isSelected()) {
					state = State.GENERATED;
					showMarkers(false, true);
					updateMarkersGen(genB, genE, tempB, tempE);
					panelGraph.draw(imgGenerated, msiimage.getWidthMM(), msiimage.getHeightMM());
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void setAlpha(BufferedImage imgTemplate, int apha) {
		imgTemplateApha = new BufferedImage(imgTemplate.getWidth(), imgTemplate.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int cx = 0; cx < imgTemplate.getWidth(); cx++) {
			for (int cy = 0; cy < imgTemplate.getHeight(); cy++) {
				int color = imgTemplate.getRGB(cx, cy);
				Color c = new Color(color);
				Color n = new Color(c.getRed(), c.getGreen(), c.getBlue(), apha);
				imgTemplateApha.setRGB(cx, cy, n.getRGB());
			}
		}
	}

	// Merge imgGenerated and imgTemplateApha
	private void merge(int genB, int genE, int tempB, int tempE) throws IOException {

		double ratio = (double) (genE - genB) / (tempE - tempB);
		// Generated is offsetX pixels to the right
		int offsetX = (int) (genB - (tempB * ratio));

		// create the new image, canvas size is the max. of both image sizes
		int w = Math.max(imgGenerated.getWidth() - offsetX, (int) (imgTemplateApha.getWidth() * ratio));
		int h = Math.max(imgGenerated.getHeight(), imgTemplateApha.getHeight());

		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(imgGenerated, -offsetX, 0, imgGenerated.getWidth(), h, null);
		g.drawImage(imgTemplateApha, 0, 0, (int) (imgTemplateApha.getWidth() * ratio), h, null);
		g.dispose();

		// Save as new image
		// ImageIO.write(combined, "PNG", new File("combined.png"));
		panelGraph.draw(combined, msiimage.getWidthMM(), msiimage.getHeightMM());

	}

	private BufferedImage getTemplateOverlay() {
		int tempB = Integer.parseInt(jtemplateP1.getText().split(",")[0]);
		int tempE = Integer.parseInt(jtemplateP2.getText().split(",")[0]);
		int genB = Integer.parseInt(jgeneratedP1.getText().split(",")[0]);
		int genE = Integer.parseInt(jgeneratedP2.getText().split(",")[0]);

		double ratio = (double) (genE - genB) / (tempE - tempB);
		// Generated is offsetX pixels to the right
		int offsetX = (int) (genB - (tempB * ratio));

		// create the new image, canvas size is the max. of both image sizes
		int w = (int) (imgTemplateBW.getWidth() * ratio);
		int h = imgGenerated.getHeight();

		BufferedImage imgTemplateBWLowRes = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = imgTemplateBWLowRes.getGraphics();
		g.drawImage(imgTemplateBW, offsetX, 0, w, h, null);
		g.dispose();
		return imgTemplateBWLowRes;
	}

	private void updateMarkersGen(int genB, int genE, int tempB, int tempE) {
		mGeneratedP1.x = (double) genB / imgGenerated.getWidth();
		mGeneratedP2.x = (double) genE / imgGenerated.getWidth();
		panelGraph.repaint();
	}

	private void updateMarkersTemp(int genB, int genE, int tempB, int tempE) {
		mTemplateP1.x = (double) tempB / imgTemplate.getWidth();
		mTemplateP2.x = (double) tempE / imgTemplate.getWidth();
		panelGraph.repaint();
	}

	private void showMarkers(boolean temp, boolean gen) {
		mTemplateP1.setVisible(temp);
		mTemplateP2.setVisible(temp);
		mGeneratedP1.setVisible(gen);
		mGeneratedP2.setVisible(gen);
		panelGraph.repaint();
	}

	@Override
	public void imageClicked(PanelGraph.Point p) {
		if (!jcheckEnable.isSelected()) {
			return;
		}
		if (state == State.TEMPLATE) {
			if (jtemplateP1.hasFocus()) {
				PanelGraphWithMarkers.Marker m = new PanelGraphWithMarkers.Marker(p);
				panelGraph.removeMarker(mTemplateP1);
				panelGraph.addMarker(m);
				mTemplateP1 = m;
				jtemplateP1.setText(String.format("%d, %d", (int) (p.x * imgTemplate.getWidth()), (int) (p.y * imgTemplate.getHeight())));
			} else if (jtemplateP2.hasFocus()) {
				PanelGraphWithMarkers.Marker m = new PanelGraphWithMarkers.Marker(p);
				panelGraph.removeMarker(mTemplateP2);
				panelGraph.addMarker(m);
				mTemplateP2 = m;
				jtemplateP2.setText(String.format("%d, %d", (int) (p.x * imgTemplate.getWidth()), (int) (p.y * imgTemplate.getHeight())));
			}
		} else if (state == State.GENERATED) {
			if (jgeneratedP1.hasFocus()) {
				PanelGraphWithMarkers.Marker m = new PanelGraphWithMarkers.Marker(p);
				panelGraph.removeMarker(mGeneratedP1);
				panelGraph.addMarker(m);
				mGeneratedP1 = m;
				jgeneratedP1.setText(String.format("%d, %d", (int) (p.x * imgGenerated.getWidth()), (int) (p.y * imgGenerated.getHeight())));
			} else if (jgeneratedP2.hasFocus()) {
				PanelGraphWithMarkers.Marker m = new PanelGraphWithMarkers.Marker(p);
				panelGraph.removeMarker(mGeneratedP2);
				panelGraph.addMarker(m);
				mGeneratedP2 = m;
				jgeneratedP2.setText(String.format("%d, %d", (int) (p.x * imgGenerated.getWidth()), (int) (p.y * imgGenerated.getHeight())));
			}
		}
		panelGraph.repaint();
	}

	private JDialog showWaitDialog() {
		JDialog loading = new JDialog(this, true);
		loading.setLayout(new BorderLayout());
		loading.add(new JLabel("Please wait..."), BorderLayout.CENTER);
		loading.setLocationRelativeTo(this);
		loading.setUndecorated(true);
		loading.pack();
		return loading;
	}

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

	public void jenable(boolean enabled) {
		jtemplateP1.setEnabled(enabled);
		jtemplateP2.setEnabled(enabled);
		jgeneratedP1.setEnabled(enabled);
		jgeneratedP2.setEnabled(enabled);
	}

	public void addOkListener(IOkListener l) {
		ok = l;
	}
}
