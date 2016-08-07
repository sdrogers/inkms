package com.constambeys.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import com.constambeys.ui.colormaps.ColormapShow;
import com.constambeys.ui.colormaps.Custom;
import com.constambeys.ui.colormaps.Gray;
import com.constambeys.ui.colormaps.Hot;
import com.constambeys.ui.colormaps.IColormap;

/**
 * The graph dialog allows the user to plot an image
 * 
 * @author Constambeys
 *
 */
public class DialogGraph extends JDialog {

	interface IOkListener {
		public void actionPerformed(ActionEvent e, MassRange ranges[], IColormap colormap);
	}

	/**
	 * Specifies the available colormaps
	 */
	private static enum Colormap {
		HOT("HOT"), GRAY("GRAY"), CUSTOM("CUSTOM");

		String name;

		private Colormap(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	// Mass Range
	private JTextField jLowerMass;
	private JTextField jHigherMass;
	private DefaultListModel<MassRange> jListModel;
	private JList<MassRange> jList;

	// Colormap
	private ItemListener jcomboListener;
	private JComboBox<Colormap> jcomboColor;
	private ColormapShow colormapShow;

	// Buttons
	private JButton jbtnRight;
	private JButton jbtnLeft;
	private JButton jbtnDelete;
	private JButton buttonOK;
	private IOkListener ok;

	/**
	 * The {@code DialogGraph} class allows the user to draw a mass range
	 * 
	 * @param owner
	 *            the Frame from which the dialog is displayed
	 * @param title
	 *            the {@code String} to display in the dialog's title bar
	 * @param modal
	 *            specifies whether dialog blocks user input to other top-level windows when shown
	 * @throws IOException
	 */
	public DialogGraph(Frame owner, String title, boolean modal) throws IOException {
		super(owner, title, modal);
		setupUI();
	}

	/**
	 * The {@code DialogGraph} class allows the user to draw a mass range
	 * 
	 * @param owner
	 *            the Frame from which the dialog is displayed
	 * @param title
	 *            the {@code String} to display in the dialog's title bar
	 * @param modal
	 *            specifies whether dialog blocks user input to other top-level windows when shown
	 * @throws IOException
	 */
	public DialogGraph(Dialog owner, String title, boolean modal) throws IOException {
		super(owner, title, modal);
		setupUI();
	}

	public void addMassRange(MassRange m) {
		jListModel.addElement(m);
	}

	public void addMassRange(MassRange ranges[]) {
		for (MassRange m : ranges) {
			jListModel.addElement(m);
		}
	}

	public void setColormap(IColormap c) {
		colormapShow.setColormap(c);
		jcomboColor.removeItemListener(jcomboListener);
		if (c instanceof Hot) {
			jcomboColor.setSelectedItem(Colormap.HOT);
		} else if (c instanceof Gray) {
			jcomboColor.setSelectedItem(Colormap.GRAY);
		} else {
			jcomboColor.setSelectedItem(Colormap.CUSTOM);
		}
		jcomboColor.addItemListener(jcomboListener);
	}

	/**
	 * Initialises the UI elements
	 * 
	 * @throws IOException
	 * 
	 */
	private void setupUI() throws IOException {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 200);
		setMinimumSize(getSize());

		JPanel background = new JPanel();
		setContentPane(background);

		background.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Border Layout North, South,East,West
		background.setLayout(new BorderLayout(0, 0));

		background.add(createCenterPanel(), BorderLayout.CENTER);
		colormapShow = new ColormapShow();
		background.add(colormapShow, BorderLayout.EAST);

		Box boxSouth = new Box(BoxLayout.X_AXIS);
		boxSouth.setBorder(new EmptyBorder(5, 5, 5, 5));
		background.add(boxSouth, BorderLayout.SOUTH);

		JLabel l = new JLabel("Colormap:", JLabel.TRAILING);
		boxSouth.add(l);
		DefaultComboBoxModel<Colormap> model = new DefaultComboBoxModel<Colormap>(Colormap.values());
		jcomboColor = new JComboBox<Colormap>();
		jcomboColor.setModel(model);
		l.setLabelFor(jcomboColor);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));
		boxSouth.add(jcomboColor);

		boxSouth.add(Box.createHorizontalGlue());
		buttonOK = new JButton("OK");
		buttonOK.setFocusable(false);
		boxSouth.add(buttonOK);
		boxSouth.add(Box.createRigidArea(new Dimension(10, 0)));

		addListeners();
		changeColormap((Colormap) jcomboColor.getSelectedItem());
		getRootPane().setDefaultButton(buttonOK);

	}

	private JPanel createCenterPanel() throws IOException {

		JPanel panelCenter = new JPanel();
		GroupLayout layoutCenter = new GroupLayout(panelCenter);
		panelCenter.setLayout(layoutCenter);

		layoutCenter.setAutoCreateGaps(true);
		layoutCenter.setAutoCreateContainerGaps(true);

		JLabel l1 = new JLabel("Lower Mass", JLabel.TRAILING);
		l1.setFocusable(false);
		JLabel l2 = new JLabel("Higher Mass", JLabel.TRAILING);
		l2.setFocusable(false);

		jLowerMass = new JTextField();
		jLowerMass.setFocusable(true);
		jHigherMass = new JTextField();
		jLowerMass.setFocusable(true);

		jListModel = new DefaultListModel<MassRange>();
		jList = new JList(jListModel);
		jList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		jList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		jList.setFocusable(false);
		JScrollPane jScrollList = new JScrollPane();
		jScrollList.setPreferredSize(new Dimension(80, 80));
		jScrollList.setViewportView(jList);

		jbtnRight = new JButton();
		int height = (int) jbtnRight.getPreferredSize().getHeight();
		jbtnRight.setIcon(Startup.loadIcon("right128.png", height, height));
		jbtnRight.setFocusable(false);

		jbtnLeft = new JButton();
		jbtnLeft.setIcon(Startup.loadIcon("left128.png", height, height));
		jbtnLeft.setFocusable(false);

		jbtnDelete = new JButton();
		jbtnDelete.setIcon(Startup.loadIcon("close128.png", height, height));
		jbtnDelete.setFocusable(false);

		ParallelGroup h1 = layoutCenter.createParallelGroup(GroupLayout.Alignment.LEADING);
		h1.addComponent(l1);
		h1.addComponent(jLowerMass);
		h1.addComponent(l2);
		h1.addComponent(jHigherMass);

		ParallelGroup h2 = layoutCenter.createParallelGroup(GroupLayout.Alignment.CENTER);
		h2.addComponent(jbtnRight);
		h2.addComponent(jbtnLeft);
		h2.addComponent(jbtnDelete);

		SequentialGroup h = layoutCenter.createSequentialGroup();
		h.addGroup(h1);
		h.addGroup(h2);
		h.addComponent(jScrollList);
		layoutCenter.setHorizontalGroup(h);

		layoutCenter.linkSize(SwingConstants.HORIZONTAL, jbtnRight, jbtnLeft);

		SequentialGroup v1 = layoutCenter.createSequentialGroup();
		v1.addComponent(l1);
		v1.addComponent(jLowerMass);
		v1.addComponent(l2);
		v1.addComponent(jHigherMass);

		SequentialGroup v2 = layoutCenter.createSequentialGroup();
		v2.addComponent(jbtnRight);
		v2.addComponent(jbtnLeft);
		v2.addComponent(jbtnDelete);

		ParallelGroup v3 = layoutCenter.createParallelGroup(GroupLayout.Alignment.CENTER);
		v3.addComponent(jScrollList);

		ParallelGroup v = layoutCenter.createParallelGroup(GroupLayout.Alignment.CENTER);
		v.addGroup(v1);
		v.addGroup(v2);
		v.addGroup(v3);

		layoutCenter.setVerticalGroup(v);

		return panelCenter;
	}

	private void addListeners() {
		jbtnRight.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					parseInput();
					jLowerMass.setText("");
					jHigherMass.setText("");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		});

		jbtnLeft.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int index = jList.getSelectedIndex();
				if (index != -1) {
					MassRange selected = jListModel.getElementAt(index);
					jListModel.remove(index);
					jLowerMass.setText(selected.lowerMass());
					jHigherMass.setText(selected.higherMass());
				}

			}
		});

		jbtnDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int index = jList.getSelectedIndex();
				if (index != -1) {
					MassRange selected = jListModel.getElementAt(index);
					jListModel.remove(index);
				}
			}
		});

		buttonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					parseInput();
					setVisible(false);
					dispose();
					if (ok != null) {
						MassRange ranges[] = new MassRange[jListModel.getSize()];
						for (int i = 0; i < jListModel.getSize(); i++) {
							MassRange item = jListModel.getElementAt(i);
							ranges[i] = item;
						}
						ok.actionPerformed(event, ranges, colormapShow.getColormap());
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		jcomboListener = new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					changeColormap((Colormap) e.getItem());
				}
			}
		};

		jcomboColor.addItemListener(jcomboListener);

	}

	private void changeColormap(Colormap c) {
		if (c == Colormap.HOT) {
			colormapShow.setColormap(new Hot());
		} else if (c == Colormap.GRAY) {
			colormapShow.setColormap(new Gray());
		} else {
			String input = JOptionPane.showInputDialog("Enter hue value between 0 and 1", 0.5);
			if (input != null)
				colormapShow.setColormap(new Custom(Float.parseFloat(input)));
		}
	}

	/**
	 * Reads user interface values and adds them to {@code massrange} array
	 */
	private void parseInput() {
		String strLowerMass = jLowerMass.getText().trim();
		String strHigherMass = jHigherMass.getText().trim();
		if (strLowerMass.equals("") && strHigherMass.equals("")) {
			return;
		}

		MassRange m = new MassRange(strLowerMass, strHigherMass);
		addMassRange(m);
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

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
					// "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
					// UIManager.getCrossPlatformLookAndFeelClassName());

					Dialog dialog = new DialogGraph(new JFrame(), "Set Parameters", true);
					dialog.pack();
					dialog.setVisible(true);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		});
	}

}
