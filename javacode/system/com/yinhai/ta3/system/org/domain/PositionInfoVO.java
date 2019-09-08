package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;
import java.util.Date;

public class PositionInfoVO implements Serializable {

	private Long positionid;
	private String positionname;
	private Long orgid;
	private String orgnamepath;
	private String effective;
	private Date createtime;
	private String positiontype;
	private String username;
	private String loginid;
	private Long userid;
	private String sex;
	private String islock;
	private String mainposition;
	private String isshare;
	private String iscopy;

	public String getMainposition() {
		return mainposition;
	}

	public void setMainposition(String mainposition) {
		this.mainposition = mainposition;
	}

	public String getIslock() {
		return islock;
	}

	public void setIslock(String islock) {
		this.islock = islock;
	}

	public String getLoginid() {
		return loginid;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPositiontype() {
		return positiontype;
	}

	public void setPositiontype(String positiontype) {
		this.positiontype = positiontype;
	}

	public Long getPositionid() {
		return positionid;
	}

	public PositionInfoVO() {
	}

	public PositionInfoVO(Long positionid, String positionname, String orgnamepath, String positiontype,
			String mainposition, String isshare, String iscopy) {
		this.positionid = positionid;
		this.positionname = positionname;
		this.orgnamepath = orgnamepath;
		this.positiontype = positiontype;
		this.mainposition = mainposition;
		this.isshare = isshare;
		this.iscopy = iscopy;
	}

	public void setPositionid(Long positionid) {
		this.positionid = positionid;
	}

	public String getPositionname() {
		return positionname;
	}

	public void setPositionname(String positionname) {
		this.positionname = positionname;
	}

	public String getOrgnamepath() {
		return orgnamepath;
	}

	public void setOrgnamepath(String orgnamepath) {
		this.orgnamepath = orgnamepath;
	}

	public String getEffective() {
		return effective;
	}

	public void setEffective(String effective) {
		this.effective = effective;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Long getOrgid() {
		return orgid;
	}

	public void setOrgid(Long orgid) {
		this.orgid = orgid;
	}

	public String getIsshare() {
		return isshare;
	}

	public void setIsshare(String isshare) {
		this.isshare = isshare;
	}

	public String getIscopy() {
		return iscopy;
	}

	public void setIscopy(String iscopy) {
		this.iscopy = iscopy;
	}
}
