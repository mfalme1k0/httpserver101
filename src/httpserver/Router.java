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

    private static final String TEXT = "text/plain; charset=utf-8";

    /** Exercise 4: exact-match routes. Anything else is a 404. */
    public HttpResponse route(HttpRequest request) {
        String path = stripQuery(request.path());

        if (request.method().equals("GET")) {
            switch (path) {
                case "/":
                    return new HttpResponse().body("Home", TEXT);
                case "/hello":
                    return new HttpResponse().body("Hello", TEXT);
                default:
                    break;
            }
        }
        return new HttpResponse().status(404).body("Not Found", TEXT);
    }

    /** "/hello?name=x" routes as "/hello". */
    private static String stripQuery(String path) {
        int q = path.indexOf('?');
        return q < 0 ? path : path.substring(0, q);
    }
}
