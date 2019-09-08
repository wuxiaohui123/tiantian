package com.yinhai.ta3.system.security.ta3;

import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.security.ta3.IRoleAuthrity;

public class DefaultRoleAuthrity implements IRoleAuthrity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 785256125913933232L;
	private IPosition position;

	public DefaultRoleAuthrity(IPosition position) {
		this.position = position;
	}

	public String getRole() {
		return String.valueOf(position.getPositionid());
	}
}
