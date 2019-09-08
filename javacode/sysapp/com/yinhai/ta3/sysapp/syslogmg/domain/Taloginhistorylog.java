package com.yinhai.ta3.sysapp.syslogmg.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class Taloginhistorylog implements Serializable {

	private Long logid;
	private Long userid;
	private String name;
	private String username;
	private String telphone;
	private Timestamp logintime;
	private Timestamp logouttime;
	private Long onlinetime;
	private String clientip;
	private String serverip;
	private String sessionid;
	private String syspath;

	public Taloginhistorylog() {
	}

	public Taloginhistorylog(Long logid, Long userid,String username, String name, String telphone, Timestamp logintime, Timestamp logouttime,Long onlinetime, String clientip, String serverip, String sessionid,
			String syspath) {
		this.logid = logid;
		this.userid = userid;
		this.username = username;
		this.name = name;
		this.telphone = telphone;
		this.logintime = logintime;
		this.logouttime = logouttime;
		this.onlinetime = onlinetime;
		this.clientip = clientip;
		this.serverip = serverip;
		this.sessionid = sessionid;
		this.syspath = syspath;
	}

	public Long getLogid() {
		return logid;
	}

	public void setLogid(Long logid) {
		this.logid = logid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public Long getOnlinetime() {
		return onlinetime;
	}

	public void setOnlinetime(Long onlinetime) {
		this.onlinetime = onlinetime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getLogintime() {
		return logintime;
	}

	public void setLogintime(Timestamp logintime) {
		this.logintime = logintime;
	}

	public Timestamp getLogouttime() {
		return logouttime;
	}

	public void setLogouttime(Timestamp logouttime) {
		this.logouttime = logouttime;
	}

	public String getClientip() {
		return clientip;
	}

	public void setClientip(String clientip) {
		this.clientip = clientip;
	}

	public String getServerip() {
		return serverip;
	}

	public void setServerip(String serverip) {
		this.serverip = serverip;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String getSyspath() {
		return syspath;
	}

	public void setSyspath(String syspath) {
		this.syspath = syspath;
	}
}
