#!/bin/bash

while :; do
	java -Xms512m -Xmx512m -cp goncafa-proxy-server-1.0.jar com.goncafa.proxy.server.GoncafaProxyServer > ./logs/console.log
	[ $? -ne 2 ] && break
	sleep 10
done
