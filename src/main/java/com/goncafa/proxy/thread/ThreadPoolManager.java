/*
 * Copyright (C) 2012 by Gonzalo Castillo
 * This software is provided under the GNU general public license (http://www.gnu.org/copyleft/gpl.html).
 */
package com.goncafa.proxy.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.goncafa.proxy.config.Config;

public class ThreadPoolManager {
	private static ThreadPoolManager instance;
	
	private ExecutorService executorService;
	
	public void execute(Runnable runnable) {
		this.executorService.execute(runnable);
	}
	
	public static ThreadPoolManager getInstance() {
		if (ThreadPoolManager.instance == null) {
			ThreadPoolManager.instance = new ThreadPoolManager();
		}
		
		return ThreadPoolManager.instance;
	}
	
	private ThreadPoolManager() {
		super();
		this.executorService = Executors.newFixedThreadPool(Config.MAX_THREADS_ON_POOL);
	}
}
