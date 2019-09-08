package com.yinhai.sysframework.dto;


public class PrcDTO extends BaseDTO {
	public static final String DEFAULT_APPCODE_NAME = "AppCode";
	public static final String DEFAULT_ERRORMSG_NAME = "ErrorMsg";
	public static final String DEFAULT_APPCODE_SUCCESS = "NOERROR";
	public static final String HEAD_APPCODE_SUCESS = "success10000";
	public static final String HEAD_APPCODE_FAIL = "fail10000";
	public static final String DETAILERROR = " ERROR:";

	public PrcDTO() {
		put(DEFAULT_APPCODE_NAME, null);
		put(DEFAULT_ERRORMSG_NAME, null);
	}

	public String getAppCode() {
		return getAsString(DEFAULT_APPCODE_NAME);
	}

	public String getErrorMsg() {
		return getAsString(DEFAULT_ERRORMSG_NAME);
	}

	public String getShortMsg() {
		String ret = getAsString(DEFAULT_ERRORMSG_NAME);
		if (ret != null)
			ret = ret.split(DETAILERROR)[0];
		return ret;
	}

}
