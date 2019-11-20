/*
 * Copyright (C) 2012 by Gonzalo Castillo
 * This software is provided under the GNU general public license (http://www.gnu.org/copyleft/gpl.html).
 */
package com.goncafa.proxy.http.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import com.goncafa.proxy.config.Config;
import com.goncafa.proxy.http.service.HttpProxyServiceManager;
import com.goncafa.proxy.http.service.HttpSSLProxyServiceManager;
import com.goncafa.proxy.io.InputStreamConsumer;
import com.goncafa.proxy.logger.Logger;
import com.goncafa.proxy.request.Headers;
import com.goncafa.proxy.request.ProxyRequest;
import com.goncafa.proxy.service.ProxyService;
import com.goncafa.proxy.util.Timer;

public class HttpProxyRequest implements ProxyRequest {
	private static Logger _log = Logger.getInstance(HttpProxyRequest.class);

	private Socket client;
	private Semaphore clientSemaphore;

	public HttpProxyRequest(Socket client, Semaphore clientSemaphore) {
		super();
		this.client = client;
		this.clientSemaphore = clientSemaphore;
	}

	public void run() {
		/*
		 * try to read bytes from request.
		 * 
		 * wait since bytes on stream are > 0 or request timeout.
		 */
		Timer requestTimeOut = new Timer();
		requestTimeOut.schedule(Config.FIRST_REQUEST_TIMEOUT);

		try {
			while (this.client.getInputStream().available() < 1) {
				try {Thread.sleep(1);} catch (InterruptedException e) {}

				// if request timeout cancel petition
				if (requestTimeOut.isTimeout()) {
					_log.info("request canceled");
					this.clientSemaphore.release(2);
					return;
				}
			}
		} catch (IOException e) {
			_log.error(e.getLocalizedMessage(), e);
			this.clientSemaphore.release(2);
			return;
		}
		
		ByteArrayOutputStream httpBytesHeaders = new ByteArrayOutputStream();
		
		try {
			new InputStreamConsumer(this.client.getInputStream(), httpBytesHeaders, 1000L).consume();
		} catch (IOException e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		Headers request = new HttpRequestHeaders(new String(httpBytesHeaders.toByteArray()));
		
		//_log.debug(request);
		_log.debug(new String("from: ") + this.client.getInetAddress() + new String(" ") + request.getRequestMethodEnum() + " " + request.getHost() + " " + request.getPort());

		ProxyService proxy = null;

		if (request.getRequestMethodEnum() == HttpRequestMethodType.GET
				|| request.getRequestMethodEnum() == HttpRequestMethodType.POST) {
				proxy = new HttpProxyServiceManager(this.client, this.clientSemaphore);
		} else if(request.getRequestMethodEnum() == HttpRequestMethodType.CONNECT) {
				proxy = new HttpSSLProxyServiceManager(this.client, this.clientSemaphore);
		} else {
			_log.debug(request);
		}

		if (proxy != null) {
			proxy.proxy(request);
		}
	}
}
