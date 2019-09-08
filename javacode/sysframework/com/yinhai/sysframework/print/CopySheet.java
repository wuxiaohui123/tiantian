package com.yinhai.sysframework.print;

import java.io.Serializable;

public class CopySheet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8880416558068344740L;
	public int sourceSheetNumber;
	public int targetSheetNumber;
	public String targetSheetName;
}
