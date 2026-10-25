@echo off
REM Build: JDK 17+ шаардлагатай
if not exist out mkdir out
javac -encoding UTF-8 -d out src\main\java\com\os\*.java
if errorlevel 1 (echo Build FAILED & exit /b 1)
echo Build OK -^> out\
