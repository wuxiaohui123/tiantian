package com.yinhai.sysframework.datafield.domain;

import java.io.Serializable;



public class DataFieldId implements Serializable{

	private Long userid;
	private Long menuid;
	private String yab139;

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

	public String getYab139() {
		return yab139;
	}

	public void setYab139(String yab139) {
		this.yab139 = yab139;
	}

	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = 31 * result + (menuid == null ? 0 : menuid.hashCode());
		result = 31 * result + (userid == null ? 0 : userid.hashCode());
		result = 31 * result + (yab139 == null ? 0 : yab139.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataFieldId other = (DataFieldId) obj;
		if (menuid == null) {
			if (menuid != null)
				return false;
		} else if (!menuid.equals(menuid))
			return false;
		if (userid == null) {
			if (userid != null)
				return false;
		} else if (!userid.equals(userid))
			return false;
		if (yab139 == null) {
			if (yab139 != null)
				return false;
		} else if (!yab139.equals(yab139))
			return false;
		return true;
	}

	public DataFieldId(Long userid, Long menuid, String yab139) {
		this.userid = userid;
		this.menuid = menuid;
		this.yab139 = yab139;
	}

	public DataFieldId() {
	}
}
