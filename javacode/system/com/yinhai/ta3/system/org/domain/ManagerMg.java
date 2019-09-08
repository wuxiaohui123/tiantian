package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;

public class ManagerMg implements Serializable {
	public static final String MANAGER_TYPE_CHIEF = "1";
	public static final String MANAGER_TYPE_DEPUTY = "2";
	private ManagerMgId id;

	public ManagerMg() {
	}

	public ManagerMg(ManagerMgId id) {
		this.id = id;
	}

	public ManagerMgId getId() {
		return id;
	}

	public void setId(ManagerMgId id) {
		this.id = id;
	}
}
