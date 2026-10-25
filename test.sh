#!/usr/bin/env bash
# Test: автомат тестүүдийг (GUI шаардахгүй) compile хийж ажиллуулна.
set -e
./build.sh
mkdir -p out-tests
javac -encoding UTF-8 -cp out -d out-tests tests/AlgorithmTests.java
java -Dstdout.encoding=UTF-8 -Dfile.encoding=UTF-8 -cp out:out-tests com.os.AlgorithmTests
