package basicwebbrowser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Stack;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.commons.validator.routines.UrlValidator;

public class BrowserGUI extends JFrame implements HyperlinkListener {

	private JButton btnBack;
	private JButton btnFwrd;
	private JButton btnGo;
	private JTextField txtUrlField;
	private JEditorPane editorPane;
	private JLabel lblStatus;
	private JProgressBar progressBar;
	private Semaphore wait = new Semaphore(0);
	/**
	 * Flag for checking if page has fully rendered
	 */
	private boolean isDoneLoad = false;
	private Stack<String> backStack = new Stack<String>();
	private Stack<String> fwrdStack = new Stack<String>();
	private String title = "Basic Web Browser";
	private String myURL;

	/**
	 * Set this to true when you want the performance to be measured
	 */
	public static boolean PERFORMANCE = true;
	
	/**
	 * Set this to true when you want to show the GUI
	 */
	public static boolean GUI = !false;

	private long startTime;
	private int port;

	/**
	 * Create the frame.
	 * @param port 
	 * @param file 
	 */
	public BrowserGUI(String file, int port) {
		this.port = port;
		
		if (GUI){
			initGUI();
		}
		showPage(file);
	}


	private void initGUI() {
		setTitle(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 960, 576);
		setLocationRelativeTo(null);

		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		btnBack = new JButton("<<");
		btnBack.setToolTipText("Back");
		btnBack.setEnabled(false);
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
		btnFwrd.setEnabled(false);
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
				// if (e.getKeyCode() == KeyEvent.VK_ENTER)
				// goLoad();
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
				// goLoad();
			}
		});
		pnlMain.add(btnGo);

		editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		editorPane.setEditable(false);
		// this is for determining if the page has fully rendered
		editorPane.addPropertyChangeListener("page",
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						isDoneLoad = true;
						wait.release();
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

	/**
	 * Handler for BACK button
	 */
	public void goBack() {
		myURL = backStack.pop();
		fwrdStack.push(myURL);
		myURL = backStack.peek();
		new PageLoader().execute();
	}

	/**
	 * Handler for FORWARD button
	 */
	public void goForward() {
		myURL = fwrdStack.pop();
		backStack.push(myURL);
		new PageLoader().execute();
	}

	/**
	 * Handler for GO button
	 * 
	 * @param url
	 */
	public void goLoad(String url) {
		myURL = checkURL(url);

		 if (!myURL.isEmpty()) {
		 backStack.push(myURL);
		 fwrdStack.clear();
		 }

		new PageLoader().execute();
	}

	/**
	 * Checks if the url is in the proper/valid format. Format is either
	 * 'http://www.something.com' or 'https://www.something.com' or
	 * 'http://something.com' or 'https://something.com'
	 * 
	 * @param url
	 *            URL to validate
	 * @return url if valid, empty if invalid
	 */
	private String checkURL(String url) {
		// force it to http or https protocols
		String[] schemes = { "http", "https" };
		// UrlValidator.ALLOW_LOCAL_URLS is for allowing localhost or
		// localmachine
		UrlValidator urlValidator = new UrlValidator(schemes,
				UrlValidator.ALLOW_LOCAL_URLS);
		if (urlValidator.isValid(url))
			return url;
		else
			return "";
	}

	/**
	 * Renders the page into the Basic Web Browser
	 * 
	 * @param file
	 *            URL to set in the title bar
	 */
	private void showPage(String file) {
		if (PERFORMANCE)
			startTime = System.nanoTime();
		
		try {
			if (GUI){
				setTitle(title + " || " + file);
				txtUrlField.setText(file);
				editorPane.setPage(file);	
			}
			
			// Read the 'file' from port '80' of the host 'localhost'
			BrowserSocket browserSocket = new BrowserSocket("localhost", port, file);
			
			// Send the HTTP GET request
			browserSocket.sendRequest();
			
			// Close the socket
			browserSocket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (PERFORMANCE){
				long doneTime = System.nanoTime();
				
				// Duration of how many seconds the page took to load from the server
				double diff = (doneTime - startTime) / 1000000000.0;
				System.out.println(diff);
			}
		}
		
		if (GUI)
			updateButtons();
	}

	/**
	 * Enables/Disables the BACK and FORWARD buttons
	 */
	private void updateButtons() {
		if (backStack.size() < 2)
			btnBack.setEnabled(false);
		else
			btnBack.setEnabled(true);

		if (fwrdStack.isEmpty())
			btnFwrd.setEnabled(false);
		else
			btnFwrd.setEnabled(true);
	}

	/**
	 * Handler for clicking the available links in the page
	 */
	@Override
	public void hyperlinkUpdate(HyperlinkEvent h) {
		if (h.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			myURL = checkURL(h.getURL().toString());

			if (!myURL.isEmpty()) {
				backStack.push(myURL);
				fwrdStack.clear();
			}

			new PageLoader().execute();
		}
	}

	/**
	 * Thread thingy for simultaneous progress bar animation
	 */
	class PageLoader extends SwingWorker<String, Void> {

		/**
		 * Handles page render while the progress bar is moving
		 */
		protected String doInBackground() {

			 if (myURL.isEmpty()) {
			 setTitle(title + " || Invalid URL");
			 editorPane.setText("Error: Invalid URL");
			 isDoneLoad = true;
			 } else {
			 progressBar.setVisible(true);
			 progressBar.setIndeterminate(true);
			showPage(myURL);
			 }

			// for valid page: wait until page has fully rendered
			// refer to editorPane.addPropertyChangeListener() in initGUI()
			try {
				wait.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// while (!isDoneLoad)
			// lblStatus.setText("Loading");

			return "Done";
		}

		/**
		 * This is called when doInBackground() returns "Done"
		 */
		protected void done() {
			 progressBar.setIndeterminate(false);
			 progressBar.setVisible(false);
			 lblStatus.setText("Done");
			 isDoneLoad = false;

		}
	}

}
