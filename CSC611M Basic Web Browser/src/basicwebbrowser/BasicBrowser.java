package basicwebbrowser;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class BasicBrowser extends JFrame implements HyperlinkListener {

	private JButton btnBack;
	private JButton btnFwrd;
	private JButton btnGo;
	private JTextField txtUrlField;
	private JEditorPane editorPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BasicBrowser frame = new BasicBrowser();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public BasicBrowser() {
		initGUI();
	}

	private void initGUI() {
		setTitle("Basic Web Browser");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 960, 576);
		setLocationRelativeTo(null);

		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		btnBack = new JButton("<<");
		btnBack.setToolTipText("Back");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 12));
		pnlMain.add(btnBack);

		btnFwrd = new JButton(">>");
		btnFwrd.setToolTipText("Forward");
		btnFwrd.setFont(new Font("Tahoma", Font.PLAIN, 12));
		pnlMain.add(btnFwrd);

		txtUrlField = new JTextField();
		txtUrlField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		final String initTxt = "Enter url here";
		txtUrlField.setText(initTxt);
		txtUrlField.setColumns(75);
		txtUrlField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if (txtUrlField.getText().isEmpty())
					txtUrlField.setText(initTxt);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (txtUrlField.getText().equals(initTxt))
					txtUrlField.setText("");
				else
					txtUrlField.selectAll();
			}
		});
		pnlMain.add(txtUrlField);

		btnGo = new JButton("Go");
		pnlMain.add(btnGo);
		btnGo.setToolTipText("Go");
		btnGo.setFont(new Font("Tahoma", Font.PLAIN, 12));

		editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		editorPane.setEditable(false);
		editorPane.addHyperlinkListener(this);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(pnlMain, BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(editorPane), BorderLayout.CENTER);
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent h) {
		// TODO Auto-generated method stub

	}
}
