package com.yinhai.sysframework.exception;

public class AppException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3515800509410031926L;

	private String errorMessage;

	private String fieldName;

	AppException() {
	}

	public AppException(String msg) {
		super(msg);
		errorMessage = msg;
	}

	public AppException(String msg, String fieldName) {
		super(msg);
		errorMessage = msg;
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getMessage() {
		return errorMessage;
	}
}
