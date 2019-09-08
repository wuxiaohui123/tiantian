package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;

public class Yab003LevelMgId implements Serializable {

	private String pyab003;
	private String yab003;

	public Yab003LevelMgId() {
	}

	public Yab003LevelMgId(String pyab003, String yab003) {
		this.pyab003 = pyab003;
		this.yab003 = yab003;
	}

	public String getPyab003() {
		return pyab003;
	}

	public void setPyab003(String pyab003) {
		this.pyab003 = pyab003;
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
		if (!(other instanceof Yab003LevelMgId))
			return false;
		Yab003LevelMgId castOther = (Yab003LevelMgId) other;

		return ((getPyab003() == castOther.getPyab003()) || ((getPyab003() != null) && (castOther.getPyab003() != null) && (getPyab003()
				.equals(castOther.getPyab003()))))
				&& ((getYab003() == castOther.getYab003()) || ((getYab003() != null) && (castOther.getYab003() != null) && (getYab003()
						.equals(castOther.getYab003()))));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getPyab003() == null ? 0 : getPyab003().hashCode());

		result = 37 * result + (getYab003() == null ? 0 : getYab003().hashCode());

		return result;
	}
}
