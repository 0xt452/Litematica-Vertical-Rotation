@echo off
setlocal
cd /d "%~dp0"
echo Building the experimental Litematica vertical rotation mod for Minecraft 1.21.11...
echo This requires a Java 21 JDK and an internet connection.
call gradlew.bat --gradle-user-home "%~dp0.gradle-user-home" build
if errorlevel 1 goto failed
echo.
echo Build succeeded. The playable mod JAR is:
echo %~dp0build\libs\litematica-fabric-1.21.11-0.26.16+vertical.1.jar
echo Read VERTICAL-ROTATION.md before installing.
pause
exit /b 0
:failed
echo.
echo Build failed. Please retain the error output above.
pause
exit /b 1
