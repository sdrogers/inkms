package com.constambeys.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;

import com.constambeys.python.ICheckLetter;
import com.constambeys.python.IProgress;
import com.constambeys.python.IsLetterV2;
import com.constambeys.readers.MSIImage;
import com.constambeys.ui.graph.PanelGraph;
import com.constambeys.ui.graph.PanelGraphWithMarkers;

/**
 * The {@code DialogOverlay} class allows the user to align the overlay image with the generated image
 * 
 * @author Constambeys
 * 
 */
public class DialogOverlay extends JDialog implements PanelGraph.ImageClicked {
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

	final static String helpMsg[] = { "Click Load to load an image", // 0
			"Click enable to enable the textboxes", // 1
			"Click Template P1 textbox", // 2
			"Select  point 1 on image", // 3
			"Click Template P2 textbox", // 4
			"Select point 2 on image", // 5
			"Uncheck Template checkbox", // 6
			"Check Generated checkbox", // 7
			"Click Generated P1 textbox", // 8
			"Select the corresponding point 1 on image", // 9
			"Click Generated P2 textbox", // 10
			"Select the corresponding point 2 on image", // 11
			"Check both template and generated checkboxes" // 12
	};

	static enum HELP {
		STARTUP(helpMsg[0]), LOAD(helpMsg[1]), ENABLE(helpMsg[2]), TEXT_TEMP_P1(helpMsg[3]), CLICK_TEMP_P1(helpMsg[4]), TEXT_TEMP_P2(helpMsg[5]), CLICK_TEMP_P2(helpMsg[6]), CHECK_NONE(helpMsg[7]), CHECK_GEN(helpMsg[8]), TEXT_GEN_P1(helpMsg[9]), CLICK_GEN_P1(helpMsg[10]), TEXT_GEN_P2(helpMsg[11]), CLICK_GEN_P2(helpMsg[12]), FINISH("");

		String name;

		private HELP(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	int helpIndex = 0;
	private JLabel jHelp;
	private PanelGraphWithMarkers panelGraph;

	private BufferedImage imgGenerated;
	private BufferedImage imgTemplate;
	private BufferedImage imgTemplateBW;
	private BufferedImage imgTemplateApha;

	private PanelGraphWithMarkers.Marker mTemplateP1 = new PanelGraphWithMarkers.Marker();
	private PanelGraphWithMarkers.Marker mTemplateP2 = new PanelGraphWithMarkers.Marker();
	private PanelGraphWithMarkers.Marker mGeneratedP1 = new PanelGraphWithMarkers.Marker();
	private PanelGraphWithMarkers.Marker mGeneratedP2 = new PanelGraphWithMarkers.Marker();

	private JButton btnSaveTemp;

	private JFormattedTextField jtemplateP1;
	private JFormattedTextField jtemplateP2;
	private JFormattedTextField jgeneratedP1;
	private JFormattedTextField jgeneratedP2;
	private JFormattedTextField jTransparency;

	private JCheckBox jcheckEnable = new JCheckBox("Enable:");
	private JCheckBox jCheckTemplate = new JCheckBox("Template");
	private JCheckBox jCheckGenerated = new JCheckBox("Generated");
	private JCheckBox jCheckBlackWhite = new JCheckBox("Black & White");

	/**
	 * Initialises a new user interface dialog
	 * 
	 * @param msiimage
	 *            the loaded mass spectrometry image
	 * @param owner
	 *            the Frame from which the dialog is displayed
	 * @param modal
	 *            specifies whether dialog blocks user input to other top-level windows when shown
	 * @throws Exception
	 */
	public DialogOverlay(MSIImage msiimage, Frame owner, boolean modal) throws Exception {
		super(owner, modal);
		setupUI(msiimage);
	}

	/**
	 * Initialises the UI elements
	 * 
	 */
	public void setupUI(MSIImage msiimage) throws ParseException {

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

		Box boxCenter = new Box(BoxLayout.Y_AXIS);
		background.add(BorderLayout.CENTER, boxCenter);

		jHelp = new JLabel("", JLabel.TRAILING);
		boxCenter.add(jHelp);
		showHelp(HELP.STARTUP);
		panelGraph = new PanelGraphWithMarkers();
		panelGraph.addClickListener(this);
		boxCenter.add(panelGraph);

		Box boxSouth = new Box(BoxLayout.X_AXIS);
		background.add(BorderLayout.SOUTH, boxSouth);

		Box boxEast = new Box(BoxLayout.Y_AXIS);
		background.add(BorderLayout.EAST, boxEast);

		JButton btnLoad = new JButton("Load");
		boxSouth.add(btnLoad);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		btnSaveTemp = new JButton("Save Temp");
		btnSaveTemp.setEnabled(false);
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
					showHelp(HELP.ENABLE);
					jTextEnable(true);
				} else {
					jTextEnable(false);
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

		FocusListener focus = new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void focusGained(FocusEvent e) {
				if (e.getSource() == jtemplateP1)
					showHelp(HELP.TEXT_TEMP_P1);
				else if (e.getSource() == jtemplateP2)
					showHelp(HELP.TEXT_TEMP_P2);
				else if (e.getSource() == jgeneratedP1)
					showHelp(HELP.TEXT_GEN_P1);
				else if (e.getSource() == jgeneratedP2)
					showHelp(HELP.TEXT_GEN_P2);
			}
		};

		jtemplateP1.addFocusListener(focus);
		jtemplateP2.addFocusListener(focus);
		jgeneratedP1.addFocusListener(focus);
		jgeneratedP2.addFocusListener(focus);

		PropertyChangeListener callSyncUI = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// System.out.println(evt.getPropertyName());
				// System.out.println(evt.getNewValue());
				// System.out.println(evt.getNewValue().getClass());
				syncUI();
			}
		};

		jTextEnable(false);
		jtemplateP1.addPropertyChangeListener("value", callSyncUI);
		jtemplateP2.addPropertyChangeListener("value", callSyncUI);
		jgeneratedP1.addPropertyChangeListener("value", callSyncUI);
		jgeneratedP2.addPropertyChangeListener("value", callSyncUI);
		jTransparency.addPropertyChangeListener("value", callSyncUI);
	}

	/**
	 * Sets the generated image
	 * 
	 * @param imgGenerated
	 */
	public void setGraph(BufferedImage imgGenerated) {
		try {
			this.imgGenerated = imgGenerated;
			panelGraph.draw(imgGenerated, msiimage.getWidthMM(), msiimage.getHeightMM());
			state = State.GENERATED;
			jCheckTemplate.setSelected(false);
			jCheckGenerated.setSelected(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * ActionListener of the Template button.
	 * 
	 */
	private void btnLoad() {

		try {
			File selectedFile;

			Settings settings = new Settings();
			JFileChooser fileChooser = new JFileChooser();
			FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
			fileChooser.setFileFilter(imageFilter);
			if (settings.get("load_dir") != null) {
				fileChooser.setCurrentDirectory(new File(settings.get("load_dir")));
			}
			int result = fileChooser.showOpenDialog(DialogOverlay.this);
			if (result == JFileChooser.APPROVE_OPTION) {
				selectedFile = fileChooser.getSelectedFile();
			} else {
				return;
			}

			imgTemplate = ImageIO.read(selectedFile);
			imgTemplateBW = convertBlackWhite(imgTemplate);
			panelGraph.draw(imgTemplate, msiimage.getWidthMM(), msiimage.getHeightMM());
			state = State.TEMPLATE;
			jCheckGenerated.setSelected(false);
			jCheckTemplate.setSelected(true);
			btnSaveTemp.setEnabled(true);
			showHelp(HELP.LOAD);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 *
	 * Convert to Black and White image
	 *
	 * @param original
	 *            image
	 * @return black and white {@code BufferedImage}
	 */
	private static BufferedImage convertBlackWhite(BufferedImage original) {

		BufferedImage imgBlackWhite = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

		for (int y = 0; y < imgBlackWhite.getHeight(); y++) {
			for (int x = 0; x < imgBlackWhite.getWidth(); x++) {
				Color color = new Color(original.getRGB(x, y));
				if (color.getRed() * 0.299f + color.getGreen() * 0.587f + color.getBlue() * 0.114f >= 128) {
					imgBlackWhite.setRGB(x, y, Color.WHITE.getRGB());
				} else {
					imgBlackWhite.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
		}

		return imgBlackWhite;
	}

	/**
	 * ActionListener of the Save Template button.
	 * 
	 */
	private void btnSaveTemp() {
		try {
			if (imgGenerated != null) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showSaveDialog(DialogOverlay.this) == JFileChooser.APPROVE_OPTION) {
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

	/**
	 * ActionListener of the OK button.
	 * 
	 */
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

			ICheckLetter isLetter = new IsLetterV2(getTemplateOverlay(), Color.BLACK.getRGB());
			setVisible(false);
			dispose();
			if (ok != null)
				ok.actionPerformed(event, isLetter);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Initialises the UI elements
	 * 
	 */
	private void syncUI() {
		try {
			Point t1 = null;
			Point t2 = null;
			Point g1 = null;
			Point g2 = null;

			if (imgTemplate != null) {
				String[] p1 = jtemplateP1.getText().split(",");
				t1 = new Point(Integer.parseInt(p1[0]), Integer.parseInt(p1[1].trim()));
				String[] p2 = jtemplateP2.getText().split(",");
				t2 = new Point(Integer.parseInt(p2[0]), Integer.parseInt(p2[1].trim()));
			}

			if (imgGenerated != null) {
				String[] p1 = jgeneratedP1.getText().split(",");
				g1 = new Point(Integer.parseInt(p1[0]), Integer.parseInt(p1[1].trim()));
				String[] p2 = jgeneratedP2.getText().split(",");
				g2 = new Point(Integer.parseInt(p2[0]), Integer.parseInt(p2[1].trim()));
			}

			if (imgGenerated != null && jCheckGenerated.isSelected() && ((imgTemplate != null && jCheckTemplate.isSelected()) || (imgTemplateBW != null && jCheckBlackWhite.isSelected()))) {
				state = State.BOTH;
				showMarkers(false, false);
				updateMarkersGen(g1, g2);
				updateMarkersTemp(t1, t2);

				if (jCheckTemplate.isSelected())
					imgTemplateApha = setAlpha(imgTemplate, (Integer) jTransparency.getValue());
				else
					imgTemplateApha = setAlpha(imgTemplateBW, (Integer) jTransparency.getValue());

				merge(g1, g2, t1, t2);
				showHelp(HELP.FINISH);
			} else {
				if (imgTemplate != null && jCheckTemplate.isSelected()) {
					jCheckBlackWhite.setSelected(false);
					state = State.TEMPLATE;
					showMarkers(true, false);
					updateMarkersTemp(t1, t2);
					panelGraph.draw(imgTemplate, msiimage.getWidthMM(), msiimage.getHeightMM());
				} else if (imgTemplateBW != null && jCheckBlackWhite.isSelected()) {
					jCheckTemplate.setSelected(false);
					state = State.TEMPLATE;
					showMarkers(true, false);
					updateMarkersTemp(t1, t2);
					panelGraph.draw(imgTemplateBW, msiimage.getWidthMM(), msiimage.getHeightMM());
				} else if (imgGenerated != null && jCheckGenerated.isSelected()) {
					state = State.GENERATED;
					showMarkers(false, true);
					updateMarkersGen(g1, g2);
					panelGraph.draw(imgGenerated, msiimage.getWidthMM(), msiimage.getHeightMM());
					showHelp(HELP.CHECK_GEN);
				} else {
					showHelp(HELP.CHECK_NONE);
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Change image alpha value
	 * 
	 * @param original
	 *            image
	 * @param apha
	 *            the alpha component in the range (0-255)
	 * @return
	 */
	private static BufferedImage setAlpha(BufferedImage original, int apha) {
		BufferedImage imgWithApha = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int cx = 0; cx < original.getWidth(); cx++) {
			for (int cy = 0; cy < original.getHeight(); cy++) {
				int color = original.getRGB(cx, cy);
				Color c = new Color(color);
				Color n = new Color(c.getRed(), c.getGreen(), c.getBlue(), apha);
				imgWithApha.setRGB(cx, cy, n.getRGB());
			}
		}
		return imgWithApha;
	}

	/**
	 * Merge imgGenerated and imgTemplateApha
	 * 
	 * @param g1
	 *            generated point 1
	 * @param g2
	 *            generated point 2
	 * @param t1
	 *            template point 1
	 * @param t2
	 *            template point 2
	 * @throws IOException
	 */
	private void merge(Point g1, Point g2, Point t1, Point t2) throws IOException {

		double ratioX = (double) (g2.x - g1.x) / (t2.x - t1.x);
		double ratioY = (double) (g2.y - g1.y) / (t2.y - t1.y);
		// Generated is offsetX pixels to the right
		int offsetX = (int) (g1.x - (t1.x * ratioX));
		int offsetY = (int) (g1.y - (t1.y * ratioY));

		// create the new image, canvas size is the max. of both image sizes
		int w = Math.max(imgGenerated.getWidth() - offsetX, (int) (imgTemplateApha.getWidth() * ratioX));
		int h = Math.max(imgGenerated.getHeight() - offsetY, (int) (imgTemplateApha.getHeight() * ratioY));

		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(imgGenerated, -offsetX, -offsetY, imgGenerated.getWidth(), imgGenerated.getHeight(), null);
		g.drawImage(imgTemplateApha, 0, 0, (int) (imgTemplateApha.getWidth() * ratioX), (int) (imgTemplateApha.getHeight() * ratioY), null);
		g.dispose();

		// Save as new image
		// ImageIO.write(combined, "PNG", new File("combined.png"));
		panelGraph.draw(combined, msiimage.getWidthMM(), msiimage.getHeightMM());

	}

	/**
	 * @return the final template image aligned with the generated image
	 */
	private BufferedImage getTemplateOverlay() {
		String[] temp;
		temp = jtemplateP1.getText().split(",");
		Point t1 = new Point(Integer.parseInt(temp[0]), Integer.parseInt(temp[1].trim()));
		temp = jtemplateP2.getText().split(",");
		Point t2 = new Point(Integer.parseInt(temp[0]), Integer.parseInt(temp[1].trim()));

		temp = jgeneratedP1.getText().split(",");
		Point g1 = new Point(Integer.parseInt(temp[0]), Integer.parseInt(temp[1].trim()));
		temp = jgeneratedP2.getText().split(",");
		Point g2 = new Point(Integer.parseInt(temp[0]), Integer.parseInt(temp[1].trim()));

		double ratioX = (double) (g2.x - g1.x) / (t2.x - t1.x);
		double ratioY = (double) (g2.y - g1.y) / (t2.y - t1.y);
		// Generated is offsetX pixels to the right
		int offsetX = (int) (g1.x - (t1.x * ratioX));
		int offsetY = (int) (g1.y - (t1.y * ratioY));

		int w = (int) (imgTemplateBW.getWidth() * ratioX);
		int h = (int) (imgTemplateBW.getHeight() * ratioY);

		BufferedImage imgTemplateBWLowRes = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = imgTemplateBWLowRes.getGraphics();
		g.drawImage(imgTemplateBW, offsetX, offsetY, w, h, null);
		g.dispose();
		return imgTemplateBWLowRes;
	}

	/**
	 * Update generated image marker coordinates
	 * 
	 * @param g1
	 *            the point 1
	 * @param g2
	 *            the point 2
	 */
	private void updateMarkersGen(Point g1, Point g2) {
		mGeneratedP1.x_ratio = (double) g1.x / imgGenerated.getWidth();
		mGeneratedP1.y_ratio = (double) g1.y / imgGenerated.getHeight();
		mGeneratedP2.x_ratio = (double) g2.x / imgGenerated.getWidth();
		mGeneratedP2.y_ratio = (double) g2.y / imgGenerated.getHeight();
		panelGraph.repaint();
	}

	/**
	 * Update template image marker coordinates
	 * 
	 * @param t1
	 *            the point 1
	 * @param t2
	 *            the point 2
	 */
	private void updateMarkersTemp(Point t1, Point t2) {
		mTemplateP1.x_ratio = (double) t1.x / imgTemplate.getWidth();
		mTemplateP1.y_ratio = (double) t1.y / imgTemplate.getHeight();
		mTemplateP2.x_ratio = (double) t2.x / imgTemplate.getWidth();
		mTemplateP2.y_ratio = (double) t2.y / imgTemplate.getHeight();
		panelGraph.repaint();
	}

	/**
	 * Show or hide markers
	 * 
	 * @param temp
	 * @param gen
	 */
	private void showMarkers(boolean temp, boolean gen) {
		mTemplateP1.setVisible(temp);
		mTemplateP2.setVisible(temp);
		mGeneratedP1.setVisible(gen);
		mGeneratedP2.setVisible(gen);
		panelGraph.repaint();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.constambeys.ui.graph.PanelGraph.ImageClicked#imageClicked(com.constambeys.ui.graph.PanelGraph.Point)
	 */
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
				jtemplateP1.setText(String.format("%d, %d", (int) (p.x_ratio * imgTemplate.getWidth()), (int) (p.y_ratio * imgTemplate.getHeight())));
				showHelp(HELP.CLICK_TEMP_P1);
			} else if (jtemplateP2.hasFocus()) {
				PanelGraphWithMarkers.Marker m = new PanelGraphWithMarkers.Marker(p);
				panelGraph.removeMarker(mTemplateP2);
				panelGraph.addMarker(m);
				mTemplateP2 = m;
				jtemplateP2.setText(String.format("%d, %d", (int) (p.x_ratio * imgTemplate.getWidth()), (int) (p.y_ratio * imgTemplate.getHeight())));
				showHelp(HELP.CLICK_TEMP_P2);
			}
		} else if (state == State.GENERATED) {
			if (jgeneratedP1.hasFocus()) {
				PanelGraphWithMarkers.Marker m = new PanelGraphWithMarkers.Marker(p);
				panelGraph.removeMarker(mGeneratedP1);
				panelGraph.addMarker(m);
				mGeneratedP1 = m;
				jgeneratedP1.setText(String.format("%d, %d", (int) (p.x_ratio * imgGenerated.getWidth()), (int) (p.y_ratio * imgGenerated.getHeight())));
				showHelp(HELP.CLICK_GEN_P1);
			} else if (jgeneratedP2.hasFocus()) {
				PanelGraphWithMarkers.Marker m = new PanelGraphWithMarkers.Marker(p);
				panelGraph.removeMarker(mGeneratedP2);
				panelGraph.addMarker(m);
				mGeneratedP2 = m;
				jgeneratedP2.setText(String.format("%d, %d", (int) (p.x_ratio * imgGenerated.getWidth()), (int) (p.y_ratio * imgGenerated.getHeight())));
				showHelp(HELP.CLICK_GEN_P2);
			}
		}
		panelGraph.repaint();
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
	 * Enable or disable {@code JFormattedTextField}
	 * 
	 * @param enabled
	 */
	public void jTextEnable(boolean enabled) {
		jtemplateP1.setEnabled(enabled);
		jtemplateP2.setEnabled(enabled);
		jgeneratedP1.setEnabled(enabled);
		jgeneratedP2.setEnabled(enabled);
	}

	/**
	 * Set OK button listener
	 * 
	 * @param l
	 *            callback listener
	 */
	public void addOkListener(IOkListener l) {
		ok = l;
	}

	/**
	 * Displays a help message below the title
	 * 
	 * @param index
	 *            of the help message
	 */
	private void showHelp(HELP help) {
		if (helpIndex == HELP.FINISH.ordinal()) {
			jHelp.setVisible(false);
		} else if (helpIndex == help.ordinal()) {
			jHelp.setText(help.toString());
			helpIndex++;
		}
	}
}
