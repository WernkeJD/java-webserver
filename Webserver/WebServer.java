/**
 * Assignment 1
 * Jacob Wernke
 **/
import java.io.*;
import java.net.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

final class HttpRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;

    // Constructor
    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    // Implement the run() method of the Runnable interface.
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        // Get the input and output streams from the socket.
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        // Read and print the request line and header lines.
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String requestLine = br.readLine();
        System.out.println(requestLine);

        String headerLine;
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }

        // Close streams and socket.
        os.close();
        br.close();
        socket.close();
    }
}


public final class WebServer {
    public static void main(String argv[]) throws Exception {
        // Set the port number.
        int port = 6789;
        
        // Establish the listen socket.
        ServerSocket user_socket = new ServerSocket(port);

        //Process HTTP service requests in an infinite loop.
        while (true) {
            // Listen for a TCP connection request.
            Socket accepted_socket = user_socket.accept();

            // Create an HttpRequest object to process the request.
            HttpRequest request = new HttpRequest(accepted_socket);

            // Create a new thread to process the request.
            Thread thread = new Thread(request);
            //Start the thread
            thread.start();
        }
    }
}


