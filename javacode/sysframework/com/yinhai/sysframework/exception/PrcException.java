package com.yinhai.sysframework.exception;

public class PrcException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7123506476740700073L;
	private String appCode;
	private String errorMsg;
	private String shortMsg;

	public PrcException(String prcName, String appCode, String errorMsg) {
		super("调用存储过程[" + prcName + "发生错误，错误编码为：[" + appCode + "]错误原因：[" + errorMsg + "]");
		this.appCode = appCode;
		this.errorMsg = errorMsg;
	}

	public PrcException(String prcName, String appCode, String errorMsg, String shortMsg) {
		super("调用存储过程[" + prcName + "发生错误，错误编码为：[" + appCode + "]错误原因：[" + errorMsg + "]");
		this.appCode = appCode;
		this.errorMsg = errorMsg;
		this.shortMsg = shortMsg;
	}

	public String getAppCode() {
		return appCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public String getShortMsg() {
		return shortMsg;
	}
}
