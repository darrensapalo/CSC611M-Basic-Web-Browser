package advanos;


import advanos.browser.BrowserSocket;

import java.io.IOException;
import java.net.InetAddress;

public class Driver {
    public static final String url = "google.com";

	public static void main(String[] args) {

		try {
            InetAddress ipaddress = InetAddress.getByName(url);
            System.out.println("IP address: " + ipaddress.getHostAddress());

            BrowserSocket browserSocket = new BrowserSocket(url, url);
            browserSocket.sendRequest();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
