/*
 * Copyright (C) 2012 by Gonzalo Castillo
 * This software is provided under the GNU general public license (http://www.gnu.org/copyleft/gpl.html).
 */
package com.goncafa.proxy.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;

import com.goncafa.proxy.config.Config;
import com.goncafa.proxy.http.request.response.ContentType;
import com.goncafa.proxy.logger.Logger;
import com.goncafa.proxy.util.RegexUtil;
import com.goncafa.proxy.util.Timer;

public class InputStreamConsumer implements Runnable {
	private static Logger _log = Logger.getInstance(InputStreamConsumer.class);
	
	private InputStream input;
	private OutputStream output;
	private Semaphore semaphore;
	private Long readTimeOut;
	private Timer timerRead;
	
	public InputStreamConsumer(InputStream input, OutputStream output) {
		super();
		this.input = input;
		this.output = output;
		this.timerRead = new Timer();
	}
			
	public InputStreamConsumer(InputStream input, OutputStream output, Long readTimeOut) {
		this(input, output);
		this.readTimeOut = readTimeOut;
	}
	
	public InputStreamConsumer(InputStream input, OutputStream output, Long readTimeOut, Semaphore semaphore) {
		this(input, output, readTimeOut);
		this.semaphore = semaphore;
	}
	
	public void consume() {
		try {
			if (this.readTimeOut == null) {
				this.readTimeOut = Config.DEFAULT_READ_TIMEOUT;
			}
			
			boolean availableBytes = false;
			int bytesReaded = 0;
			byte[] bytesBuffer = new byte[Config.PROXY_READ_BUFFER_SIZE];
			String contentType = null;

			do {
				try {
					bytesReaded = this.input.read(bytesBuffer);
					
					// if bytesReaded is -1 then EOF
					if (bytesReaded == -1) {
						_log.debug(new String("EOF has been readed."));
						break;
					}
				} catch(IOException e) {}

				try {
					this.output.write(bytesBuffer, 0, bytesReaded);
					this.output.flush();
				} catch (Exception e) {}
				
				if (contentType == null) {
					try {
						String response = new String(bytesBuffer, 0, bytesReaded);
						Matcher contentTypeMatcher = RegexUtil.HTTP_RESPONSE_CONTENT_TYPE_PATTERN.matcher(response.toLowerCase());
						
						if (contentTypeMatcher.find()) {
							contentType = contentTypeMatcher.group(1);
							
							if (contentType != null) {
								contentType = contentType.trim();
								_log.debug(new String("Content-Type: ") + contentType);
								
								if (contentType.toLowerCase().contains(ContentType.APPLICATION_OCTETSTREAM.toString()) ||
										contentType.toLowerCase().contains(ContentType.APPLICATION_ZIP.toString())) {
									_log.debug(new String("stting read timeout to: ") + Config.APPLICATION_OCTETSTREAM_TIMEOUT);
									this.readTimeOut = Config.APPLICATION_OCTETSTREAM_TIMEOUT;
								}
							}
						}
					} catch (Exception e) {}
				}
				
				this.timerRead.schedule(this.readTimeOut);

				try {
					while (!(this.input.available() > 0) && !this.timerRead.isTimeout()) {
						try {Thread.sleep(1);} catch (Exception e) {}
					}
					
					availableBytes = this.input.available() > 0;
				} catch(IOException e) {}
			} while (availableBytes);
		} finally {
			if (this.semaphore != null) {
				this.semaphore.release(2);
			}
		}
	}
	
	public void run() {
		this.consume();
	}
}
