/*
 * Copyright (C) 2012 by Gonzalo Castillo
 * This software is provided under the GNU general public license (http://www.gnu.org/copyleft/gpl.html).
 */
package com.goncafa.proxy.io;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.goncafa.proxy.logger.Logger;

public class SocketCloseHandler implements Runnable {
	private static Logger _log = Logger.getInstance(SocketCloseHandler.class);
	
	private Socket socket;
	private List<Semaphore> semaphores;
	
	public SocketCloseHandler(Socket socket) {
		super();
		this.socket = socket;
	}
	
	public SocketCloseHandler(Socket socket, Semaphore semaphore) {
		super();
		this.socket = socket;
		this.addSemaphore(semaphore);
	}
	
	public boolean addSemaphore(Semaphore semaphore) {
		if (this.semaphores == null) {
			this.semaphores = new ArrayList<Semaphore>();
		}
		
		return this.semaphores.add(semaphore);
	}

	public void run() {
		for (Semaphore semaphore : this.semaphores) {
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
		
		try {
			this.socket.getInputStream().close();
		} catch(Exception e) {}

		try {
			this.socket.getOutputStream().close();
		} catch(Exception e) {}

		try {
			this.socket.close();
		} catch(Exception e) {}
	}

}
