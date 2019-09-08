package com.yinhai.ta3.system.org.domain;

import java.util.Date;

public class PermissionInfoVO {

	private Long permissionid;
	private Long positionid;
	private Date effectivetime;
	private Long operator;
	private Date operateTime;
	private String opertype;
	private String permissiontype;

	public PermissionInfoVO(Long permissionid, Long positionid, Long operator, Date operateTime, String opertype,
			String permissiontype) {
		this.permissionid = permissionid;
		this.positionid = positionid;
		this.operator = operator;
		this.operateTime = operateTime;
		this.opertype = opertype;
		this.permissiontype = permissiontype;
	}

	public PermissionInfoVO(Long permissionid, Long positionid, Date effectivetime, Long operator, Date operateTime,
			String opertype, String permissiontype) {
		this.permissionid = permissionid;
		this.positionid = positionid;
		this.effectivetime = effectivetime;
		this.operator = operator;
		this.operateTime = operateTime;
		this.opertype = opertype;
		this.permissiontype = permissiontype;
	}

	public Long getPermissionid() {
		return permissionid;
	}

	public void setPermissionid(Long permissionid) {
		this.permissionid = permissionid;
	}

	public Long getPositionid() {
		return positionid;
	}

	public void setPositionid(Long positionid) {
		this.positionid = positionid;
	}

	public Long getOperator() {
		return operator;
	}

	public void setOperator(Long operator) {
		this.operator = operator;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public String getOpertype() {
		return opertype;
	}

	public void setOpertype(String opertype) {
		this.opertype = opertype;
	}

	public String getPermissiontype() {
		return permissiontype;
	}

	public void setPermissiontype(String permissiontype) {
		this.permissiontype = permissiontype;
	}

	public Date getEffectivetime() {
		return effectivetime;
	}

	public void setEffectivetime(Date effectivetime) {
		this.effectivetime = effectivetime;
	}
}
