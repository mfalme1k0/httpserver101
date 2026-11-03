package httpserver;

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
        // TODO (Exercise 1): create a ServerSocket with setReuseAddress(true),
        // bind to host:port, accept a connection and echo what you read.
        System.out.printf("Server scaffold ready for %s:%d (root=%s). Start with Exercise 1.%n",
                host, port, documentRoot);
    }
}
