package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;
import java.util.Date;

public class PositionAuthrity implements Serializable {

	private PositionAuthrityId id;
	private String usepermission;
	private String repermission;
	private String reauthrity;
	private Long createuser;
	private Date createtime;
	private Date effecttime;
	private Date auditeaccessdate;
	private Long auditeuser;
	private String auditstate;

	public PositionAuthrity() {
	}

	public PositionAuthrity(PositionAuthrityId id) {
		this.id = id;
	}

	public PositionAuthrity(PositionAuthrityId id, String usepermission, String repermission, String reauthrity,
			Long createuser, Date createtime, Date effecttime, Date auditeaccessdate, Long auditeuser, String auditstate) {
		this.id = id;
		this.usepermission = usepermission;
		this.repermission = repermission;
		this.reauthrity = reauthrity;
		this.createuser = createuser;
		this.createtime = createtime;
		this.effecttime = effecttime;
		this.auditeaccessdate = auditeaccessdate;
		this.auditeuser = auditeuser;
		this.auditstate = auditstate;
	}

	public PositionAuthrityId getId() {
		return id;
	}

	public void setId(PositionAuthrityId id) {
		this.id = id;
	}

	public String getUsepermission() {
		return usepermission;
	}

	public void setUsepermission(String usepermission) {
		this.usepermission = usepermission;
	}

	public String getRepermission() {
		return repermission;
	}

	public void setRepermission(String repermission) {
		this.repermission = repermission;
	}

	public String getReauthrity() {
		return reauthrity;
	}

	public void setReauthrity(String reauthrity) {
		this.reauthrity = reauthrity;
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

	public Date getEffecttime() {
		return effecttime;
	}

	public void setEffecttime(Date effecttime) {
		this.effecttime = effecttime;
	}

	public Date getAuditeaccessdate() {
		return auditeaccessdate;
	}

	public void setAuditeaccessdate(Date auditeaccessdate) {
		this.auditeaccessdate = auditeaccessdate;
	}

	public Long getAuditeuser() {
		return auditeuser;
	}

	public void setAuditeuser(Long auditeuser) {
		this.auditeuser = auditeuser;
	}

	public String getAuditstate() {
		return auditstate;
	}

	public void setAuditstate(String auditstate) {
		this.auditstate = auditstate;
	}
}
