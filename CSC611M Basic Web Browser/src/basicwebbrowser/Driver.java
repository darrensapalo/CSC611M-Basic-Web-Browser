package basicwebbrowser;


public class Driver {

	public static void main(String[] args) {
		
		int instances = 1;
		
		String localFile = "test.html";
		int port = 80;
		
		for (int i = 0; i < instances; i++) {
			PerformanceThread pt = new PerformanceThread(localFile, port);
			pt.start();

		}
	}

}
