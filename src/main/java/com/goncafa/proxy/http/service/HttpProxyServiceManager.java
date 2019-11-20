/*
 * Copyright (C) 2012 by Gonzalo Castillo
 * This software is provided under the GNU general public license (http://www.gnu.org/copyleft/gpl.html).
 */
package com.goncafa.proxy.http.service;

import java.io.IOException;
import java.io.OutputStream;
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

public class HttpProxyServiceManager implements ProxyService {
	private static Logger _log = Logger.getInstance(HttpProxyServiceManager.class);

	private Socket client;
	private Semaphore clientSemaphore;

	public HttpProxyServiceManager(Socket client, Semaphore clientSemaphore) {
		super();
		this.client = client;
		this.clientSemaphore = clientSemaphore;
	}

	public void proxy(Headers request) {
			Socket to;
			OutputStream toOut;
			
			try {
				if (Config.USE_PROXY_SOCKS) {
					to = new Socket(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(Config.SOCKS_PROXY_HOST, Config.SOCKS_PROXY_PORT)));
				} else {
					to = new Socket();
				}
				
				to.connect(new InetSocketAddress(request.getHost(), request.getPort()));
				
				toOut = to.getOutputStream();
				toOut.write(request.toString().getBytes());
				toOut.flush();
			} catch (IOException e) {
				_log.error(request.toString());
				_log.error(e.getLocalizedMessage(), e);
				this.clientSemaphore.release(2);
				return;
			}
			
			try {
				ThreadPoolManager.getInstance().execute(new InputStreamConsumer(to.getInputStream(), this.client.getOutputStream(), Config.HTTP_BYTES_WAIT_TIMEOUT, this.clientSemaphore));
				ThreadPoolManager.getInstance().execute(new SocketCloseHandler(to, this.clientSemaphore));
			} catch (IOException e) {
				_log.error(request.toString());
				_log.error(e.getLocalizedMessage(), e);
				this.clientSemaphore.release(2);
			}
	}
	
}
