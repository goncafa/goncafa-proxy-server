/*
 * Copyright (C) 2012 by Gonzalo Castillo
 * This software is provided under the GNU general public license (http://www.gnu.org/copyleft/gpl.html).
 */
package com.goncafa.proxy.http.request;

import java.util.regex.Matcher;

import com.goncafa.proxy.logger.Logger;
import com.goncafa.proxy.request.Headers;
import com.goncafa.proxy.util.RegexUtil;

public class HttpRequestHeaders implements Headers {
	private static Logger _log = Logger.getInstance(HttpRequestHeaders.class);
	private String request;
	private HttpRequestMethodType requestMethodEnum;
	private String host;
	private Integer port;
	
	public HttpRequestHeaders(String request) {
		super();
		this.request = request;
		this.parse();
	}
	
	private void parse() {
		//_log.debug(request);
		String lowerCaseRequest = this.request.toLowerCase();
		// find host
		Matcher hostMatcher = RegexUtil.HTTP_REQUEST_HOST_PATTERN.matcher(lowerCaseRequest);
		
		if (hostMatcher.find()) {
			String host = hostMatcher.group(1);
			
			if (host != null) {
				this.host = host.trim();
			} else {
				_log.warning(new String("host not found"));
			}
		} else {
			_log.warning(new String("host not found"));
		}
		
		// find request method
		if (lowerCaseRequest.startsWith(HttpRequestMethodType.GET.toString().toLowerCase())) {
			this.requestMethodEnum = HttpRequestMethodType.GET;
		} else if (lowerCaseRequest.startsWith(HttpRequestMethodType.POST.toString().toLowerCase())) {
			this.requestMethodEnum = HttpRequestMethodType.POST;
		} else if (lowerCaseRequest.startsWith(HttpRequestMethodType.CONNECT.toString().toLowerCase())) {
			this.requestMethodEnum = HttpRequestMethodType.CONNECT;
		}
		
		// find port
		Matcher portMatcher = RegexUtil.HTTP_REQUEST_PORT_PATTERN.matcher(lowerCaseRequest);
		
		if (portMatcher.find()) {
			String port = portMatcher.group(1);
			if (port != null) {
				try {
				this.port = Integer.valueOf(port.trim());
				} catch (Exception e) {
					this.port = 80;
				}
			} else {
				this.port = 80;
			}
		} else {
			this.port = 80;
		}
	}
	
	@Override
	public String toString() {
		return this.depureRequest();
	}
	
	public String getHost() {
		return this.host;
	}
	
	public HttpRequestMethodType getRequestMethodEnum() {
		return this.requestMethodEnum;
	}
	
	public Integer getPort() {
		return this.port;
	}
	
	public String depureRequest() {
		if (this.requestMethodEnum == HttpRequestMethodType.GET 
				|| this.requestMethodEnum == HttpRequestMethodType.POST) {
			String toFind = new String("http://") + this.getHost();
			int index = this.request.indexOf(toFind + new String(":") + this.getPort());
			
			if (index != -1) {
				return this.request.replaceFirst(toFind + new String(":") + this.getPort(), new String(""));
			}
			
			return this.request.replaceFirst(toFind, new String(""));
		} else {
			return this.request;
		}
	}
	
}
