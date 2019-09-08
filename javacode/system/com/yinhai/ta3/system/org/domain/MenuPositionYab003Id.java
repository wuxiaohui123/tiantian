package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;

public class MenuPositionYab003Id implements Serializable {

	private Long menuid;
	private Long positionid;
	private String yab003;

	public MenuPositionYab003Id() {
	}

	public MenuPositionYab003Id(Long menuid, Long positionid, String yab003) {
		this.menuid = menuid;
		this.positionid = positionid;
		this.yab003 = yab003;
	}

	public Long getMenuid() {
		return menuid;
	}

	public void setMenuid(Long menuid) {
		this.menuid = menuid;
	}

	public Long getPositionid() {
		return positionid;
	}

	public void setPositionid(Long positionid) {
		this.positionid = positionid;
	}

	public String getYab003() {
		return yab003;
	}

	public void setYab003(String yab003) {
		this.yab003 = yab003;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof MenuPositionYab003Id))
			return false;
		MenuPositionYab003Id castOther = (MenuPositionYab003Id) other;

		return ((getMenuid() == castOther.getMenuid()) || ((getMenuid() != null) && (castOther.getMenuid() != null) && (getMenuid()
				.equals(castOther.getMenuid()))))
				&& ((getPositionid() == castOther.getPositionid()) || ((getPositionid() != null)
						&& (castOther.getPositionid() != null) && (getPositionid().equals(castOther.getPositionid()))))
				&& ((getYab003() == castOther.getYab003()) || ((getYab003() != null) && (castOther.getYab003() != null) && (getYab003()
						.equals(castOther.getYab003()))));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getMenuid() == null ? 0 : getMenuid().hashCode());

		result = 37 * result + (getPositionid() == null ? 0 : getPositionid().hashCode());

		result = 37 * result + (getYab003() == null ? 0 : getYab003().hashCode());

		return result;
	}
}
