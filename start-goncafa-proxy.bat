@echo off
title goncafa-proxy-server-console
:start
echo goncafa-proxy-server.
echo.
java -Xms40m -Xmx40m -cp goncafa-proxy-server-1.0.jar com.goncafa.proxy.server.GoncafaProxyServer
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo restarting ...
echo.
goto start
:error
echo.
echo server terminated abnormaly
echo.
:end
echo.
echo server terminated
echo.
pause