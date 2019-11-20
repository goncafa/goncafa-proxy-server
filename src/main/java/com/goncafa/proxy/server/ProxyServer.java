/*
 * Copyright (C) 2012 by Gonzalo Castillo
 * This software is provided under the GNU general public license (http://www.gnu.org/copyleft/gpl.html).
 */
package com.goncafa.proxy.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import com.goncafa.proxy.config.Config;
import com.goncafa.proxy.http.request.HttpProxyRequest;
import com.goncafa.proxy.io.SocketCloseHandler;
import com.goncafa.proxy.logger.Logger;
import com.goncafa.proxy.request.ProxyRequest;
import com.goncafa.proxy.thread.ThreadPoolManager;

/**
 * The <code>ProxyServer</code> class create an {@link java.net.ServerSocket ServerSocket}
 * to listen for new proxy request on port configured in config file.
 * 
 * <p>When a new request is accepted create a new thread to manage it and listen for new
 * requests.
 * 
 * @author Gonzalo Castillo
 * @version 1.0
 */
public class ProxyServer implements Runnable {
	/** _log is used to log messages */
	private static Logger _log = Logger.getInstance(ProxyServer.class);
	
	/** server is the instance used to listen new proxy request */
	private ServerSocket server;
	
	public void run() {
		_log.info(new String("starting goncafa-proxy-server"));
		
		// log the information of socks proxy if USE_PROXY_SOCKS is set to True
		// on config file. otherwise announce the directly attempt to internet
		// connection
		if (Config.USE_PROXY_SOCKS) {
			_log.info(new String("using the following socks proxy to connect to the internet: ") + Config.SOCKS_PROXY_HOST + new String(":") + Config.SOCKS_PROXY_PORT);
		} else {
			_log.info(new String("connecting directly to the internet"));
		}
		
		try {
			// create new server socket on config port
			server = new ServerSocket(Config.PROXY_PORT);
		} catch (IOException e) {
			_log.critical(e.getLocalizedMessage(), e);
			return;
		}

		_log.info(new String("proxy server listening on local port: ") + Config.PROXY_PORT);
		
		// create proxy demon
		while (true) {
			try {
				// listen for new request
				Socket client = server.accept();
				
				Semaphore clientSemaphore = new Semaphore(0, true);
				
				// create new proxy client
				ProxyRequest proxyRequest = new HttpProxyRequest(client, clientSemaphore);
				
				// create new thread to manage client request
				ThreadPoolManager.getInstance().execute(proxyRequest);
				
				ThreadPoolManager.getInstance().execute(new SocketCloseHandler(client, clientSemaphore));
			} catch (IOException e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}

}
