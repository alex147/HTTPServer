package simplehttpserver;

import java.io.*;
import java.net.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author andersb
 */
public class RequestHandlerTest {

  private static final String CRLF = "\r\n";

  @Test
  public void testResponseOK() throws IOException {
    final Socket client = new Socket("localhost", 8080);

    final OutputStream output = client.getOutputStream();
    output.write(("GET /text.txt HTTP/1.0" + CRLF + CRLF).getBytes());
    output.flush();

    final BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    final String statusLine = input.readLine();
    assertEquals("HTTP/1.0 200 OK", statusLine);
    client.close();
  }

  @Test
  public void testResponseNotOK() throws IOException {
    final Socket client = new Socket("localhost", 8080);

    final OutputStream output = client.getOutputStream();
    output.write(("GET /doesNotExist.html HTTP/1.0" + CRLF + CRLF).getBytes());
    output.flush();

    final BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    final String statusLine = input.readLine();
    assertEquals("HTTP/1.0 404 Not Found", statusLine);
    client.close();
  }

  @Test
  public void testIllegalProtocol() throws IOException {
    final Socket client = new Socket("localhost", 8080);

    final OutputStream output = client.getOutputStream();
    output.write(("GET /doesNotExist.html" + CRLF + CRLF).getBytes());
    output.flush();

    final BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    final String statusLine = input.readLine();
    assertEquals("HTTP/1.0 400 Bad Request", statusLine);
    client.close();
  }

  @Test
  public void testMissingProtocol() throws IOException {
    final Socket client = new Socket("localhost", 8080);

    final OutputStream output = client.getOutputStream();
    output.write(("GET /doesNotExist.html HTTP 1.0" + CRLF + CRLF).getBytes());
    output.flush();

    final BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    final String statusLine = input.readLine();
    assertEquals("HTTP/1.0 400 Bad Request", statusLine);
    client.close();
  }

  @Test
  public void testNotImplemented() throws IOException {
    final Socket client = new Socket("localhost", 8080);

    final OutputStream output = client.getOutputStream();
    output.write(("PUT /doesNotExist.html HTTP/1.0" + CRLF + CRLF).getBytes());
    output.flush();

    final BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    final String statusLine = input.readLine();
    assertEquals("HTTP/1.0 501 Not Implemented", statusLine);
    client.close();
  }
}
