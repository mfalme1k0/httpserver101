package httpserver;

/**
 * Entry point. Starts the server on 127.0.0.1:8080.
 * Usage: java -cp out httpserver.Main [port]
 */
public class Main {
    public static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        new HttpServer("127.0.0.1", port, "public").start();
    }
}
