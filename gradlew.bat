@rem Gradle wrapper script para Windows
@rem Android Studio usa su propio Gradle integrado, pero este archivo
@rem es necesario para que el proyecto sea reconocido correctamente.

@if "%DEBUG%"=="" @echo off
@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set APP_BASE_NAME=%~n0
set APP_HOME=%~dp0

set CLASSPATH=%APP_HOME%gradle\wrapper\gradle-wrapper.jar

:end
@rem Return error code
exit /b %EXIT_CODE%
