package httpserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
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
        // Exercise 1: TCP Echo Server
        // Create a ServerSocket with setReuseAddress(true), bind to host:port,
        // accept a connection and echo what you read.
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new java.net.InetSocketAddress(InetAddress.getByName(host), port));
            System.out.printf("Echo server listening on %s:%d%n", host, port);

            // Accept one connection (Exercise 1 requirement)
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.printf("Client connected from %s:%d%n",
                        clientSocket.getInetAddress().getHostAddress(),
                        clientSocket.getPort());

                InputStream in = clientSocket.getInputStream();
                OutputStream out = clientSocket.getOutputStream();

                // Echo everything the client sends. Network data arrives in chunks,
                // so we loop until the client closes the connection (read returns -1).
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    out.flush();
                }

                System.out.println("Client disconnected. Echo complete.");
            }
        }
    }
}