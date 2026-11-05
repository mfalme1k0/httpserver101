package httpserver;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Owns the listening socket and the accept loop.
 *
 * Exercise 1  - accept one connection and echo bytes back.
 * Exercise 2  - reply with a fixed 200 OK response.
 * Exercise 8  - hand each connection to an ExecutorService.
 * Exercise 9  - keep-alive loop per connection.
 * Exercise 10 - delegate to Router, add access logging.
 */
public class HttpServer {
    private final String host;
    private final int port;
    private final String documentRoot;

    public HttpServer(String host, int port, String documentRoot) {
        this.host = host;
        this.port = port;
        this.documentRoot = documentRoot;
    }

    public void start() throws Exception {
        // Exercise 2: Fixed HTTP Response
        // Ignore the request content and always reply with 200 OK / "Hello, World!".
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName(host), port));
            System.out.printf("HTTP server listening on %s:%d%n", host, port);

            // Handle clients one at a time (concurrency comes in Exercise 8).
            while (true) {
                try (Socket client = serverSocket.accept()) {
                    client.setSoTimeout(5000);
                    drainRequestHead(client.getInputStream());

                    HttpResponse response = new HttpResponse()
                            .header("Connection", "close")
                            .body("Hello, World!", "text/plain; charset=utf-8");

                    OutputStream out = client.getOutputStream();
                    out.write(response.toBytes());
                    out.flush();
                } catch (IOException e) {
                    System.err.println("Connection error: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Reads (and discards) bytes until the blank line (CRLF CRLF) that ends the
     * request head, so the client isn't reset while still sending.
     * Partial reads are fine: we scan byte by byte through a buffered stream.
     */
    private static void drainRequestHead(InputStream raw) throws IOException {
        InputStream in = new BufferedInputStream(raw);
        int matched = 0; // progress through "\r\n\r\n"
        int b;
        while ((b = in.read()) != -1) {
            if ((matched % 2 == 0 && b == '\r') || (matched % 2 == 1 && b == '\n')) {
                if (++matched == 4) return;
            } else {
                matched = (b == '\r') ? 1 : 0;
            }
        }
    }
}
