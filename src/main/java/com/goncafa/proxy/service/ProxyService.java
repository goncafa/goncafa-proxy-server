/*
 * Copyright (C) 2012 by Gonzalo Castillo
 * This software is provided under the GNU general public license (http://www.gnu.org/copyleft/gpl.html).
 */
package com.goncafa.proxy.service;

import com.goncafa.proxy.request.Headers;

public interface ProxyService {
	public void proxy(Headers request);
}
