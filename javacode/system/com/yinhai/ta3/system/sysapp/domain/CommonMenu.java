package com.yinhai.ta3.system.sysapp.domain;

import java.io.Serializable;

public class CommonMenu implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1782530462407583152L;
	private CommonMenuId id;

	public CommonMenu() {
	}

	public CommonMenu(CommonMenuId id) {
		this.id = id;
	}

	public CommonMenuId getId() {
		return id;
	}

	public void setId(CommonMenuId id) {
		this.id = id;
	}
}
