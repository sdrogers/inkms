package com.constambeys.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import com.constambeys.layout.SpringUtilities;

/**
 * The {@code DialogOptimalMz} class allows the user to specify Optimal Mass settings
 * 
 * @author Constambeys
 *
 */
public class DialogOptimalMz extends JDialog {

	/**
	 * Create the dialog.
	 * 
	 * @param result
	 */
	ActionListener ok;

	JPanel panelSouth = new JPanel();
	JButton buttonOK = new JButton("OK");

	JPanel panelCenter;
	Map<String, JTextField> map = new HashMap<String, JTextField>();

	/**
	 * Initialises a new user interface dialog
	 * 
	 * @param owner
	 *            the Frame from which the dialog is displayed
	 * @param title
	 *            the {@code String} to display in the dialog's title bar
	 * @param modal
	 *            specifies whether dialog blocks user input to other top-level windows when shown
	 */
	public DialogOptimalMz(FrameMain owner, String title, boolean modal) {
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
		panelCenter = new JPanel(new SpringLayout());
		getContentPane().add(panelCenter, BorderLayout.CENTER);

		panelSouth.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelSouth.setBorder(new EmptyBorder(5, 5, 5, 5));

		JButton buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		panelSouth.add(buttonCancel);

		buttonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
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
	 * Set OK button listener
	 * 
	 * @param l
	 *            callback listener
	 */
	public void addOkListener(ActionListener l) {
		ok = l;
	}

	/**
	 * Add text box
	 * 
	 * @param id
	 *            used as a key
	 * @param title
	 *            The text to be displayed by the label
	 */
	public void addTextBox(String id, String title) {
		JTextField jtextField = new JTextField(10);
		JLabel l = new JLabel(title, JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jtextField);
		panelCenter.add(jtextField);

		map.put(id, jtextField);

		// Lay out the panel.
		SpringUtilities.makeCompactGrid(panelCenter, map.size(), 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad
	}

	/**
	 * Add text box
	 * 
	 * @param id
	 *            used as a key
	 * @param title
	 *            The text to be displayed by the label
	 * @param defaultValue
	 *            The text to be displayed by the text box
	 * 
	 */
	public void addTextBox(String id, String title, String defaultValue) {
		JTextField jtextField = new JTextField(10);
		jtextField.setText(defaultValue);
		JLabel l = new JLabel(title, JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jtextField);
		panelCenter.add(jtextField);

		map.put(id, jtextField);

		// Lay out the panel.
		SpringUtilities.makeCompactGrid(panelCenter, map.size(), 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad
	}

	/**
	 * Get value
	 * 
	 * @param id
	 *            used as a key
	 * 
	 * @return the text contained in {@code TextComponent}
	 */
	public String getText(String id) {
		return map.get(id).getText();
	}
}
