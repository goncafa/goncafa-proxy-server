package com.goncafa.proxy.util;

import java.util.regex.Pattern;

public class RegexUtil {
	public static final Pattern HTTP_REQUEST_HOST_PATTERN;
	public static final Pattern HTTP_REQUEST_PORT_PATTERN;
	public static final Pattern HTTP_RESPONSE_CONTENT_TYPE_PATTERN;
	
	static {
		HTTP_REQUEST_HOST_PATTERN = Pattern.compile(new String("\\bhost:([^:\\r]+)"));
		HTTP_REQUEST_PORT_PATTERN = Pattern.compile(new String(":(\\d+)\\/?\\shttp"));
		HTTP_RESPONSE_CONTENT_TYPE_PATTERN = Pattern.compile(new String("\\bcontent-type:([^;\\r]+)"));
	}
}
