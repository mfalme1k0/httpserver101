package httpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
    /** Case-insensitive lookup; null if the header is absent. */
    public String header(String name) { return headers.get(name); }
    public byte[] body() { return body; }

    private static final int MAX_LINE_BYTES = 8192;
    private static final int MAX_HEADER_LINES = 100;

    /** Thrown when the request is malformed; the server answers with 400. */
    public static class BadRequestException extends Exception {
        public BadRequestException(String message) { super(message); }
    }

    /**
     * Exercise 3: reads the request line and validates it.
     * Exercise 5: reads the headers into a case-insensitive map until the
     * blank line that ends the head.
     *
     * Returns null if the client closed the connection before sending anything.
     * The body is read in Exercise 7. The caller should pass a buffered stream.
     */
    public static HttpRequest parse(InputStream in) throws IOException, BadRequestException {
        String requestLine = readLine(in);
        if (requestLine == null) return null;

        // Be lenient about leading blank lines (RFC 9112 section 2.2).
        while (requestLine.isEmpty()) {
            requestLine = readLine(in);
            if (requestLine == null) return null;
        }

        String[] parts = requestLine.split(" ", -1);
        if (parts.length != 3) {
            throw new BadRequestException("Request line must have exactly 3 parts");
        }
        String method = parts[0], path = parts[1], version = parts[2];

        if (!method.matches("[A-Z]+")) {
            throw new BadRequestException("Invalid method");
        }
        if (!path.startsWith("/")) {
            throw new BadRequestException("Path must start with '/'");
        }
        if (!version.equals("HTTP/1.1") && !version.equals("HTTP/1.0")) {
            throw new BadRequestException("Unsupported HTTP version");
        }

        // Exercise 5: header lines until the empty line (CRLF CRLF).
        HttpRequest req = new HttpRequest();
        for (int i = 0; ; i++) {
            if (i > MAX_HEADER_LINES) throw new BadRequestException("Too many header lines");
            String line = readLine(in);
            if (line == null) throw new BadRequestException("Unexpected end of headers");
            if (line.isEmpty()) break;

            // Obsolete line folding (continuation lines) is rejected (RFC 9112 section 5.2).
            if (line.charAt(0) == ' ' || line.charAt(0) == '\t') {
                throw new BadRequestException("Obsolete header line folding");
            }
            int colon = line.indexOf(':');
            if (colon <= 0) {
                throw new BadRequestException("Malformed header line");
            }
            String name = line.substring(0, colon);
            // No whitespace allowed between the name and the colon.
            if (!name.matches("[!#$%&'*+.^_`|~0-9A-Za-z-]+")) {
                throw new BadRequestException("Invalid header name");
            }
            String value = line.substring(colon + 1).trim();
            // Repeated headers are combined with ", " (RFC 9110 section 5.3).
            req.headers.merge(name, value, (a, b) -> a + ", " + b);
        }

        req.method = method;
        req.path = path;
        req.version = version;
        return req;
    }

    /** Reads one line terminated by LF (CR stripped). Null on EOF before any byte. */
    private static String readLine(InputStream in) throws IOException, BadRequestException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
            if (b == '\n') {
                byte[] bytes = buf.toByteArray();
                int len = bytes.length;
                if (len > 0 && bytes[len - 1] == '\r') len--;
                return new String(bytes, 0, len, StandardCharsets.ISO_8859_1);
            }
            if (buf.size() >= MAX_LINE_BYTES) throw new BadRequestException("Line too long");
            buf.write(b);
        }
        return buf.size() == 0 ? null : new String(buf.toByteArray(), StandardCharsets.ISO_8859_1);
    }
}
