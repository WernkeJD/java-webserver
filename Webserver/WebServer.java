/**
 * Assignment 1
 * Jacob Wernke
 **/
import java.io.*;
import java.net.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;

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
        // Get a reference to the socket's input and output streams.
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
    
        // Read and print the request line and header lines.
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String requestLine = br.readLine();
        System.out.println("Request:");
        System.out.println("------------");
        System.out.println(requestLine);

    
        // Extract the filename from the request line.
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();  // skip over the method, which should be "GET"
        String fileName = tokens.nextToken();
    
        // Prepend a "." so that file request is within the current directory.
        fileName = "." + fileName;
    
        // Open the requested file.
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }
    
        // Construct the response message.
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;
        if (fileExists) {
            statusLine = "HTTP/1.1 200 OK" + CRLF;
            contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;    
            System.out.println("Response:");
            System.out.println("-------------");
            System.out.println(statusLine);
            System.out.println(contentTypeLine);
        } else {
            statusLine = "HTTP/1.1 404 Not Found" + CRLF;;
            contentTypeLine = "Content-type: text/html" + CRLF;
            entityBody = "<HTML>" + 
                         "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
                         "<BODY>Not Found</BODY></HTML>";
            System.out.println("Response:");
            System.out.println("-------------");
            System.out.println(statusLine);
            System.out.println(contentTypeLine);
        }
    
        // Send the status line.
        os.writeBytes(statusLine);
    
        // Send the content type line.
        os.writeBytes(contentTypeLine);
    
        // Send a blank line to indicate the end of the header lines.
        os.writeBytes(CRLF);
    
        // Send the entity body.
        if (fileExists) {
            sendBytes(fis, os);
            fis.close();
        } else {
            os.writeBytes(entityBody);
        }
    
        // Close streams and socket.
        os.close();
        fis.close();
    }
    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        // Construct a 1K buffer to hold bytes on their way to the socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;
    
        // Copy requested file into the socket's output stream.
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }
    
    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if (fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (fileName.endsWith(".gif")) {
            return "image/gif";
        }
        if(fileName.endsWith(".txt")){
            return "text/plain";
        }
        return "application/octet-stream";
    }
}





