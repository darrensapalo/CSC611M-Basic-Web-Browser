package basicwebbrowser;

public class PerformanceThread extends Thread{
	private String file;
	private int port;
	
	public PerformanceThread(String file, int port) {
		this.file = file;
		this.port = port;	
	}
	
	@Override
	public void run() {
		try {
			BrowserGUI frame = new BrowserGUI(file, port);
			
			if (BrowserGUI.GUI)
				frame.setVisible(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
