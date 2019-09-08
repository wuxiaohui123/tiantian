package com.yinhai.sysframework.security.ta3;

public class NoPermissionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1337354300361870742L;

	public NoPermissionException(String msg) {
		super(msg);
	}

	public NoPermissionException(String msg, Throwable t) {
		super(msg, t);
	}
}
