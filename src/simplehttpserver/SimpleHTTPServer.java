/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simplehttpserver;

import com.sun.org.apache.bcel.internal.generic.LOOKUPSWITCH;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Admin
 */
public class SimpleHTTPServer {

    /**
     * @param args the command line arguments
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final ServerSocket serverSocket = new ServerSocket(8080);
        final ExecutorService pool = Executors.newCachedThreadPool();
        final Logger logger = Logger.getLogger("HTTPLogger");
        final FileHandler handler = new FileHandler("C:/HTTPServer/HTTPLog.txt");
        handler.setFormatter(new SimpleFormatter());
        logger.addHandler(handler);
        logger.info("Starting up the server.");
        while (true) {
            final Socket connection = serverSocket.accept();
            final RequestHandler requestHandler = new RequestHandler(connection);
            pool.execute(requestHandler);
        }
    }
}
