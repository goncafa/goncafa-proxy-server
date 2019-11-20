/*
 * Copyright (C) 2012 by Gonzalo Castillo
 * This software is provided under the GNU general public license (http://www.gnu.org/copyleft/gpl.html).
 */
package com.goncafa.proxy.request;

import com.goncafa.proxy.http.request.HttpRequestMethodType;

public interface Headers {
	public String getHost();
	public HttpRequestMethodType getRequestMethodEnum();
	public Integer getPort();
}
