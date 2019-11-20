/*
 * Copyright (C) 2012 by Gonzalo Castillo
 * This software is provided under the GNU general public license (http://www.gnu.org/copyleft/gpl.html).
 */
package com.goncafa.proxy.http.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import com.goncafa.proxy.config.Config;
import com.goncafa.proxy.io.InputStreamConsumer;
import com.goncafa.proxy.io.SocketCloseHandler;
import com.goncafa.proxy.logger.Logger;
import com.goncafa.proxy.request.Headers;
import com.goncafa.proxy.service.ProxyService;
import com.goncafa.proxy.thread.ThreadPoolManager;

public class HttpSSLProxyServiceManager implements ProxyService {
	private static Logger _log = Logger.getInstance(HttpSSLProxyServiceManager.class);

	private Socket client;
	private Semaphore clientSemaphore;

	public HttpSSLProxyServiceManager(Socket client, Semaphore clientSemaphore) {
		super();
		this.client = client;
		this.clientSemaphore = clientSemaphore;
	}

	public void proxy(Headers request) {
		_log.debug(request);
		Socket to;
		
		try {
			if (Config.USE_PROXY_SOCKS) {
				to = new Socket(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(Config.SOCKS_PROXY_HOST, Config.SOCKS_PROXY_PORT)));
			} else {
				to = new Socket();
			}
			
			to.connect(new InetSocketAddress(request.getHost(), request.getPort()));

			this.client.getOutputStream().write("HTTP/1.1 200 Connection established\r\nProxy-connection: Keep-alive\r\n\r\n".getBytes());
			this.client.getOutputStream().flush();
		} catch (IOException e) {
			_log.error(e.getLocalizedMessage(), e);
			this.clientSemaphore.release(2);
			return;
		}
		
		Semaphore clientSemaphore = new Semaphore(0, true);
		Semaphore toSemaphore = new Semaphore(0, true);
		
		SocketCloseHandler toSocketCloseHandler = new SocketCloseHandler(to);
		toSocketCloseHandler.addSemaphore(clientSemaphore);
		toSocketCloseHandler.addSemaphore(toSemaphore);
		
		// if port is 443 set timeout to 3 seconds
		// if port is not 443 set no timeout
		Long timeout = null;
		if (request.getPort() == 443) {
			timeout = Config.SSL_443_READ_TIMEOUT;
		} else {
			timeout = Config.SSL_NNN_READ_TIMEOUT;
		}

		try {
			ThreadPoolManager.getInstance().execute(new InputStreamConsumer(this.client.getInputStream(), to.getOutputStream(), timeout, clientSemaphore));
		} catch (IOException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		
		try {
			ThreadPoolManager.getInstance().execute(new InputStreamConsumer(to.getInputStream(), this.client.getOutputStream(), timeout, toSemaphore));
		} catch (IOException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		
		ThreadPoolManager.getInstance().execute(toSocketCloseHandler);
		
		try {
			clientSemaphore.acquire();
			toSemaphore.acquire();
		} catch (InterruptedException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		
		this.clientSemaphore.release(2);
	}
}
