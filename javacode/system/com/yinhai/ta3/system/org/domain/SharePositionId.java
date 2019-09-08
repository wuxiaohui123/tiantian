package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;

public class SharePositionId implements Serializable {

	private long spositionid;
	private long dpositionid;

	public SharePositionId() {
	}

	public SharePositionId(long spositionid, long dpositionid) {
		this.spositionid = spositionid;
		this.dpositionid = dpositionid;
	}

	public long getSpositionid() {
		return spositionid;
	}

	public void setSpositionid(long spositionid) {
		this.spositionid = spositionid;
	}

	public long getDpositionid() {
		return dpositionid;
	}

	public void setDpositionid(long dpositionid) {
		this.dpositionid = dpositionid;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof SharePositionId))
			return false;
		SharePositionId castOther = (SharePositionId) other;

		return (getSpositionid() == castOther.getSpositionid()) && (getDpositionid() == castOther.getDpositionid());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (int) getSpositionid();
		result = 37 * result + (int) getDpositionid();
		return result;
	}
}
