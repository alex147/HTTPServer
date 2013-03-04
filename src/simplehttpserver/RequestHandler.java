/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simplehttpserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;
import javax.naming.OperationNotSupportedException;

/**
 * Class implementing the Runnable interface, used to handle request from
 * clients.
 *
 * @author Alexander Kanchev
 */
public class RequestHandler implements Runnable {

    private Socket socket;
    private static final String ROOT_CATALOG = "C:/HTTPServer/";

    /**
     * Constructs an object for every client who connects to the server.
     *
     * @param socket the socket connecting client and server
     */
    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * Extracts the necessary information from browser requests and sends the
     * desired files.
     */
    @Override
    public void run() {
        Logger logger = Logger.getLogger("HTTPLogger");
        PrintWriter output = null;
        try {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            Scanner inputScanner = new Scanner(inputStream);
            output = new PrintWriter(outputStream);
            FileInputStream fileInputStream;
            if (inputScanner.hasNext()) {
                String line = inputScanner.nextLine();
                String request[] = line.split(" ");
                String method = request[0];
                String path = request[1].replace("/", "");
                String version=null;
                if(request.length==3){
                    version=request[2];
                }
                logger.info("Received a request from client.");
                switch (method) {
                    case "GET": {
                        versionCheck(version);
                        fileInputStream = new FileInputStream(ROOT_CATALOG + path);
                        output.print("HTTP/1.0 200 OK\r\n\r\n");
                        output.flush();
                        copy(fileInputStream, outputStream);
                        logger.info("Sending response to client.");
                        break;
                    }
                    case "PUT": {
                        versionCheck(version);
                        throw new OperationNotSupportedException("Operation not supported!");
                    }
                    case "HEAD": {
                        versionCheck(version);
                        throw new OperationNotSupportedException("Operation not supported!");
                    }
                    case "POST": {
                        versionCheck(version);
                        throw new OperationNotSupportedException("Operation not supported!");
                    }
                    case "DELETE": {
                        versionCheck(version);
                        throw new OperationNotSupportedException("Operation not supported!");
                    }
                    default:
                        throw new IllegalArgumentException("Bad request!");
                }
            }
        } catch (IOException ex) {
            logger.info("Could not find the requested file." + ex.getMessage());
            if (output != null) {
                output.print("HTTP/1.0 404 Not Found\r\n\r\n");
                output.flush();
            }
        } catch (OperationNotSupportedException exception) {
            logger.info(exception.getMessage());
            if (output != null) {
                output.print("HTTP/1.0 501 Not Implemented\r\n\r\n");
                output.flush();
            }
        } catch (IllegalArgumentException exception) {
            logger.info(exception.getMessage());
            if (output != null) {
                output.print("HTTP/1.0 400 Bad Request\r\n\r\n");
                output.flush();
            }
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                logger.info(ex.getMessage());
            }
        }
    }

    private static void copy(final InputStream input, final OutputStream output) throws IOException {
        final byte[] buffer = new byte[1024];
        while (true) {
            int bytesRead = input.read(buffer);
            if (bytesRead == -1) {
                break;
            }
            output.write(buffer, 0, bytesRead);
            output.flush();
        }
    }

    private void versionCheck(String request) throws IllegalArgumentException {
        if (!"HTTP/1.0".equals(request) || request == null) {
            throw new IllegalArgumentException("Bad HTTP version.");
        }
    }
}
