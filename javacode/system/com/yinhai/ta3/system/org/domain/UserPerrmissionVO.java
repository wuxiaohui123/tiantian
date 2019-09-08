package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;
import java.util.Date;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;

public class UserPerrmissionVO implements Serializable {

	private String menuname;
	private String url;
	private String type;
	private String usepermission;
	private String repermission;
	private String reauthrity;
	private String positionname;
	private String name;
	private Date createtime;
	private String orgnamepath;
	private String auditstate;
	private Long menuid;

	public UserPerrmissionVO(String menuname, String url, String type, String usepermission, String repermission,
			String reauthrity, String positionname, String name, Date createtime, String orgnamepath) {
		this.menuname = menuname;
		this.url = url;
		this.type = type;
		this.usepermission = usepermission;
		this.repermission = repermission;
		this.reauthrity = reauthrity;
		this.positionname = positionname;
		this.name = name;
		this.createtime = createtime;
		this.orgnamepath = orgnamepath;
	}

	public UserPerrmissionVO(String menuname, String url, String type, String usepermission, String repermission,
			String reauthrity, String positionname, String name, Date createtime, String orgnamepath,
			String auditstate, Long menuid) {
		this.menuname = menuname;
		this.url = url;
		this.type = type;
		this.usepermission = usepermission;
		this.repermission = repermission;
		this.reauthrity = reauthrity;
		this.positionname = positionname;
		this.name = name;
		this.createtime = createtime;
		this.orgnamepath = orgnamepath;
		this.auditstate = auditstate;
		this.menuid = menuid;
	}

	public String getMenuname() {
		return menuname;
	}

	public void setMenuname(String menuname) {
		this.menuname = menuname;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getPositionname() {
		return positionname;
	}

	public void setPositionname(String positionname) {
		this.positionname = positionname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getOrgnamepath() {
		return orgnamepath;
	}

	public void setOrgnamepath(String orgnamepath) {
		this.orgnamepath = orgnamepath;
	}

	public String getAuditstate() {
		return auditstate;
	}

	public void setAuditstate(String auditstate) {
		this.auditstate = auditstate;
	}

	public Long getMenuid() {
		return menuid;
	}

	public void setMenuid(Long menuid) {
		this.menuid = menuid;
	}

}
