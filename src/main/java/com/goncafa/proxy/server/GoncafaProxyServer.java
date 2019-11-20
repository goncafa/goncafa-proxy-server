/*
 * Copyright (C) 2012 by Gonzalo Castillo
 * This software is provided under the GNU general public license (http://www.gnu.org/copyleft/gpl.html).
 */
package com.goncafa.proxy.server;

import com.goncafa.proxy.thread.ThreadPoolManager;

public class GoncafaProxyServer {
	public static void main(String[] args) {
		ThreadPoolManager.getInstance().execute(new ProxyServer());
	}
}
