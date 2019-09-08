package com.yinhai.sysframework.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

public class TypeCastException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3370659324862496824L;
	Throwable nested = null;

	public TypeCastException() {
	}

	public TypeCastException(String msg) {
		super(msg);
	}

	public TypeCastException(String msg, Throwable nested) {
		super(msg);
		this.nested = nested;
	}

	public TypeCastException(Throwable nested) {
		this.nested = nested;
	}

	public String getMessage() {
		if (nested != null) {
			return super.getMessage() + " (" + nested.getMessage() + ")";
		}
		return super.getMessage();
	}

	public String getNonNestedMessage() {
		return super.getMessage();
	}

	public Throwable getNested() {
		if (nested == null)
			return this;
		return nested;
	}

	public void printStackTrace() {
		super.printStackTrace();
		if (nested != null)
			nested.printStackTrace();
	}

	public void printStackTrace(PrintStream ps) {
		super.printStackTrace(ps);
		if (nested != null)
			nested.printStackTrace(ps);
	}

	public void printStackTrace(PrintWriter pw) {
		super.printStackTrace(pw);
		if (nested != null)
			nested.printStackTrace(pw);
	}
}
