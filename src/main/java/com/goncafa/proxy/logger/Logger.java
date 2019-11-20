/*
 * Copyright (C) 2012 by Gonzalo Castillo
 * This software is provided under the GNU general public license (http://www.gnu.org/copyleft/gpl.html).
 */
package com.goncafa.proxy.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.goncafa.proxy.config.Config;


public class Logger {
	
	private static Map<Class<?>, Logger> instances = new HashMap<Class<?>, Logger>();
	private boolean isLogFileThere;
	private DateFormat dateFormat;
	private Class<?> clazz;
	
	public static Logger getInstance(Class<?> clazz) {
		Logger instance = instances.get(clazz);
		if (instance == null) {
			instance = new Logger(clazz);
			instances.put(clazz, instance);
		}
		
		return instance;
	}
	
	private Logger() { super(); }
	
	private Logger(Class<?> _class) { 
		this(); 
		
		this.clazz = _class;
		this.isLogFileThere = false;
		
		if (!Config.LOG_FILE.exists()) {
			try {
				if (Config.LOG_FILE.createNewFile()) {
					isLogFileThere = true;
				}
			} catch (IOException e) {
				isLogFileThere = false;
				e.printStackTrace();
			}
		} else {
			isLogFileThere = true;
		}
		
		dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
	}
	
	public void critical(String message) {
		if (Config.LOGGER_LEVEL < Config.CRITIAL_LOGGER_LEVEL) {
			return;
		}
		
		this.write(message, LoggerLevelType.CRITICAL);
	}
	
	public void critical(String message, Throwable t) {
		if (Config.LOGGER_LEVEL < Config.CRITIAL_LOGGER_LEVEL) {
			return;
		}
		
		StringWriter errorWriter = new StringWriter();
		t.printStackTrace(new PrintWriter(errorWriter));
		
		this.error(message);
		this.write(errorWriter.toString(), LoggerLevelType.CRITICAL);
	}
	
	public void error(String message) {
		if (Config.LOGGER_LEVEL < Config.ERROR_LOGGER_LEVEL) {
			return;
		}
		
		this.write(message, LoggerLevelType.ERROR);
	}
	
	public void error(String message, Throwable t) {
		if (Config.LOGGER_LEVEL < Config.ERROR_LOGGER_LEVEL) {
			return;
		}
		
		StringWriter errorWriter = new StringWriter();
		t.printStackTrace(new PrintWriter(errorWriter));
		
		this.error(message);
		this.write(errorWriter.toString(), LoggerLevelType.ERROR);
	}
	
	public void warning(String message) {
		if (Config.LOGGER_LEVEL < Config.WARNING_LOGGER_LEVEL) {
			return;
		}
		
		this.write(message, LoggerLevelType.WARNING);
	}
	
	public void debug(String message) {
		if (Config.LOGGER_LEVEL < Config.DEBUG_LOGGER_LEVEL) {
			return;
		}
		
		this.write(message, LoggerLevelType.DEBUG);
	}
	
	public void debug(Object object) {
		this.debug(object.toString());
	}
	
	public void info(String message) {
		if (Config.LOGGER_LEVEL < Config.INFO_LOGGER_LEVEL) {
			return;
		}
		
		this.write(message, LoggerLevelType.INFO);
	}
	
	public Class<?> getClazz() {
		return this.clazz;
	}
	
	private void write(String message, LoggerLevelType logType) {
		if (message == null) {
			return;
		}

		StringBuilder toWrite = new StringBuilder()
		.append(new String(this.dateFormat.format(new Date())))
		.append(new String(" ")).append(logType)
		.append(new String(" - "))
		.append(this.getClazz().getSimpleName())
		.append(new String(".java - [ ")).append(message)
		.append(new String(" ]"));

		if (isLogFileThere && Config.LOG_FILE.canWrite()) {
			synchronized (this) {
				try {
					FileWriter fileWriter = new FileWriter(Config.LOG_FILE, true);
					PrintWriter printWriter = new PrintWriter(fileWriter);
					printWriter.println(toWrite.toString());
					printWriter.close();
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		if (Config.LOG_TO_CONSOLE) {
			System.out.println(toWrite.toString());
		}
	}
}
