/*
 * Copyright (C) 2012 by Gonzalo Castillo
 * This software is provided under the GNU general public license (http://www.gnu.org/copyleft/gpl.html).
 */
package com.goncafa.proxy.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.goncafa.proxy.util.ProxyProperties;

public class Config {
	public static final String PROXY_CONFIGURATION_FILE = new String("./config/proxy.properties");
	public static final String LOGGER_CONFIGURATION_FILE = new String("./config/logger.properties");
	
	public static final Integer CRITIAL_LOGGER_LEVEL = 1;
	public static final Integer ERROR_LOGGER_LEVEL = 2;
	public static final Integer WARNING_LOGGER_LEVEL = 3;
	public static final Integer INFO_LOGGER_LEVEL = 4;
	public static final Integer DEBUG_LOGGER_LEVEL = 5;
	
	public static File LOG_FILE;
	public static Integer LOGGER_LEVEL;
	public static boolean LOG_TO_CONSOLE;
	
	public static Integer PROXY_PORT;
	public static Integer PROXY_READ_BUFFER_SIZE;
	public static Integer MAX_THREADS_ON_POOL;
	public static Long FIRST_REQUEST_TIMEOUT;
	public static Long HTTP_BYTES_WAIT_TIMEOUT;
	public static Long SSL_443_READ_TIMEOUT;
	public static Long SSL_NNN_READ_TIMEOUT;
	public static Long APPLICATION_OCTETSTREAM_TIMEOUT;
	public static Long DEFAULT_READ_TIMEOUT;
	public static Boolean USE_PROXY_SOCKS;
	public static String SOCKS_PROXY_HOST;
	public static Integer SOCKS_PROXY_PORT;
	
	static {
		try {
			ProxyProperties loggerProperties = new ProxyProperties();
			InputStream is = new FileInputStream(new File(new String(LOGGER_CONFIGURATION_FILE)));
			loggerProperties.load(is);
			
			LOG_FILE = new File(loggerProperties.getProperty(new String("LOG_FILE"), new String("./logs/goncafa-proxy.log")));
			
			String loggerLevel = loggerProperties.getProperty(new String("LOGGER_LEVEL"), new String("CRITICAL"));
			if ("CRITICAL".equalsIgnoreCase(loggerLevel)) {
				LOGGER_LEVEL = 1;
			} else if ("ERROR".equalsIgnoreCase(loggerLevel)) {
				LOGGER_LEVEL = 2;
			} else if ("WARNING".equalsIgnoreCase(loggerLevel)) {
				LOGGER_LEVEL = 3;
			} else if ("INFO".equalsIgnoreCase(loggerLevel)) {
				LOGGER_LEVEL = 4;
			} else if ("DEBUG".equalsIgnoreCase(loggerLevel)) {
				LOGGER_LEVEL = 5;
			} else {
				LOGGER_LEVEL = 1;
			}
			
			LOG_TO_CONSOLE = Boolean.parseBoolean(loggerProperties.getProperty(new String("LOG_TO_CONSOLE"), new String("False")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			ProxyProperties proxyProperties = new ProxyProperties();
			InputStream is = new FileInputStream(new File(new String(PROXY_CONFIGURATION_FILE)));
			proxyProperties.load(is);
			
			PROXY_PORT = Integer.valueOf(proxyProperties.getProperty(new String("PROXY_PORT"), new String("3128")));
			PROXY_READ_BUFFER_SIZE = Integer.valueOf(proxyProperties.getProperty(new String("PROXY_READ_BUFFER_SIZE"), new String("1024")));
			MAX_THREADS_ON_POOL = Integer.valueOf(proxyProperties.getProperty(new String("MAX_THREADS_ON_POOL"), new String("3000")));
			FIRST_REQUEST_TIMEOUT = Long.valueOf(proxyProperties.getProperty(new String("FIRST_REQUEST_TIMEOUT"), new String("5000")));
			HTTP_BYTES_WAIT_TIMEOUT = Long.valueOf(proxyProperties.getProperty(new String("HTTP_BYTES_WAIT_TIMEOUT"), new String("2000")));
			SSL_443_READ_TIMEOUT = Long.valueOf(proxyProperties.getProperty(new String("SSL_443_READ_TIMEOUT"), new String("3000")));
			SSL_NNN_READ_TIMEOUT = Long.valueOf(proxyProperties.getProperty(new String("SSL_NNN_READ_TIMEOUT"), new String("600000")));
			APPLICATION_OCTETSTREAM_TIMEOUT = Long.valueOf(proxyProperties.getProperty(new String("APPLICATION_OCTETSTREAM_TIMEOUT"), new String("300000")));
			DEFAULT_READ_TIMEOUT = Long.valueOf(proxyProperties.getProperty(new String("DEFAULT_READ_TIMEOUT"), new String("600000")));
			USE_PROXY_SOCKS = Boolean.valueOf(proxyProperties.getProperty(new String("USE_PROXY_SOCKS"), new String("False")));
			SOCKS_PROXY_HOST = proxyProperties.getProperty(new String("SOCKS_PROXY_HOST"), new String(""));
			SOCKS_PROXY_PORT = Integer.valueOf(proxyProperties.getProperty(new String("SOCKS_PROXY_PORT"), new String("0")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
