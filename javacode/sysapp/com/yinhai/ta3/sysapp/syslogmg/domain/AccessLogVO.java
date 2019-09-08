package com.yinhai.ta3.sysapp.syslogmg.domain;

import java.io.Serializable;
import java.util.Date;

public class AccessLogVO implements Serializable {

	private Long userid;
	private String name;
	private Long positionid;
	private String positionnamepath;
	private Long permissionid;
	private String menunamepath;
	private String ispermission;
	private Date accesstime;
	private String url;
	private String sysflag;

	
	
	public AccessLogVO() {
	}

	public AccessLogVO(Long userid, String name, Long positionid, String positionnamepath, Long permissionid, String menunamepath,
			String ispermission, Date accesstime, String url, String sysflag) {
		this.userid = userid;
		this.name = name;
		this.positionid = positionid;
		this.positionnamepath = positionnamepath;
		this.permissionid = permissionid;
		this.menunamepath = menunamepath;
		this.ispermission = ispermission;
		this.accesstime = accesstime;
		this.url = url;
		this.sysflag = sysflag;
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

	public String getPositionnamepath() {
		return positionnamepath;
	}

	public void setPositionnamepath(String positionnamepath) {
		this.positionnamepath = positionnamepath;
	}

	public String getMenunamepath() {
		return menunamepath;
	}

	public void setMenunamepath(String menunamepath) {
		this.menunamepath = menunamepath;
	}
}
