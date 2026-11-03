package httpserver;

/**
 * Maps a request to a response. Keep this free of socket code.
 *
 * Exercise 4  - GET /, GET /hello, 404 otherwise.
 * Exercise 6  - static files from public/ with path-traversal protection.
 * Exercise 7  - POST /echo, 405 for unsupported methods.
 */
public class Router {
    private final String documentRoot;

    public Router(String documentRoot) {
        this.documentRoot = documentRoot;
    }

    public HttpResponse route(HttpRequest request) {
        // TODO (Exercise 4+): dispatch on method and path.
        return new HttpResponse().status(404).body("Not Found", "text/plain; charset=utf-8");
    }
}
