package com.constambeys.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import com.constambeys.layout.SpringUtilities;

public class DialogGraph extends JDialog {

	/**
	 * Create the dialog.
	 * 
	 * @param result
	 */
	private ActionListener ok;

	private JPanel panelSouth = new JPanel();
	private JButton buttonOK = new JButton("OK");

	private JTextField jLowerMass = new JTextField(10);
	private JTextField jHigherMass = new JTextField(10);

	ArrayList<MassPerCharge> massrange = new ArrayList<MassPerCharge>();

	/**
	 * The {@code DialogGraph} class allows the user to draw a mass range
	 * 
	 * @param owner
	 *            the Frame from which the dialog is displayed
	 * @param title
	 *            the {@code String} to display in the dialog's title bar
	 * @param modal
	 *            specifies whether dialog blocks user input to other top-level windows when shown
	 */
	public DialogGraph(Frame owner, String title, boolean modal) {
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
	 */
	public DialogGraph(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		setupUI();
	}

	/**
	 * Initialises the UI elements
	 * 
	 */
	private void setupUI() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 200, 100);
		setMinimumSize(getSize());
		getContentPane().setLayout(new BorderLayout());

		// Create and populate the panel.
		JPanel panelCenter = new JPanel(new SpringLayout());
		JLabel l;

		l = new JLabel("Lower Mass", JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jLowerMass);
		panelCenter.add(jLowerMass);

		l = new JLabel("Higher Mass", JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jHigherMass);
		panelCenter.add(jHigherMass);

		// Lay out the panel.
		SpringUtilities.makeCompactGrid(panelCenter, 2, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		getContentPane().add(panelCenter, BorderLayout.CENTER);

		panelSouth.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelSouth.setBorder(new EmptyBorder(5, 5, 5, 5));

		JButton buttonSave = new JButton("Save");
		buttonSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					readValues();
					jLowerMass.setText("");
					jHigherMass.setText("");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		panelSouth.add(buttonSave);

		buttonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					readValues();
					setVisible(false);
					dispose();
					if (ok != null)
						ok.actionPerformed(event);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		panelSouth.add(buttonOK);

		getRootPane().setDefaultButton(buttonOK);
		getContentPane().add(panelSouth, BorderLayout.SOUTH);
	}

	/**
	 * Reads user interface values and adds them to {@code massrange} array
	 */
	private void readValues() {
		String strLowerMass = jLowerMass.getText().trim();
		String strHigherMass = jHigherMass.getText().trim();
		if (strLowerMass.equals("") && strHigherMass.equals("")) {
			return;
		}
		double lowerMass = Double.parseDouble(strLowerMass);
		double higherMass = Double.parseDouble(strHigherMass);
		MassPerCharge m = new MassPerCharge(strLowerMass, strHigherMass, lowerMass, higherMass);
		massrange.add(m);
	}

	/**
	 * Set OK button listener
	 * 
	 * @param l
	 *            callback listener
	 */
	public void addOkListener(ActionListener l) {
		ok = l;
	}

	class MassPerCharge {
		String strLowerMass;
		String strHigherMass;
		double lowerMass;
		double higherMass;

		MassPerCharge(String strLowerMass, String strHigherMass, double lowerMass, double higherMass) {
			this.strLowerMass = strLowerMass;
			this.strHigherMass = strHigherMass;

			this.lowerMass = lowerMass;
			this.higherMass = higherMass;

		}
	}
}
