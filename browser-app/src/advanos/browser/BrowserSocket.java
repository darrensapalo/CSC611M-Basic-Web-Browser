package advanos.browser;

import java.io.*;
import java.net.Socket;

public class BrowserSocket extends Socket {

    private String url;
    private String filename;
    private BrowserSocket child;
    private boolean isMoved;

    public BrowserSocket(String url, String filename) throws IOException {
        super(url, 80);
        this.url = url;
        this.filename = filename;
    }

    /**
     * This function sends an HTTP GET request to the server, and has the option of
     * printing out the results of the retrieved page.
     *
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

        writer.println("GET / HTTP/1.1");
        writer.println("Host: " + url);
        writer.println();
        writer.flush();

        // This is where the each line of data is stored from the socket, given by the server
        String response;

        String fileOriginal = this.filename.replace("[^a-zA-Z]+", "");

        File file = new File("/cache/");

        String absolutePath = file.getAbsolutePath();

        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath();
        helper = helper.substring(0, helper.length() - 1);

        file = new File (helper + "cache/");

        boolean mkdir = file.mkdirs();

        file = new File (helper + "cache/" + filename + ".response");
        PrintWriter fileWriter = new PrintWriter(file);

        // Read the response header
        while ((response = reader.readLine()).length() > 0) {
            fileWriter.append(response + "\n");
            if (response.startsWith("HTTP/1.1 404 Not Found")) {
                System.err.println("Not found");
            }else if (response.startsWith("HTTP/1.1 301")){
                System.err.println("Permanently moved");
                isMoved = true;
            }
        }

        fileWriter.flush();
        fileWriter.close();

        file = new File (helper + "cache/" + filename + ".content");
        fileWriter = new PrintWriter(file);

        // Read the response content
        while ((response = reader.readLine()) != null) {
            fileWriter.append(response + "\n");
            fileWriter.flush();
            System.out.println(response);
        }


        fileWriter.close();
        reader.close();
    }

}
