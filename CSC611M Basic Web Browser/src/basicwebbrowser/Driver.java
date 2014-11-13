package basicwebbrowser;


public class Driver {

	public static void main(String[] args) {
		int runs = 50;

		for (int i = 0; i < runs; i++) {
			PerformanceThread pt = new PerformanceThread();
			pt.start();

		}
	}

}
