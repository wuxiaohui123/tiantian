package com.yinhai.ta3.sysapp.syslogmg.domain;

import java.io.Serializable;
import java.util.Date;

public class AccessLog implements Serializable {

	private Long logid;
	private Long userid;
	private Long positionid;
	private Long permissionid;
	private String ispermission;
	private Date accesstime;
	private String url;
	private String sysflag;

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Long getPositionid() {
		return positionid;
	}

	public void setPositionid(Long positionid) {
		this.positionid = positionid;
	}

	public Long getPermissionid() {
		return permissionid;
	}

	public void setPermissionid(Long permissionid) {
		this.permissionid = permissionid;
	}

	public String getIspermission() {
		return ispermission;
	}

	public void setIspermission(String ispermission) {
		this.ispermission = ispermission;
	}

	public Date getAccesstime() {
		return accesstime;
	}

	public void setAccesstime(Date accesstime) {
		this.accesstime = accesstime;
	}

	public AccessLog() {
	}

	public Long getLogid() {
		return logid;
	}

	public void setLogid(Long logid) {
		this.logid = logid;
	}

	public AccessLog(Long logid, Long userid, Long positionid, Long permissionid, String ispermission, Date accesstime, String url, String sysflag) {
		this.logid = logid;
		this.userid = userid;
		this.positionid = positionid;
		this.permissionid = permissionid;
		this.ispermission = ispermission;
		this.accesstime = accesstime;
		this.url = url;
		this.sysflag = sysflag;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSysflag() {
		return sysflag;
	}

	public void setSysflag(String sysflag) {
		this.sysflag = sysflag;
	}
}
