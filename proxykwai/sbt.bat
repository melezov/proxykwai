@echo off
setlocal

set PROJECT=ProxyKwai

if %1.==. set DEFAULT=shell
call "%~dp0..\sbt.bat" "project %PROJECT%" %DEFAULT% %*
