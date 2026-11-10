package httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HTTP response builder.
 *
 * Exercise 6  - file-backed bodies, streamed instead of loaded into memory.
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
    private Path bodyFile; // if set, streamed from disk instead of using body

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

    /** Body streamed from a file; Content-Length is taken from the file size. */
    public HttpResponse bodyFile(Path file, long length, String contentType) {
        this.bodyFile = file;
        this.body = new byte[0];
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(length));
        return this;
    }

    public static String reasonPhrase(int status) {
        return REASONS.getOrDefault(status, "Unknown");
    }

    private byte[] headBytes() {
        StringBuilder head = new StringBuilder();
        head.append("HTTP/1.1 ").append(status).append(' ').append(reasonPhrase(status)).append("\r\n");
        for (Map.Entry<String, String> h : headers.entrySet()) {
            head.append(h.getKey()).append(": ").append(h.getValue()).append("\r\n");
        }
        head.append("\r\n");
        return head.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    /** Writes status line, headers, blank CRLF, then the body (streaming a file body). */
    public void writeTo(OutputStream out) throws IOException {
        out.write(headBytes());
        if (bodyFile != null) {
            Files.copy(bodyFile, out);
        } else {
            out.write(body);
        }
        out.flush();
    }

    /** Whole response as bytes (loads a file body into memory; prefer writeTo). */
    public byte[] toBytes() throws IOException {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        writeTo(buf);
        return buf.toByteArray();
    }
}
