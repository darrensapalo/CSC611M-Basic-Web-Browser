package basicwebbrowser;

public class PerformanceThread extends Thread{
	@Override
	public void run() {
		try {
			new BasicBrowser("test.html");
			// frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
