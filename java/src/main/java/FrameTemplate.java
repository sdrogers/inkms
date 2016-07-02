import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import pyhton.LoadMZXML;

public class FrameTemplate extends JFrame implements PanelGraph.ImageListener {
	enum State {
		NONE, GENERATED, TEMPLATE, BOTH
	}

	private LoadMZXML loadMZXML;
	private State state = State.NONE;
	private PanelGraphPaint panelGraph;

	private BufferedImage imgGenerated;
	private BufferedImage imgTemplate;
	private BufferedImage imgTemplateApha;
	private int alpha;

	private PanelGraphPaint.Marker mTemplateB = new PanelGraphPaint.Marker();
	private PanelGraphPaint.Marker mTemplateE = new PanelGraphPaint.Marker();
	private PanelGraphPaint.Marker mGeneratedB = new PanelGraphPaint.Marker();
	private PanelGraphPaint.Marker mGeneratedE = new PanelGraphPaint.Marker();

	private JCheckBox jcheckEnable = new JCheckBox("Enable:");
	private JTextField jtemplateB = new JTextField(10);
	private JTextField jtemplateE = new JTextField(10);
	private JTextField jgeneratedB = new JTextField(10);
	private JTextField jgeneratedE = new JTextField(10);

	private JFormattedTextField jTransparency;
	private JCheckBox jCheckTemplate = new JCheckBox("Template");
	private JCheckBox jCheckGenerated = new JCheckBox("Generated");

	public FrameTemplate() throws Exception {
		init(Startup.loadMZML(null));
	}

	public FrameTemplate(LoadMZXML loadMZXML) throws Exception {
		init(loadMZXML);
	}

	public void init(LoadMZXML loadMZXML) {

		this.loadMZXML = loadMZXML;
		setTitle("JavaBall");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 300);

		JPanel background = new JPanel();
		setContentPane(background);

		background.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Border Layout North, South,East,West
		background.setLayout(new BorderLayout(0, 0));

		panelGraph = new PanelGraphPaint();
		panelGraph.addListener(this);
		background.add(BorderLayout.CENTER, panelGraph);

		Box boxSouth = new Box(BoxLayout.X_AXIS);
		background.add(BorderLayout.SOUTH, boxSouth);

		Box boxEast = new Box(BoxLayout.Y_AXIS);
		background.add(BorderLayout.EAST, boxEast);

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

		JLabel l;

		jcheckEnable.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				syncMarkers();
			}
		});
		boxEast.add(jcheckEnable);

		l = new JLabel("Template B: ", JLabel.TRAILING);
		boxEast.add(l);
		l.setLabelFor(jtemplateB);
		boxEast.add(jtemplateB);

		l = new JLabel("Template E: ", JLabel.TRAILING);
		boxEast.add(l);
		l.setLabelFor(jtemplateE);
		boxEast.add(jtemplateE);

		l = new JLabel("Generated B: ", JLabel.TRAILING);
		boxEast.add(l);
		l.setLabelFor(jgeneratedB);
		boxEast.add(jgeneratedB);

		l = new JLabel("Generated E: ", JLabel.TRAILING);
		boxEast.add(l);
		l.setLabelFor(jgeneratedE);
		boxEast.add(jgeneratedE);

		jCheckTemplate.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				syncUI();
			}
		});
		boxEast.add(jCheckTemplate);

		jCheckGenerated.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				syncUI();
			}

		});
		boxEast.add(jCheckGenerated);

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

		jTransparency.addPropertyChangeListener("value", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// System.out.println(evt.getPropertyName());
				// System.out.println(evt.getNewValue());
				// System.out.println(evt.getNewValue().getClass());
				syncUI();
			}
		});

		boxEast.add(l);
		l.setLabelFor(jTransparency);
		boxEast.add(jTransparency);

	}

	private void btnLoad() {

		try {
			File selectedFile;
			if (Startup.DEBUG) {
				selectedFile = new File("E:\\Enironments\\data\\abcdef-AF.png");
			} else {

				JFileChooser fileChooser = new JFileChooser();
				int result = fileChooser.showOpenDialog(FrameTemplate.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					selectedFile = fileChooser.getSelectedFile();
				} else {
					return;
				}
			}

			imgTemplate = ImageIO.read(selectedFile);
			state = State.TEMPLATE;
			panelGraph.draw(imgTemplate, loadMZXML.getWidthMM(), loadMZXML.getHeightMM());

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}

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
						imgGenerated = panelGraph.getImage(intensity);
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

	private void syncUI() {
		try {
			if (imgTemplate != null && imgGenerated != null && jCheckTemplate.isSelected()
					&& jCheckGenerated.isSelected()) {
				state = State.BOTH;
				syncMarkers();

				Integer newvalue = (Integer) jTransparency.getValue();
				if (alpha != newvalue) {
					setAlpha(newvalue);
					alpha = newvalue;
				}
				merge();

			} else if (imgTemplate != null && jCheckTemplate.isSelected()) {
				state = State.TEMPLATE;
				syncMarkers();
				panelGraph.draw(imgTemplate, loadMZXML.getWidthMM(), loadMZXML.getHeightMM());
			} else if (imgGenerated != null && jCheckGenerated.isSelected()) {
				state = State.GENERATED;
				syncMarkers();
				panelGraph.draw(imgGenerated, loadMZXML.getWidthMM(), loadMZXML.getHeightMM());
			}
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void setAlpha(int apha) {
		imgTemplateApha = new BufferedImage(imgTemplate.getWidth(), imgTemplate.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		for (int cx = 0; cx < imgTemplate.getWidth(); cx++) {
			for (int cy = 0; cy < imgTemplate.getHeight(); cy++) {
				int color = imgTemplate.getRGB(cx, cy);
				Color c = new Color(color);
				Color n = new Color(c.getRed(), c.getGreen(), c.getBlue(), apha);
				imgTemplateApha.setRGB(cx, cy, n.getRGB());
			}
		}
	}

	private void merge() throws IOException {

		// create the new image, canvas size is the max. of both image sizes
		int w = Math.max(imgGenerated.getWidth(), imgTemplateApha.getWidth());
		int h = Math.max(imgGenerated.getHeight(), imgTemplateApha.getHeight());

		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(imgGenerated, 0, 0, w, h, null);
		g.drawImage(imgTemplateApha, 0, 0, w, h, null);
		g.dispose();

		// Save as new image
		// ImageIO.write(combined, "PNG", new File("combined.png"));
		panelGraph.draw(combined, loadMZXML.getWidthMM(), loadMZXML.getHeightMM());

	}

	private void syncMarkers() {
		if (jcheckEnable.isSelected() && (state == State.TEMPLATE || state == State.GENERATED)) {
			if (state == State.TEMPLATE) {
				mTemplateB.setVisible();
				mTemplateE.setVisible();
			} else if (state == State.GENERATED) {
				mGeneratedB.setVisible();
				mGeneratedE.setVisible();
			}
		} else {
			mTemplateB.setInvisible();
			mTemplateE.setInvisible();
			mGeneratedB.setInvisible();
			mGeneratedE.setInvisible();
		}
		panelGraph.repaint();
	}

	@Override
	public void mouseClicked(PanelGraph.Point p) {
		if (!jcheckEnable.isSelected()) {
			return;
		}
		if (state == State.TEMPLATE) {
			if (jtemplateB.hasFocus()) {
				PanelGraphPaint.Marker m = new PanelGraphPaint.Marker(p);
				panelGraph.removeMarker(mTemplateB);
				panelGraph.addMarker(m);
				mTemplateB = m;
				jtemplateB.setText(String.format("%d, %d", (int) (p.x * imgTemplate.getWidth()),
						(int) (p.y * imgGenerated.getHeight())));
			} else if (jtemplateE.hasFocus()) {
				PanelGraphPaint.Marker m = new PanelGraphPaint.Marker(p);
				panelGraph.removeMarker(mTemplateE);
				panelGraph.addMarker(m);
				mTemplateE = m;
				jtemplateE.setText(String.format("%d, %d", (int) (p.x * imgTemplate.getWidth()),
						(int) (p.y * imgGenerated.getHeight())));
			}
		} else if (state == State.GENERATED) {
			if (jgeneratedB.hasFocus()) {
				PanelGraphPaint.Marker m = new PanelGraphPaint.Marker(p);
				panelGraph.removeMarker(mGeneratedB);
				panelGraph.addMarker(m);
				mGeneratedB = m;
				jgeneratedB.setText(String.format("%d, %d", (int) (p.x * imgGenerated.getWidth()),
						(int) (p.y * imgGenerated.getHeight())));
			} else if (jgeneratedE.hasFocus()) {
				PanelGraphPaint.Marker m = new PanelGraphPaint.Marker(p);
				panelGraph.removeMarker(mGeneratedE);
				panelGraph.addMarker(m);
				mGeneratedE = m;
				jgeneratedE.setText(String.format("%d, %d", (int) (p.x * imgGenerated.getWidth()),
						(int) (p.y * imgGenerated.getHeight())));
			}
		}
		panelGraph.repaint();
	}

}
