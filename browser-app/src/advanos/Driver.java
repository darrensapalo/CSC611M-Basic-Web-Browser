package advanos;


import advanos.browser.BrowserSocket;

import java.io.IOException;
import java.net.InetAddress;

public class Driver {
    public static final String defaultUrl = "google.com";

    public static void main(String[] args) {

        try {
            String url;
            if (args.length > 0)
                url = args[0];
            else
                url = defaultUrl;

            InetAddress ipaddress = InetAddress.getByName(url);
            System.out.println("IP address: " + ipaddress.getHostAddress());

            BrowserSocket browserSocket = new BrowserSocket(url, url);
            browserSocket.sendRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
