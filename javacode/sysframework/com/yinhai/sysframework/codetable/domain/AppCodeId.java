package com.yinhai.sysframework.codetable.domain;

import java.io.Serializable;

public class AppCodeId implements Serializable {

	private String codeType;
	private String codeValue;

	public AppCodeId() {
	}

	public AppCodeId(String aaa100, String aaa102) {
		codeType = aaa100;
		codeValue = aaa102;
	}

	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String aaa100) {
		codeType = aaa100;
	}

	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(String aaa102) {
		codeValue = aaa102;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof AppCodeId))
			return false;
		AppCodeId castOther = (AppCodeId) other;

		return ((getCodeType() == castOther.getCodeType()) || ((getCodeType() != null)
				&& (castOther.getCodeType() != null) && (getCodeType().equals(castOther.getCodeType()))))
				&& ((getCodeValue() == castOther.getCodeValue()) || ((getCodeValue() != null)
						&& (castOther.getCodeValue() != null) && (getCodeValue().equals(castOther.getCodeValue()))));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getCodeType() == null ? 0 : getCodeType().hashCode());

		result = 37 * result + (getCodeValue() == null ? 0 : getCodeValue().hashCode());

		return result;
	}
}
