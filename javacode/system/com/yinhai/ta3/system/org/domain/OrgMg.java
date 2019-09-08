package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;

public class OrgMg implements Serializable {

	private OrgMgId id;

	public OrgMg() {
	}

	public OrgMg(OrgMgId id) {
		this.id = id;
	}

	public OrgMgId getId() {
		return id;
	}

	public void setId(OrgMgId id) {
		this.id = id;
	}
}
