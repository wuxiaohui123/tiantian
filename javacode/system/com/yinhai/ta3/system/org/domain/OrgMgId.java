package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;

public class OrgMgId implements Serializable {

	private long positionid;
	private long orgid;

	public OrgMgId() {
	}

	public OrgMgId(long positionid, long orgid) {
		this.positionid = positionid;
		this.orgid = orgid;
	}

	public long getPositionid() {
		return positionid;
	}

	public void setPositionid(long positionid) {
		this.positionid = positionid;
	}

	public long getOrgid() {
		return orgid;
	}

	public void setOrgid(long orgid) {
		this.orgid = orgid;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof OrgMgId))
			return false;
		OrgMgId castOther = (OrgMgId) other;

		return (getPositionid() == castOther.getPositionid()) && (getOrgid() == castOther.getOrgid());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (int) getPositionid();
		result = 37 * result + (int) getOrgid();
		return result;
	}
}
