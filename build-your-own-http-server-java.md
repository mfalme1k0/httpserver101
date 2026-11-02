# Build Your Own HTTP Server (Java)

**A LeetCode-style progressive course**

Learn HTTP by building a real HTTP/1.1 server from scratch using only
the Java standard library.

Each exercise builds directly on the previous one. Keep evolving the
same codebase rather than starting a new project for every exercise.

------------------------------------------------------------------------

## Table of Contents

1.  [Core Documentation](#core-documentation)
2.  [Exercise 1 --- TCP Echo Server](#exercise-1--tcp-echo-server)
3.  [Exercise 2 --- Fixed HTTP
    Response](#exercise-2--fixed-http-response)
4.  [Exercise 3 --- Parse the Request
    Line](#exercise-3--parse-the-request-line)
5.  [Exercise 4 --- Simple Routing](#exercise-4--simple-routing)
6.  [Exercise 5 --- Parse Headers](#exercise-5--parse-headers)
7.  [Exercise 6 --- Serve Static Files](#exercise-6--serve-static-files)
8.  [Exercise 7 --- Handle Request Bodies
    (POST)](#exercise-7--handle-request-bodies-post)
9.  [Exercise 8 --- Concurrent
    Connections](#exercise-8--concurrent-connections)
10. [Exercise 9 --- HTTP Keep-Alive](#exercise-9--http-keep-alive)
11. [Exercise 10 --- Clean Architecture, Status Codes, and
    Logging](#exercise-10--clean-architecture-status-codes-and-logging)
12. [Bonus Challenges](#bonus-challenges)
13. [Recommended Workflow](#recommended-workflow)

------------------------------------------------------------------------

## Core Documentation

Use these references while working through the exercises:

-   [Java Networking
    (`java.net`)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/package-summary.html)
-   [ServerSocket](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/ServerSocket.html)
-   [Socket](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/Socket.html)
-   [MDN --- HTTP
    Overview](https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Overview)
-   [MDN --- HTTP
    Messages](https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages)
-   [RFC 9110 --- HTTP
    Semantics](https://www.rfc-editor.org/rfc/rfc9110.html)
-   [RFC 9112 --- HTTP/1.1
    Messaging](https://www.rfc-editor.org/rfc/rfc9112.html)

------------------------------------------------------------------------

## Exercise 1 --- TCP Echo Server

### What you need to know

HTTP sits on top of TCP. Before you can speak HTTP, you must be able to
create a listening socket, accept connections, read bytes, and write
bytes.

**Key classes:** `ServerSocket`, `Socket`, `InputStream`, and
`OutputStream`.

### Best practices

-   Always use try-with-resources so sockets are closed properly.
-   Call `serverSocket.setReuseAddress(true)` so you can restart the
    server quickly.
-   Prefer binding to `127.0.0.1` during development.
-   Never assume a single `read()` will get the entire message. Network
    data arrives in chunks.

### Task

Create a server on port `8080` that accepts one connection and echoes
back everything the client sends.

### Test

``` bash
echo "hello" | nc 127.0.0.1 8080
```

------------------------------------------------------------------------

## Exercise 2 --- Fixed HTTP Response

### What you need to know

An HTTP response is text with a strict format:

``` http
HTTP/1.1 200 OK\r\n
Header-Name: value\r\n
\r\n
body
```

The blank line (`\r\n\r\n`) is mandatory. `Content-Length` must match
the body size exactly.

### Best practices

-   Always send `\r\n`, not just `\n`.
-   Calculate `Content-Length` correctly: it is the number of **bytes**,
    not characters.
-   Start with a simple fixed response before trying to parse anything.

### Task

Ignore the request content and always reply with a valid `200 OK`
response containing the body:

``` text
Hello, World!
```

### Test

``` bash
curl -v http://127.0.0.1:8080/
```

------------------------------------------------------------------------

## Exercise 3 --- Parse the Request Line

### What you need to know

The first line of every HTTP request looks like this:

``` http
METHOD PATH HTTP-VERSION
```

For example:

``` http
GET /hello HTTP/1.1
```

You must stop reading the headers when you see the empty line
(`\r\n\r\n`).

### Best practices

-   Read line by line, or buffer data until `\r\n\r\n`.
-   Validate that the request line has exactly three parts.
-   Reject obviously malformed requests early.

### Task

Parse and print the method and path. Continue returning the fixed
`Hello, World!` response.

------------------------------------------------------------------------

## Exercise 4 --- Simple Routing

### What you need to know

Routing means looking at the request path (and later the method) and
deciding what response to generate. Start with exact string matches.

### Best practices

-   Keep routing logic separate from socket handling.
-   Return proper status codes, such as `200` and `404`.
-   Always set `Content-Type` and `Content-Length`.

### Task

Implement these routes:

  Request           Response
  ----------------- ----------------------------
  `GET /`           `200 OK` with body `Home`
  `GET /hello`      `200 OK` with body `Hello`
  Everything else   `404 Not Found`

------------------------------------------------------------------------

## Exercise 5 --- Parse Headers

### What you need to know

Headers come after the request line, one per line, until a blank line.
Header names are case-insensitive.

Important headers include:

-   `Host`
-   `Content-Length`
-   `Connection`
-   `User-Agent`

### Best practices

-   Store headers in a `Map` (preferably a `TreeMap` with
    `String.CASE_INSENSITIVE_ORDER`).
-   Trim header values.
-   Never trust header values; they come from the client.

### Task

Parse all headers into a map and print at least `Host` and `User-Agent`.
Keep the routing from the previous exercise.

------------------------------------------------------------------------

## Exercise 6 --- Serve Static Files

### What you need to know

A large part of what a real web server does is serve files from disk:
HTML, CSS, JavaScript, images, and other assets.

The flow is:

1.  Take the request path.
2.  Map it to a file under a safe root directory (`public/` or
    `static/`).
3.  Read the file.
4.  Return it with the correct `Content-Type` and `Content-Length`.
5.  Return `404 Not Found` if the file does not exist.

### Important security rule

Never trust the path coming from the client. Requests such as
`/../../etc/passwd` must be blocked.

Always resolve the path against your root directory and verify that the
final canonical/normalized path is still inside that root.

### Best practices

-   Create a dedicated `public/` folder.
-   Map `/` to `public/index.html`.
-   Use a simple MIME type map.
-   Prefer streaming larger files instead of loading everything into
    memory.
-   Return proper status codes, such as `200` and `404`.
-   Never enable directory listing in early versions.

### Task

1.  Create a `public/` directory.
2.  Add at least:
    -   `index.html`
    -   `style.css`
    -   `hello.txt`
3.  Implement:
    -   `GET /` → serve `public/index.html`.
    -   Any other existing file under `public/` → serve that file.
    -   Non-existent files → return `404 Not Found`.
4.  Protect against path traversal (`../../..`).
5.  Set the correct `Content-Type` for each file type.

### Tests

``` bash
curl -v http://127.0.0.1:8080/
curl -v http://127.0.0.1:8080/style.css
curl -v http://127.0.0.1:8080/hello.txt
curl -v http://127.0.0.1:8080/does-not-exist
```

------------------------------------------------------------------------

## Exercise 7 --- Handle Request Bodies (POST)

### What you need to know

When `Content-Length` is present, the request body follows the headers
and contains exactly that many bytes. You must read precisely that
amount.

### Best practices

-   Never trust `Content-Length` blindly; enforce a maximum body size.
-   Use a byte buffer of the required size.
-   Return `400 Bad Request` if `Content-Length` is missing or invalid.
-   Return `405 Method Not Allowed` for unsupported methods.

### Task

Implement `POST /echo`. It should read the request body and send it back
unchanged.

### Test

``` bash
curl -v -X POST -d "hello body" http://127.0.0.1:8080/echo
```

------------------------------------------------------------------------

## Exercise 8 --- Concurrent Connections

### What you need to know

A single-threaded accept-and-handle loop can serve only one client at a
time. Real servers use threads or a thread pool.

### Best practices

-   Prefer an `ExecutorService` (fixed or cached thread pool) over
    creating raw threads.
-   Make sure any shared state is thread-safe.
-   The accept loop should never block on request handling.

### Task

After accepting a connection, hand it to a worker thread so the main
thread can immediately accept new clients.

------------------------------------------------------------------------

## Exercise 9 --- HTTP Keep-Alive

### What you need to know

In HTTP/1.1, the default is to keep the TCP connection open so the
client can send multiple requests. You must detect `Connection: close`
and handle multiple complete requests on the same socket.

### Best practices

-   Support persistent connections by default.
-   Implement a reasonable idle timeout.
-   Be careful with partial reads: you may receive multiple requests or
    only part of one.

### Task

Allow multiple requests on the same connection and respect the
`Connection` header.

------------------------------------------------------------------------

## Exercise 10 --- Clean Architecture, Status Codes, and Logging

### What you need to know

By now the code has grown. It is time to separate concerns so the server
is maintainable and testable.

### Best practices

Create clear classes:

-   `HttpServer`
-   `HttpRequest`
-   `HttpResponse`
-   `Router`

Also:

-   Centralize status-code-to-reason-phrase mapping.
-   Add simple access logging (`METHOD path → status`).
-   Prefer composition over a giant "god class".

### Task

Refactor the server into clean classes, support common status codes
properly (`200`, `400`, `404`, `405`, and `500`), and add basic logging.

------------------------------------------------------------------------

## Bonus Challenges

Attempt these after completing the core path:

-   Query-string parsing.
-   A simple middleware system.
-   JSON responses (`Content-Type: application/json`).
-   Non-blocking I/O with `java.nio`.
-   TLS support with `SSLServerSocket`.
-   A tiny framework-style API, such as `@Get("/hello")`.

------------------------------------------------------------------------

## Recommended Workflow

For every exercise:

1.  Finish the exercise completely.
2.  Test it thoroughly with `curl -v` (and the provided test command
    where applicable).
3.  Commit the working version.
4.  Only then start the next exercise, building on the same codebase.

**The goal:** finish with a working HTTP/1.1 server that you understand
because you built it piece by piece.
