package basicwebbrowser;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.JProgressBar;
import javax.swing.JLabel;

public class BasicBrowser extends JFrame implements HyperlinkListener {

	private JButton btnBack;
	private JButton btnFwrd;
	private JButton btnGo;
	private JTextField txtUrlField;
	private JEditorPane editorPane;
	private JLabel lblStatus;
	private JProgressBar progressBar;

	private boolean isDoneLoad = false;

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
		btnBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goBack();
			}
		});
		pnlMain.add(btnBack);

		btnFwrd = new JButton(">>");
		btnFwrd.setToolTipText("Forward");
		btnFwrd.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnFwrd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goForward();
			}
		});
		pnlMain.add(btnFwrd);

		txtUrlField = new JTextField();
		txtUrlField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		final String initTxt = "Enter url here";
		txtUrlField.setText(initTxt);
		txtUrlField.setColumns(75);
		txtUrlField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					goLoad();
			}
		});
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
		btnGo.setToolTipText("Go");
		btnGo.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnGo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goLoad();
			}
		});
		pnlMain.add(btnGo);

		editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		editorPane.setEditable(false);
		// this is for determining if the page has fully rendered
		editorPane.addPropertyChangeListener("page", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				isDoneLoad = true;
			}
		});
		editorPane.addHyperlinkListener(this);

		JPanel pnlProgress = new JPanel();
		pnlProgress.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));

		lblStatus = new JLabel("Empty");
		pnlProgress.add(lblStatus);

		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		pnlProgress.add(progressBar);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(pnlMain, BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(editorPane), BorderLayout.CENTER);
		getContentPane().add(pnlProgress, BorderLayout.SOUTH);
	}

	/* BUTTON FUNCTIONS */
	public void goBack() {

	}

	public void goForward() {

	}

	/*
	 * if empty, do nothing
	 * if no .com, wrong
	 * if www.something.com, add http://
	 * if something.com, add http://www.
	 */

	public void goLoad() {
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);

		// thread thingy for progress bar animation
		class MyWorker extends SwingWorker<String, Void> {

			// while progress bar is moving, render the page
			protected String doInBackground() {
				// basic page rendering --> main goal
				String url = txtUrlField.getText();
				try {
					editorPane.setPage(url);
				} catch (IOException e) {
					e.printStackTrace();
					isDoneLoad = true;
					editorPane.setText("Error: " + e);
				}

				// for valid page: wait until page has fully rendered
				// refer to editorPane.addPropertyChangeListener() in initGUI()
				while (!isDoneLoad)
					lblStatus.setText("Loading");

				return "Done";
			}

			// this is called when doInBackground() is "Done"
			protected void done() {
				progressBar.setIndeterminate(false);
				progressBar.setVisible(false);
				lblStatus.setText("Done");
				isDoneLoad = false;
			}
		}
		new MyWorker().execute();
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent h) {
		// TODO Auto-generated method stub

	}
}
