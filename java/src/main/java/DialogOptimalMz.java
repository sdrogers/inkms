import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import layout.SpringUtilities;

public class DialogOptimalMz extends JDialog {

	/**
	 * Create the dialog.
	 * 
	 * @param result
	 */
	ActionListener ok;

	JPanel panelSouth = new JPanel();
	JButton buttonOK = new JButton("OK");

	JTextField jxStart = new JTextField(10);
	JTextField jxStop = new JTextField(10);
	JTextField jyStart = new JTextField(10);
	JTextField jyStop = new JTextField(10);
	JTextField jLowerMass = new JTextField(10);
	JTextField jHigherMass = new JTextField(10);
	JTextField jResolution = new JTextField(10);

	private void setupUI() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 200, 100);
		setMinimumSize(getSize());
		getContentPane().setLayout(new BorderLayout());

		// Create and populate the panel.
		JPanel panelCenter = new JPanel(new SpringLayout());
		JLabel l;

		l = new JLabel("x start (mm)", JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jxStart);
		panelCenter.add(jxStart);

		l = new JLabel("x stop (mm)", JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jxStop);
		panelCenter.add(jxStop);

		l = new JLabel("y start (mm)", JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jyStart);
		panelCenter.add(jyStart);

		l = new JLabel("y stop (mm)", JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jyStop);
		panelCenter.add(jyStop);

		l = new JLabel("Lower Mass", JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jLowerMass);
		panelCenter.add(jLowerMass);

		l = new JLabel("Higher Mass", JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jHigherMass);
		panelCenter.add(jHigherMass);

		l = new JLabel("Resolution", JLabel.TRAILING);
		panelCenter.add(l);
		l.setLabelFor(jResolution);
		panelCenter.add(jResolution);

		// Lay out the panel.
		SpringUtilities.makeCompactGrid(panelCenter, 7, 2, // rows, cols
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

	public DialogOptimalMz(FrameMain mainFrame, String title, boolean modal) {
		super(mainFrame, title, modal);
		setupUI();
	}

	public void addOkListener(ActionListener l) {
		ok = l;
	}
}
