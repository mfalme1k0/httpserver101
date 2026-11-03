#!/usr/bin/env bash
# Quick checks for the curl-based exercises. Start the server first.
BASE=${1:-http://127.0.0.1:8080}
set -x
curl -v "$BASE/"
curl -v "$BASE/hello"
curl -v "$BASE/style.css"
curl -v "$BASE/hello.txt"
curl -v "$BASE/does-not-exist"
curl -v --path-as-is "$BASE/../../etc/passwd"
curl -v -X POST -d "hello body" "$BASE/echo"
