/*
 * Copyright (C) 2012 by Gonzalo Castillo
 * This software is provided under the GNU general public license (http://www.gnu.org/copyleft/gpl.html).
 */
package com.goncafa.proxy.util;

import java.util.TimerTask;

public class Timer {
	private java.util.Timer timer;
	private TimeoutListener timeoutListener;
	private boolean timeout;
	
	public Timer() {
		super();
		this.timeout = false;
	}
	
	public Timer(Long millis) {
		this();
		this.schedule(millis);
	}
	
	public Timer(Long millis, TimeoutListener timeOutListener) {
		this(millis);
		this.addTimeOutListener(timeOutListener);
	}
	
	public void addTimeOutListener(TimeoutListener timeOutListener) {
		if (timeOutListener == null) {
			throw new NullPointerException();
		}
		
		this.timeoutListener = timeOutListener;
	}
	
	public void schedule(Long millis) {
		if (this.timer != null) {
			this.cancel();
			this.timer = null;
		}
		
		this.timeout = false;
		
		this.timer = new java.util.Timer();
		this.timer.schedule(new TimerTask() {
			@Override
			public void run() {
				timeout();
			}
		}, millis);
	}
	
	private void timeout() {
		this.cancel();
		
		this.timeout = true;
		
		if (this.timeoutListener != null) {
			this.timeoutListener.timeout();
		}
	}

	public void cancel() {
		this.timer.cancel();
	}

	public boolean isTimeout() {
		return timeout;
	}
	
	public static void main(String[] args) {
		Timer timer = new Timer();
		timer.schedule(5000L);
		
		while (!timer.isTimeout()) {
			try { Thread.sleep(1); } catch (Exception e) {}
		}
		
		System.out.println("-- time-out --");
	}
}
