package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;
import java.util.Date;

public class InnerControlVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -689577755612193922L;
	private String name;
	private String usernamepath;
	private String orgnamepath;
	private String positionname;
	private Date optime;
	private Long userid;
	private String sex;
	private String loginid;

	public InnerControlVO(String name, String usernamepath, String orgnamepath, String positionname, Date optime) {
		this.name = name;
		this.usernamepath = usernamepath;
		this.orgnamepath = orgnamepath;
		this.positionname = positionname;
		this.optime = optime;
	}

	public InnerControlVO(String orgnamepath, Long userid, String name, String sex, String loginid, Date optime) {
		this.orgnamepath = orgnamepath;
		this.userid = userid;
		this.name = name;
		this.optime = optime;
		this.sex = sex;
		this.loginid = loginid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsernamepath() {
		return usernamepath;
	}

	public void setUsernamepath(String usernamepath) {
		this.usernamepath = usernamepath;
	}

	public String getOrgnamepath() {
		return orgnamepath;
	}

	public void setOrgnamepath(String orgnamepath) {
		this.orgnamepath = orgnamepath;
	}

	public String getPositionname() {
		return positionname;
	}

	public void setPositionname(String positionname) {
		this.positionname = positionname;
	}

	public Date getOptime() {
		return optime;
	}

	public void setOptime(Date optime) {
		this.optime = optime;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getLoginid() {
		return loginid;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}
}
