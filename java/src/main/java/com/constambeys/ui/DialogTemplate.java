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
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;

import com.constambeys.python.ICheckLetter;
import com.constambeys.python.IsLetterV2;
import com.constambeys.python.LoadMZXML;
import com.constambeys.ui.graph.PanelGraph;
import com.constambeys.ui.graph.PanelGraphWithMarkers;

public class DialogTemplate extends JDialog implements PanelGraph.ImageClicked {
	enum State {
		NONE, GENERATED, TEMPLATE, BOTH
	}

	interface IOkListener {
		public void actionPerformed(ActionEvent e, ICheckLetter isLetter);
	}

	private IOkListener ok;
	private LoadMZXML loadMZXML;
	private State state = State.NONE;
	private PanelGraphWithMarkers panelGraph;

	private BufferedImage imgGenerated;
	private BufferedImage imgTemplate;
	private BufferedImage imgTemplateBW;
	private BufferedImage imgTemplateApha;

	private PanelGraphWithMarkers.Marker mTemplateB = new PanelGraphWithMarkers.Marker();
	private PanelGraphWithMarkers.Marker mTemplateE = new PanelGraphWithMarkers.Marker();
	private PanelGraphWithMarkers.Marker mGeneratedB = new PanelGraphWithMarkers.Marker();
	private PanelGraphWithMarkers.Marker mGeneratedE = new PanelGraphWithMarkers.Marker();

	private JFormattedTextField jtemplateB;
	private JFormattedTextField jtemplateE;
	private JFormattedTextField jgeneratedB;
	private JFormattedTextField jgeneratedE;
	private JFormattedTextField jTransparency;

	private JCheckBox jcheckEnable = new JCheckBox("Enable:");
	private JCheckBox jCheckTemplate = new JCheckBox("Template");
	private JCheckBox jCheckGenerated = new JCheckBox("Generated");
	private JCheckBox jCheckBlackWhite = new JCheckBox("Black & White");

	public DialogTemplate(Frame owner, boolean modal) throws Exception {
		super(owner, modal);
		init(Startup.loadMZML(null));
	}

	public DialogTemplate(LoadMZXML loadMZXML, Frame owner, boolean modal) throws Exception {
		super(owner, modal);
		init(loadMZXML);
	}

	public void init(LoadMZXML loadMZXML) throws ParseException {

		this.loadMZXML = loadMZXML;
		setTitle("JavaBall");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 400);

		JPanel background = new JPanel();
		setContentPane(background);

		background.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Border Layout North, South,East,West
		background.setLayout(new BorderLayout(0, 0));

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

		l = new JLabel("Template B (x,y): ", JLabel.TRAILING);
		boxEast.add(l);
		jtemplateB = new JFormattedTextField();
		jtemplateB.setText("0,0");
		l.setLabelFor(jtemplateB);
		boxEast.add(jtemplateB);

		l = new JLabel("Template E (x,y): ", JLabel.TRAILING);
		boxEast.add(l);
		jtemplateE = new JFormattedTextField();
		jtemplateE.setText("0,0");
		l.setLabelFor(jtemplateE);
		boxEast.add(jtemplateE);

		l = new JLabel("Generated B (x,y): ", JLabel.TRAILING);
		boxEast.add(l);
		jgeneratedB = new JFormattedTextField();
		jgeneratedB.setText("0,0");
		l.setLabelFor(jgeneratedB);
		boxEast.add(jgeneratedB);

		l = new JLabel("Generated E (x,y): ", JLabel.TRAILING);
		boxEast.add(l);
		jgeneratedE = new JFormattedTextField();
		jgeneratedE.setText("0,0");
		l.setLabelFor(jgeneratedE);
		boxEast.add(jgeneratedE);

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
		boxEast.add(l);
		l.setLabelFor(jTransparency);
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
		jtemplateB.addPropertyChangeListener("value", callSyncUI);
		jtemplateE.addPropertyChangeListener("value", callSyncUI);
		jgeneratedB.addPropertyChangeListener("value", callSyncUI);
		jgeneratedE.addPropertyChangeListener("value", callSyncUI);
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
			panelGraph.draw(imgTemplate, loadMZXML.getWidthMM(), loadMZXML.getHeightMM());

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
						imgGenerated = panelGraph.calculateImage(intensity);
						state = State.GENERATED;
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
				tempB = Integer.parseInt(jtemplateB.getText().split(",")[0]);
				tempE = Integer.parseInt(jtemplateE.getText().split(",")[0]);
			}

			if (imgGenerated != null) {
				genB = Integer.parseInt(jgeneratedB.getText().split(",")[0]);
				genE = Integer.parseInt(jgeneratedE.getText().split(",")[0]);
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
					panelGraph.draw(imgTemplate, loadMZXML.getWidthMM(), loadMZXML.getHeightMM());
				} else if (imgTemplateBW != null && jCheckBlackWhite.isSelected()) {
					jCheckTemplate.setSelected(false);
					state = State.TEMPLATE;
					showMarkers(true, false);
					updateMarkersTemp(genB, genE, tempB, tempE);
					panelGraph.draw(imgTemplateBW, loadMZXML.getWidthMM(), loadMZXML.getHeightMM());
				} else if (imgGenerated != null && jCheckGenerated.isSelected()) {
					state = State.GENERATED;
					showMarkers(false, true);
					updateMarkersGen(genB, genE, tempB, tempE);
					panelGraph.draw(imgGenerated, loadMZXML.getWidthMM(), loadMZXML.getHeightMM());
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
		panelGraph.draw(combined, loadMZXML.getWidthMM(), loadMZXML.getHeightMM());

	}

	private BufferedImage getTemplateOverlay() {
		int tempB = Integer.parseInt(jtemplateB.getText().split(",")[0]);
		int tempE = Integer.parseInt(jtemplateE.getText().split(",")[0]);
		int genB = Integer.parseInt(jgeneratedB.getText().split(",")[0]);
		int genE = Integer.parseInt(jgeneratedE.getText().split(",")[0]);

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
		mGeneratedB.x = (double) genB / imgGenerated.getWidth();
		mGeneratedE.x = (double) genE / imgGenerated.getWidth();
		panelGraph.repaint();
	}

	private void updateMarkersTemp(int genB, int genE, int tempB, int tempE) {
		mTemplateB.x = (double) tempB / imgTemplate.getWidth();
		mTemplateE.x = (double) tempE / imgTemplate.getWidth();
		panelGraph.repaint();
	}

	private void showMarkers(boolean temp, boolean gen) {
		mTemplateB.setVisible(temp);
		mTemplateE.setVisible(temp);
		mGeneratedB.setVisible(gen);
		mGeneratedE.setVisible(gen);
		panelGraph.repaint();
	}

	@Override
	public void imageClicked(PanelGraph.Point p) {
		if (!jcheckEnable.isSelected()) {
			return;
		}
		if (state == State.TEMPLATE) {
			if (jtemplateB.hasFocus()) {
				PanelGraphWithMarkers.Marker m = new PanelGraphWithMarkers.Marker(p);
				panelGraph.removeMarker(mTemplateB);
				panelGraph.addMarker(m);
				mTemplateB = m;
				jtemplateB.setText(String.format("%d, %d", (int) (p.x * imgTemplate.getWidth()), (int) (p.y * imgTemplate.getHeight())));
			} else if (jtemplateE.hasFocus()) {
				PanelGraphWithMarkers.Marker m = new PanelGraphWithMarkers.Marker(p);
				panelGraph.removeMarker(mTemplateE);
				panelGraph.addMarker(m);
				mTemplateE = m;
				jtemplateE.setText(String.format("%d, %d", (int) (p.x * imgTemplate.getWidth()), (int) (p.y * imgTemplate.getHeight())));
			}
		} else if (state == State.GENERATED) {
			if (jgeneratedB.hasFocus()) {
				PanelGraphWithMarkers.Marker m = new PanelGraphWithMarkers.Marker(p);
				panelGraph.removeMarker(mGeneratedB);
				panelGraph.addMarker(m);
				mGeneratedB = m;
				jgeneratedB.setText(String.format("%d, %d", (int) (p.x * imgGenerated.getWidth()), (int) (p.y * imgGenerated.getHeight())));
			} else if (jgeneratedE.hasFocus()) {
				PanelGraphWithMarkers.Marker m = new PanelGraphWithMarkers.Marker(p);
				panelGraph.removeMarker(mGeneratedE);
				panelGraph.addMarker(m);
				mGeneratedE = m;
				jgeneratedE.setText(String.format("%d, %d", (int) (p.x * imgGenerated.getWidth()), (int) (p.y * imgGenerated.getHeight())));
			}
		}
		panelGraph.repaint();
	}

	public void jenable(boolean enabled) {
		jtemplateB.setEnabled(enabled);
		jtemplateE.setEnabled(enabled);
		jgeneratedB.setEnabled(enabled);
		jgeneratedE.setEnabled(enabled);
	}

	public void addOkListener(IOkListener l) {
		ok = l;
	}
}
