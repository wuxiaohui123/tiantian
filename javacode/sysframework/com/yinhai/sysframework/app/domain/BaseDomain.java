package com.yinhai.sysframework.app.domain;

import java.util.Map;

public class BaseDomain extends AbstractDomainObject {

	public boolean equals(Object o) {
		if (null == o)
			return false;
		if (this == o) {
			return true;
		}
		if (o.getClass().toString().equals(getClass().toString())) {
			BaseDomain od = (BaseDomain) o;
			if (getPK().equals(od.getPK())) {
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		return getKey().getSortedId().hashCode();
	}

	public Map toMap() {
		return null;
	}

	public String toJson() {
		return super.toJson();
	}

	public Key getPK() {
		return null;
	}	
}
