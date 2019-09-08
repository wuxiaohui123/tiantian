package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;

public class UploadOrgUserVO implements Serializable {

	private String tempPath;
	private String porgname;
	private String orgname;
	private String orgtype;
	private String loginid;
	private String username;
	private String sex;
	private String password;
	private String tel;

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getTempPath() {
		return tempPath;
	}

	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}

	public String getPorgname() {
		return porgname;
	}

	public void setPorgname(String porgname) {
		this.porgname = porgname;
	}

	public String getOrgname() {
		return orgname;
	}

	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}

	public String getOrgtype() {
		return orgtype;
	}

	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}

	public String getLoginid() {
		return loginid;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOrgUserFieldsStr() {
		return "tempPath,porgname,orgname,orgtype,loginid,username,sex,password,tel";
	}

	public String getOrgFieldsStr() {
		return "tempPath,porgname,orgname,orgtype";
	}

	public String getUserFieldsStr() {
		return "tempPath,orgname,loginid,username,sex,password,tel";
	}
}
