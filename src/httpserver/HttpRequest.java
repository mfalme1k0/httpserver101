package httpserver;

import java.util.Map;
import java.util.TreeMap;

/**
 * Parsed HTTP request.
 *
 * Exercise 3 - method, path, version.
 * Exercise 5 - case-insensitive headers.
 * Exercise 7 - request body (bytes).
 */
public class HttpRequest {
    private String method;
    private String path;
    private String version;
    private final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private byte[] body = new byte[0];

    public String method() { return method; }
    public String path() { return path; }
    public String version() { return version; }
    public Map<String, String> headers() { return headers; }
    public byte[] body() { return body; }

    // TODO (Exercise 3/5/7): add a static parse(InputStream) factory that
    // validates the request line, reads headers until the blank line, and
    // reads exactly Content-Length bytes of body (with a max size limit).
}
