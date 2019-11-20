package com.goncafa.proxy.test;

import java.io.StringBufferInputStream;
import java.io.StringReader;

import com.goncafa.proxy.config.Config;
import com.goncafa.proxy.logger.Logger;
import com.goncafa.proxy.server.ProxyServer;
import com.goncafa.proxy.thread.ThreadPoolManager;
import com.goncafa.proxy.util.Timer;

public class ProxyServerTest {
	private static Logger _log = Logger.getInstance(ProxyServerTest.class);
	
	public static void main(String[] args) {
		Timer timer = new Timer();
		timer.schedule(5000L);
	}
}
