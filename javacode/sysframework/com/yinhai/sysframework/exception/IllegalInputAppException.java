package com.yinhai.sysframework.exception;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IllegalInputAppException extends AppException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3937733648086703445L;
	private List<AppException> exceptions;
	private String inputField;

	public String getInputField() {
		return inputField;
	}

	public void addException(AppException e) {
		if ((inputField == null) && (e.getFieldName() != null)) {
			inputField = e.getFieldName();
		}
		if (exceptions == null) {
			exceptions = new ArrayList<AppException>();
		}
		exceptions.add(e);
	}

	public List<AppException> getExceptions() {
		if (exceptions == null) {
			exceptions = new ArrayList<AppException>();
		}
		return exceptions;
	}

	public String getMessage() {
		StringBuffer sb = new StringBuffer();
		if (exceptions != null) {
			Iterator<AppException> it = exceptions.iterator();
			while (it.hasNext()) {
				sb.append(it.next().getMessage() + "\\n");
			}
		}
		return sb.toString();
	}
}
