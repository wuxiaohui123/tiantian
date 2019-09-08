package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;
import java.util.Date;

public class UserPosition implements Serializable {

	public static final String ISMAINPOSITION = "0";
	private UserPositionId id;
	private String mainposition;
	private Long createuser;
	private Date createtime;

	public UserPosition() {
	}

	public UserPosition(UserPositionId id) {
		this.id = id;
	}

	public UserPosition(UserPositionId id, String mainposition, Long createuser, Date createtime) {
		this.id = id;
		this.mainposition = mainposition;
		this.createuser = createuser;
		this.createtime = createtime;
	}

	public UserPositionId getId() {
		return id;
	}

	public void setId(UserPositionId id) {
		this.id = id;
	}

	public String getMainposition() {
		return mainposition;
	}

	public void setMainposition(String mainposition) {
		this.mainposition = mainposition;
	}

	public Long getCreateuser() {
		return createuser;
	}

	public void setCreateuser(Long createuser) {
		this.createuser = createuser;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
}
