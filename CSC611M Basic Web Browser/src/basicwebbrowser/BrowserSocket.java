package basicwebbrowser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class BrowserSocket extends Socket{

	private String file;

	public BrowserSocket(String ipAddress, int port, String file) throws UnknownHostException, IOException {
		/* 
		 * Once the BrowserSocket object is created, it creates the
		 * super class <i>Socket</i> and stores which file was requested.
		 */
		super(ipAddress, port);
		this.file = file;
	}
	
	/**
	 * This function sends an HTTP GET request to the server, and has the option of
	 * printing out the results of the retrieved page.
	 * @throws IOException
	 */
	public void sendRequest() throws IOException {
		InputStream inputStream = getInputStream();
		OutputStream outputStream = getOutputStream();

		// To be able to create an HTTP request, we need to write on the socket
		PrintStream writer = new PrintStream(outputStream);
				
		// Just like in Youtube, buffer the contents that the server is sending to you
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		// Create an HTTP GET request
		// In the form of a String such as "GET /index.html HTTP/1.0"
		
		writer.println("GET /" + file + " HTTP/1.0");
		writer.println();
		writer.flush();

		// This is where the each line of data is stored from the socket, given by the server
		String response;
		
		// Read the response header
		while ((response = reader.readLine()).length() > 0) {
			if (response.startsWith("HTTP/1.0 404 Not Found")) {
				System.err.println("Not found");
			}
		}

		// Do you want to display the results on the console?
		boolean showRetrievedPage = false;
		
		// Read the response content
		while ((response = reader.readLine()) != null) {
			if (showRetrievedPage)
				System.out.println(response);
		}

		// BufferedInputStream bis = new
		// BufferedInputStream(s.getInputStream());
	}

}
