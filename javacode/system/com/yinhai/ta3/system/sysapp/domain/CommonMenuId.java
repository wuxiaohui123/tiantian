package com.yinhai.ta3.system.sysapp.domain;

import java.io.Serializable;

public class CommonMenuId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2090710112243001215L;
	private Long userid;
	private Long menuid;

	public CommonMenuId() {
	}

	public CommonMenuId(Long userid, Long menuid) {
		this.userid = userid;
		this.menuid = menuid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Long getMenuid() {
		return menuid;
	}

	public void setMenuid(Long menuid) {
		this.menuid = menuid;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof CommonMenuId))
			return false;
		CommonMenuId castOther = (CommonMenuId) other;

		return ((getUserid() == castOther.getUserid()) || ((getUserid() != null) && (castOther.getUserid() != null) && (getUserid()
				.equals(castOther.getUserid()))))
				&& ((getMenuid() == castOther.getMenuid()) || ((getMenuid() != null) && (castOther.getMenuid() != null) && (getMenuid()
						.equals(castOther.getMenuid()))));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getUserid() == null ? 0 : getUserid().hashCode());

		result = 37 * result + (getMenuid() == null ? 0 : getMenuid().hashCode());

		return result;
	}
}
