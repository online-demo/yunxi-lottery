@echo off & setlocal enabledelayedexpansion

set LIB_JARS=""
cd lib
echo %~dp0
for %%i in (*) do set LIB_JARS=!LIB_JARS!;%CD%\%%i
echo %LIB_JARS%
cd ../
echo %~dp0

start %CD%\bin\bin\java -server -classpath %CD%\resources;%LIB_JARS% com.vteam.lucky.lottery.ServerMain
goto end

:end
