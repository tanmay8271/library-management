@echo off
if not exist out mkdir out
javac -d out src\library\*.java test\library\*.java || exit /b 1
java -cp out library.LibraryTest
