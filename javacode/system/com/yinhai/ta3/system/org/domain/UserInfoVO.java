package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;

public class UserInfoVO implements Serializable {

	private Long userid;
	private String name;
	private String loginid;
	private String sex;
	private String tel;
	private Long positionid;
	private String orgnamepath;
	private String positionname;
	private String positiontype;

	public UserInfoVO() {
	}

	public UserInfoVO(Long userid) {
		this.userid = userid;
	}

	public UserInfoVO(Long userid, String name, String loginid, String sex, String tel, String orgnamepath,
			String positionname, String positiontype) {
		this.userid = userid;
		this.name = name;
		this.loginid = loginid;
		this.sex = sex;
		this.tel = tel;
		this.orgnamepath = orgnamepath;
		this.positionname = positionname;
		this.positiontype = positiontype;
	}

	public UserInfoVO(String orgnamepath, Long userid, String name, String sex, String loginid) {
		this.orgnamepath = orgnamepath;
		this.userid = userid;
		this.name = name;
		this.sex = sex;
		this.loginid = loginid;
	}

	public UserInfoVO(Long positionid, String orgnamepath, Long userid, String name, String sex, String loginid) {
		this(orgnamepath, userid, name, sex, loginid);
		this.positionid = positionid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
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

	public String getPositiontype() {
		return positiontype;
	}

	public void setPositiontype(String positiontype) {
		this.positiontype = positiontype;
	}

	public Long getPositionid() {
		return positionid;
	}

	public void setPositionid(Long positionid) {
		this.positionid = positionid;
	}
}
