/*
 * Copyright (C) 2012 by Gonzalo Castillo
 * This software is provided under the GNU general public license (http://www.gnu.org/copyleft/gpl.html).
 */
package com.goncafa.proxy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

public class ProxyProperties extends Properties {
	
	private static final long serialVersionUID = 1L;
	
	public ProxyProperties() { super(); }
	
	public ProxyProperties(String fileName) throws IOException {
		super();
		
		this.load(new FileInputStream(fileName));
	}
	
	public ProxyProperties(InputStream inStream) throws IOException {
		super();
		
		this.load(inStream);
	}
	
	public ProxyProperties(File file) throws IOException {
		super();
		
		this.load(new FileInputStream(file));
	}

	@Override
	public synchronized void load(InputStream inStream) throws IOException {
		try {
			super.load(inStream);
		} finally {
			try {
				inStream.close();
			} catch (IOException e) {}
		}
	}
	
	@Override
	public synchronized void load(Reader reader) throws IOException {
		try {
			super.load(reader);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {}
		}
	}
	
	@Override
	public String getProperty(String key) {
		String property = super.getProperty(key);
		
		if (property == null) {
			System.err.println(new String("missing property for key: ") + key);
			
			return null;
		}
		
		return property.trim();
	}
	
	@Override
	public String getProperty(String key, String defaultValue) {
		String property = super.getProperty(key, defaultValue);
		
		if (property == null) {
			System.err.println(new String("missing default value for key: ") + key);
			
			return null;
		}
		
		return property.trim();
	}
}
