package httpserver;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

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

    private static final Map<String, String> MIME = Map.ofEntries(
            Map.entry("html", "text/html; charset=utf-8"),
            Map.entry("htm", "text/html; charset=utf-8"),
            Map.entry("css", "text/css; charset=utf-8"),
            Map.entry("js", "text/javascript; charset=utf-8"),
            Map.entry("json", "application/json"),
            Map.entry("txt", TEXT),
            Map.entry("xml", "application/xml"),
            Map.entry("png", "image/png"),
            Map.entry("jpg", "image/jpeg"),
            Map.entry("jpeg", "image/jpeg"),
            Map.entry("gif", "image/gif"),
            Map.entry("webp", "image/webp"),
            Map.entry("svg", "image/svg+xml"),
            Map.entry("ico", "image/x-icon"),
            Map.entry("pdf", "application/pdf"),
            Map.entry("woff2", "font/woff2"));

    /**
     * Exercise 4: GET /hello.
     * Exercise 6: every other GET is looked up as a file under the document root
     * ("/" maps to index.html); 404 if it doesn't exist or is outside the root.
     * Exercise 7: POST /echo echoes the request body; other methods -> 405.
     */
    public HttpResponse route(HttpRequest request) {
        String path = stripQuery(request.path());
        String method = request.method();

        if (method.equals("GET")) {
            if (path.equals("/hello")) {
                return new HttpResponse().body("Hello", TEXT);
            }
            return serveStatic(path);
        }

        if (method.equals("POST")) {
            if (path.equals("/echo")) {
                // Echo the raw body bytes with a sensible Content-Type.
                String ct = request.header("Content-Type");
                if (ct == null || ct.isBlank()) {
                    ct = "application/octet-stream";
                }
                return new HttpResponse().body(request.body(), ct);
            }
            // Known method, unknown path -> 404.
            return notFound();
        }

        // Unsupported method -> 405 Method Not Allowed.
        return new HttpResponse()
                .status(405)
                .header("Allow", "GET, POST")
                .body("Method Not Allowed", TEXT);
    }

    private HttpResponse serveStatic(String urlPath) {
        // Never trust the client's path: decode it, resolve it against the root,
        // and verify the result is still inside the root (also after symlinks).
        String decoded;
        try {
            decoded = URLDecoder.decode(urlPath.replace("+", "%2B"), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return new HttpResponse().status(400).body("Bad Request: invalid percent-encoding", TEXT);
        }
        if (decoded.indexOf('\0') >= 0) {
            return new HttpResponse().status(400).body("Bad Request: invalid path", TEXT);
        }

        // Drop every leading '/', otherwise resolve() would treat it as an absolute path.
        int i = 0;
        while (i < decoded.length() && decoded.charAt(i) == '/') i++;
        String relative = decoded.substring(i);
        if (relative.isEmpty() || relative.endsWith("/")) {
            relative += "index.html"; // "/" -> index.html (no directory listings)
        }

        try {
            Path root = Path.of(documentRoot).toRealPath();
            Path file = root.resolve(relative).normalize();
            if (!file.startsWith(root)) return notFound();      // ../ traversal

            if (!Files.isRegularFile(file)) return notFound();  // missing, or a directory
            Path real = file.toRealPath();
            if (!real.startsWith(root)) return notFound();      // symlink pointing outside

            return new HttpResponse().bodyFile(real, Files.size(real), contentType(real));
        } catch (InvalidPathException | IOException e) {
            return notFound();
        }
    }

    private static String contentType(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        String ext = dot < 0 ? "" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
        return MIME.getOrDefault(ext, "application/octet-stream");
    }

    private static HttpResponse notFound() {
        return new HttpResponse().status(404).body("Not Found", TEXT);
    }

    /** "/hello?name=x" routes as "/hello". */
    private static String stripQuery(String path) {
        int q = path.indexOf('?');
        return q < 0 ? path : path.substring(0, q);
    }
}
