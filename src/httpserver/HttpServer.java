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
        // Exercise 3: parse the request line, print method + path,
        // and keep returning the fixed "Hello, World!" response.
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName(host), port));
            System.out.printf("HTTP server listening on %s:%d%n", host, port);

            // Handle clients one at a time (concurrency comes in Exercise 8).
            while (true) {
                try (Socket client = serverSocket.accept()) {
                    client.setSoTimeout(5000);
                    InputStream in = new BufferedInputStream(client.getInputStream());

                    HttpResponse response;
                    try {
                        HttpRequest request = HttpRequest.parse(in);
                        if (request == null) continue; // client connected and left
                        System.out.printf("Method: %s  Path: %s%n", request.method(), request.path());
                        response = new HttpResponse()
                                .header("Connection", "close")
                                .body("Hello, World!", "text/plain; charset=utf-8");
                    } catch (HttpRequest.BadRequestException e) {
                        System.out.println("Bad request: " + e.getMessage());
                        response = new HttpResponse()
                                .status(400)
                                .header("Connection", "close")
                                .body("Bad Request: " + e.getMessage(), "text/plain; charset=utf-8");
                    }

                    OutputStream out = client.getOutputStream();
                    out.write(response.toBytes());
                    out.flush();
                } catch (IOException e) {
                    System.err.println("Connection error: " + e.getMessage());
                }
            }
        }
    }
}
