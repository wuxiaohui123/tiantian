package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;

public class AdminYab003ScopeId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1095412622742415017L;
	private Long positionid;
	private String yab139;

	public AdminYab003ScopeId() {
	}

	public AdminYab003ScopeId(Long positionid, String yab139) {
		this.positionid = positionid;
		this.yab139 = yab139;
	}

	public Long getPositionid() {
		return positionid;
	}

	public void setPositionid(Long positionid) {
		this.positionid = positionid;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof AdminYab003ScopeId))
			return false;
		AdminYab003ScopeId castOther = (AdminYab003ScopeId) other;

		return ((getPositionid() == castOther.getPositionid()) || ((getPositionid() != null)
				&& (castOther.getPositionid() != null) && (getPositionid().equals(castOther.getPositionid()))))
				&& ((getYab139() == castOther.getYab139()) || ((getYab139() != null) && (castOther.getYab139() != null) && (getYab139()
						.equals(castOther.getYab139()))));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getPositionid() == null ? 0 : getPositionid().hashCode());

		result = 37 * result + (getYab139() == null ? 0 : getYab139().hashCode());

		return result;
	}

	public String getYab139() {
		return yab139;
	}

	public void setYab139(String yab139) {
		this.yab139 = yab139;
	}
}
