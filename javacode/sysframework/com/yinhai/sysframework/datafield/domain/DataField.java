package com.yinhai.sysframework.datafield.domain;

import java.io.Serializable;

public class DataField implements Serializable {
	private DataFieldId id;
	private String syspath;

	public String getSyspath() {
		return syspath;
	}

	public void setSyspath(String syspath) {
		this.syspath = syspath;
	}

	public DataField() {
	}

	public DataFieldId getId() {
		return id;
	}

	public void setId(DataFieldId id) {
		this.id = id;
	}

	public DataField(DataFieldId id, String syspath) {
		this.id = id;
		this.syspath = syspath;
	}
}
