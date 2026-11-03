package httpserver;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HTTP response builder.
 *
 * Exercise 2  - status line, headers, Content-Length in bytes, CRLF framing.
 * Exercise 10 - central status-code -> reason-phrase mapping.
 */
public class HttpResponse {
    private static final Map<Integer, String> REASONS = Map.of(
            200, "OK",
            400, "Bad Request",
            404, "Not Found",
            405, "Method Not Allowed",
            500, "Internal Server Error");

    private int status = 200;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public HttpResponse status(int status) { this.status = status; return this; }
    public int status() { return status; }
    public HttpResponse header(String name, String value) { headers.put(name, value); return this; }

    public HttpResponse body(String text, String contentType) {
        return body(text.getBytes(StandardCharsets.UTF_8), contentType);
    }

    public HttpResponse body(byte[] bytes, String contentType) {
        this.body = bytes;
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(bytes.length));
        return this;
    }

    public static String reasonPhrase(int status) {
        return REASONS.getOrDefault(status, "Unknown");
    }

    // TODO (Exercise 2): serialize to bytes: "HTTP/1.1 <status> <reason>\r\n",
    // headers each ending in \r\n, a blank \r\n, then the body.
}
