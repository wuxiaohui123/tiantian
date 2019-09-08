package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;

public class Yab139MgId implements Serializable {

	private String yab003;
	private String yab139;

	public Yab139MgId() {
	}

	public Yab139MgId(String yab003, String yab139) {
		this.yab003 = yab003;
		this.yab139 = yab139;
	}

	public String getYab003() {
		return yab003;
	}

	public void setYab003(String yab003) {
		this.yab003 = yab003;
	}

	public String getYab139() {
		return yab139;
	}

	public void setYab139(String yab139) {
		this.yab139 = yab139;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof Yab139MgId))
			return false;
		Yab139MgId castOther = (Yab139MgId) other;

		return ((getYab003() == castOther.getYab003()) || ((getYab003() != null) && (castOther.getYab003() != null) && (getYab003()
				.equals(castOther.getYab003()))))
				&& ((getYab139() == castOther.getYab139()) || ((getYab139() != null) && (castOther.getYab139() != null) && (getYab139()
						.equals(castOther.getYab139()))));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getYab003() == null ? 0 : getYab003().hashCode());

		result = 37 * result + (getYab139() == null ? 0 : getYab139().hashCode());

		return result;
	}
}
