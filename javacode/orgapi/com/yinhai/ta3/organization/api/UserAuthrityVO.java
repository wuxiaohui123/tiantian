package com.yinhai.ta3.organization.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.yinhai.sysframework.app.domain.BaseVO;

public class UserAuthrityVO extends BaseVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4950624114006137111L;
	Long permissionId;
	String permissionName;
	String menuNamePath;
	String permissionType;
	String usePermission;
	String repermission;
	String reauthrity;
	Long positionid;
	String positionnamepath;
	Long createuser;
	Date createtime;

	public UserAuthrityVO(Long permissionId, String permissionName, String menuNamePath, String permissionType, String usePermission,
			String repermission, String reauthrity, Long positionid, String positionnamepath, Long createuser, Date createtime) {
		this.permissionId = permissionId;
		this.permissionName = permissionName;
		this.menuNamePath = menuNamePath;
		this.permissionType = permissionType;
		this.usePermission = usePermission;
		this.repermission = repermission;
		this.reauthrity = reauthrity;
		this.positionid = positionid;
		this.positionnamepath = positionnamepath;
		this.createuser = createuser;
		this.createtime = createtime;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("permissionId", getPermissionId());
		map.put("permissionName", getPermissionName());
		map.put("menuNamePath", getMenuNamePath());
		map.put("permissionType", getPermissionType());
		map.put("usePermission", getUsePermission());
		map.put("repermission", getRepermission());
		map.put("reauthrity", getReauthrity());
		map.put("positionid", getPositionid());
		map.put("positionnamepath", getPermissionName());
		map.put("createuser", getCreateuser());
		map.put("createtime", getCreatetime());
		return map;
	}

	public Long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public String getMenuNamePath() {
		return menuNamePath;
	}

	public void setMenuNamePath(String menuNamePath) {
		this.menuNamePath = menuNamePath;
	}

	public String getPermissionType() {
		return permissionType;
	}

	public void setPermissionType(String permissionType) {
		this.permissionType = permissionType;
	}

	public String getUsePermission() {
		return usePermission;
	}

	public void setUsePermission(String usePermission) {
		this.usePermission = usePermission;
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

	public Long getPositionid() {
		return positionid;
	}

	public void setPositionid(Long positionid) {
		this.positionid = positionid;
	}

	public String getPositionnamepath() {
		return positionnamepath;
	}

	public void setPositionnamepath(String positionnamepath) {
		this.positionnamepath = positionnamepath;
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
