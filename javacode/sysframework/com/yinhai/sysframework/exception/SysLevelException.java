package com.yinhai.sysframework.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

public class SysLevelException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2397445635609413876L;

	public SysLevelException(String msg, Throwable th) {
		super(msg, th);
	}

	public SysLevelException(String msg) {
		super(msg);
	}

	public SysLevelException(Throwable th) {
		super(th);
	}

	public void printStackTrace(PrintStream s) {
		if (super.getCause() != null) {
			s.print(getClass().getName() + " Caused by: ");
			super.getCause().printStackTrace(s);
		} else {
			super.printStackTrace(s);
		}
	}

	public void printStackTrace(PrintWriter s) {
		if (super.getCause() != null) {
			s.print(getClass().getName() + " Caused by: ");
			super.getCause().printStackTrace(s);
		} else {
			super.printStackTrace(s);
		}
	}
}
