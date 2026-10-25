@echo off
call build.bat || exit /b 1
if not exist out-tests mkdir out-tests
javac -encoding UTF-8 -cp out -d out-tests tests\AlgorithmTests.java
java -Dstdout.encoding=UTF-8 -Dfile.encoding=UTF-8 -cp out;out-tests com.os.AlgorithmTests
