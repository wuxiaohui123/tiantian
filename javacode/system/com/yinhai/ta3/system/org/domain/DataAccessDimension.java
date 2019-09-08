package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;

public class DataAccessDimension implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4919628221073563969L;
	public static final String DIMENSIONTYPE_YAB003 = "YAB003";
	public static final String DIMENSIONTYPE_YAB139 = "YAB139";
	private Long dimensionid;
	private Long positionid;
	private Long menuid;
	private String dimensiontype;
	private String dimensionpermissionid;
	private String allaccess;
	private String syspath;

	public DataAccessDimension() {
	}

	public DataAccessDimension(Long positionid, Long menuid, String dimensiontype, String dimensionpermissionid) {
		this.positionid = positionid;
		this.menuid = menuid;
		this.dimensiontype = dimensiontype;
		this.dimensionpermissionid = dimensionpermissionid;
	}

	public DataAccessDimension(Long positionid, Long menuid, String dimensiontype, String dimensionpermissionid,
			String allaccess, String syspath) {
		this.positionid = positionid;
		this.menuid = menuid;
		this.dimensiontype = dimensiontype;
		this.dimensionpermissionid = dimensionpermissionid;
		this.allaccess = allaccess;
		this.syspath = syspath;
	}

	public Long getDimensionid() {
		return dimensionid;
	}

	public void setDimensionid(Long dimensionid) {
		this.dimensionid = dimensionid;
	}

	public Long getPositionid() {
		return positionid;
	}

	public void setPositionid(Long positionid) {
		this.positionid = positionid;
	}

	public Long getMenuid() {
		return menuid;
	}

	public void setMenuid(Long menuid) {
		this.menuid = menuid;
	}

	public String getDimensiontype() {
		return dimensiontype;
	}

	public void setDimensiontype(String dimensiontype) {
		this.dimensiontype = dimensiontype;
	}

	public String getDimensionpermissionid() {
		return dimensionpermissionid;
	}

	public void setDimensionpermissionid(String dimensionpermissionid) {
		this.dimensionpermissionid = dimensionpermissionid;
	}

	public String getAllaccess() {
		return allaccess;
	}

	public void setAllaccess(String allaccess) {
		this.allaccess = allaccess;
	}

	public String getSyspath() {
		return syspath;
	}

	public void setSyspath(String syspath) {
		this.syspath = syspath;
	}
}
