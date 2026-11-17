@echo off
cd /d "%~dp0"
echo Compiling Text-Based RPG GUI...
javac -Xlint:all *.java
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)
echo Starting the graphical game...
java GameGUI
