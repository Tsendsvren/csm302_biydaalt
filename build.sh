#!/usr/bin/env bash
# Build: эх кодыг out/ руу compile хийнэ. Шаардлага: JDK 17+ (javac).
set -e
mkdir -p out
javac -encoding UTF-8 -d out src/main/java/com/os/*.java
echo "Build OK -> out/"
