package com.yinhai.ta3.sysapp.consolemg.domain;

import java.io.Serializable;

public class ConsoleModuleLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8582935648333425070L;
	private String mark;
	private Long positionid;
	private String location;

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public Long getPositionid() {
		return positionid;
	}

	public void setPositionid(Long positionid) {
		this.positionid = positionid;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
	
	
}
