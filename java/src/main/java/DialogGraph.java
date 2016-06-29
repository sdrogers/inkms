import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;
import java.io.File;

import layout.SpringUtilities;

public class DialogGraph extends JDialog {

	/**
	 * Create the dialog.
	 * 
	 * @param result
	 */
	ActionListener ok;

	JPanel panelSouth = new JPanel();
	JButton buttonOK = new JButton("OK");

	JTextField jLowerMass = new JTextField(10);
	JTextField jHigherMass = new JTextField(10);

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

	public DialogGraph(FrameMain mainFrame, String title, boolean modal) {
		super(mainFrame, title, modal);
		setupUI();
	}

	public void addOkListener(ActionListener l) {
		ok = l;
	}
}
