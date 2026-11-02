# Build Your Own HTTP Server in Java

Build an HTTP/1.1 server from the ground up using Java's standard
library.

This repository is a progressive, hands-on learning project. Instead of
relying on a web framework, you'll work through the networking and HTTP
fundamentals yourself---starting with TCP sockets and gradually building
toward request parsing, routing, static-file serving, request bodies,
concurrency, persistent connections, and a cleaner server architecture.

The exercises are designed to be completed **in order, in one evolving
codebase**.

## Learning objectives

By the end of the core exercises, you should have practical experience
with:

-   TCP sockets and client/server communication.
-   HTTP/1.1 request and response message structure.
-   Request-line and header parsing.
-   Basic method-and-path routing.
-   HTTP status codes and response headers.
-   Serving static files safely from a document root.
-   Reading request bodies, including `POST` requests.
-   Handling multiple clients concurrently.
-   Supporting persistent connections with HTTP keep-alive.
-   Separating server responsibilities into maintainable classes.
-   Basic request logging and error handling.

## Course roadmap

  ------------------------------------------------------------------------
                            \# Exercise              What you'll build
  ---------------------------- --------------------- ---------------------
                             1 TCP Echo Server       A server that accepts
                                                     a connection and
                                                     echoes received data.

                             2 Fixed HTTP Response   A server that returns
                                                     a valid `200 OK`
                                                     response.

                             3 Parse the Request     Basic parsing of the
                               Line                  HTTP method and
                                                     request path.

                             4 Simple Routing        Different responses
                                                     for `GET /`,
                                                     `GET /hello`, and
                                                     unknown paths.

                             5 Parse Headers         Case-insensitive
                                                     request-header
                                                     storage and
                                                     inspection.

                             6 Serve Static Files    File serving from a
                                                     `public/` directory,
                                                     including
                                                     path-traversal
                                                     protection.

                             7 Handle Request Bodies A `POST /echo`
                                                     endpoint that returns
                                                     the submitted body.

                             8 Concurrent            Worker threads
                               Connections           managed through an
                                                     `ExecutorService`.

                             9 HTTP Keep-Alive       Multiple requests
                                                     over one TCP
                                                     connection.

                            10 Architecture, Status  A refactored server
                               Codes, and Logging    with separate HTTP
                                                     components and basic
                                                     access logs.
  ------------------------------------------------------------------------

The course also includes optional challenges such as query-string
parsing, middleware, JSON responses, non-blocking I/O, TLS, and a small
annotation-based routing API.

## Requirements

-   A Java Development Kit (JDK).
-   A terminal.
-   `curl` for testing HTTP endpoints.
-   `nc` (Netcat) for the TCP echo exercise.

The course uses Java's standard library for the server implementation.
No web framework is required.

## Getting started

1.  Clone the repository:

    ``` bash
    git clone <repository-url>
    cd <repository-directory>
    ```

2.  Open the project in your preferred Java IDE or editor.

3.  Read the course guide:
    [`BUILD_YOUR_OWN_HTTP_SERVER.md`](BUILD_YOUR_OWN_HTTP_SERVER.md).

4.  Start with **Exercise 1 --- TCP Echo Server**.

5.  Complete each exercise before moving to the next one. Keep extending
    the same codebase.

> **Note:** The repository's course guide is the source of truth for the
> exercise requirements and test commands. Add build or run commands
> here once the project has a defined build setup and entry point.

## Suggested workflow

For each exercise:

1.  Read the concepts and best practices.
2.  Implement the task in the existing codebase.
3.  Run the provided test command and try additional edge cases.
4.  Check that earlier exercises still work.
5.  Commit the working version.
6.  Move on to the next exercise.

Use `curl -v` to inspect HTTP status lines, response headers, and
bodies. For the first TCP exercise, use the provided `nc` command.

## Suggested project structure

The exact structure is up to you as the implementation evolves. By the
final exercise, aim to separate the main responsibilities into
components such as:

``` text
.
├── src/
│   └── ...
├── public/
│   ├── index.html
│   ├── style.css
│   └── hello.txt
├── BUILD_YOUR_OWN_HTTP_SERVER.md
└── README.md
```

The `public/` directory is used in Exercise 6 for static assets. The
Java source layout can evolve as you introduce the server, request,
response, and router components.

## Core documentation

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

## Scope and security

This is a learning project, not a production-ready web server. The
exercises introduce important protocol and security concerns, but
completing them does not automatically make the server suitable for
public deployment.

Pay particular attention to:

-   Correctly framing HTTP messages and calculating `Content-Length` in
    bytes.
-   Handling partial socket reads.
-   Validating request lines and headers.
-   Limiting request-body sizes.
-   Preventing path traversal when serving files.
-   Managing sockets, worker threads, and connection timeouts.
-   Returning appropriate error responses.

Do not expose an unfinished implementation to the public internet.

## Project status

The repository is structured as a progressive implementation course.
Track your progress by completing and committing each exercise in
sequence.

------------------------------------------------------------------------

**Build it. Test it. Understand it.**
