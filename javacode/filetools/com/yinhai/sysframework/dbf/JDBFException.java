package com.yinhai.sysframework.dbf;

import java.io.PrintWriter;

public class JDBFException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4775681085911230190L;
	private Throwable detail;

	public JDBFException(String s) {
		this(s, null);
	}

	public JDBFException(Throwable throwable) {
		this(throwable.getMessage(), throwable);
	}

	public JDBFException(String s, Throwable throwable) {
		super(s);
		detail = throwable;
	}

	public String getMessage() {
		if (detail == null) {
			return super.getMessage();
		}
		return super.getMessage();
	}

	public void printStackTrace(java.io.PrintStream printstream) {
		if (detail == null) {
			super.printStackTrace(printstream);
			return;
		}
		java.io.PrintStream printstream1 = printstream;
		printstream1.println(this);
		detail.printStackTrace(printstream);
	}

	public void printStackTrace() {
		printStackTrace(System.err);
	}

	public void printStackTrace(PrintWriter printwriter) {
		if (detail == null) {
			super.printStackTrace(printwriter);
			return;
		}
		PrintWriter printwriter1 = printwriter;

		printwriter1.println(this);
		detail.printStackTrace(printwriter);
	}
}
