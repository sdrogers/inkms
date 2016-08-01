package com.constambeys.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class DialogGraph extends JDialog {

	interface IOkListener {
		public void actionPerformed(ActionEvent e, List<MassRange> ranges);
	}

	private IOkListener ok;
	private JTextField jLowerMass;
	private JTextField jHigherMass;
	private DefaultListModel<MassRange> jListModel;
	private JList<MassRange> jList;

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

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

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

		JButton jbtnRight = new JButton();
		int height = (int) jbtnRight.getPreferredSize().getHeight();
		jbtnRight.setIcon(Startup.loadIcon("right128.png", height, height));
		jbtnRight.setFocusable(false);
		JButton jbtnLeft = new JButton();
		jbtnLeft.setIcon(Startup.loadIcon("left128.png", height, height));
		jbtnLeft.setFocusable(false);
		JButton buttonOK = new JButton("OK");
		buttonOK.setFocusable(false);

		ParallelGroup h1 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		h1.addComponent(l1);
		h1.addComponent(jLowerMass);
		h1.addComponent(l2);
		h1.addComponent(jHigherMass);

		ParallelGroup h2 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		h2.addComponent(jbtnRight);
		h2.addComponent(jbtnLeft);

		ParallelGroup h3 = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
		h3.addComponent(jScrollList);
		h3.addComponent(buttonOK);

		SequentialGroup h = layout.createSequentialGroup();
		h.addGroup(h1);
		h.addGroup(h2);
		h.addGroup(h3);
		layout.setHorizontalGroup(h);

		layout.linkSize(SwingConstants.HORIZONTAL, jbtnRight, jbtnLeft);

		SequentialGroup v1 = layout.createSequentialGroup();
		v1.addComponent(l1);
		v1.addComponent(jLowerMass);
		v1.addComponent(l2);
		v1.addComponent(jHigherMass);

		SequentialGroup v2 = layout.createSequentialGroup();
		v2.addComponent(jbtnRight);
		v2.addComponent(jbtnLeft);

		ParallelGroup v3 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		v3.addComponent(jScrollList);

		ParallelGroup v4 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		v4.addGroup(v1);
		v4.addGroup(v2);
		v4.addGroup(v3);

		SequentialGroup v = layout.createSequentialGroup();
		v.addGroup(v4);
		v.addComponent(buttonOK);

		layout.setVerticalGroup(v);

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

		buttonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					parseInput();
					setVisible(false);
					dispose();
					if (ok != null) {
						ArrayList<MassRange> list = new ArrayList<MassRange>();
						for (int i = 0; i < jListModel.getSize(); i++) {
							MassRange item = jListModel.getElementAt(i);
							list.add(item);
						}
						ok.actionPerformed(event, list);
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		getRootPane().setDefaultButton(buttonOK);

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
		jListModel.addElement(m);
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
