package com.yinhai.ta3.system.org.domain;

import java.util.HashMap;
import java.util.Map;

import com.yinhai.sysframework.app.domain.BaseDomain;

public class OrgTreeVO extends BaseDomain {

	private Long id;
	private Long pId;
	private String name;
	private String orgnamepath;
	private String orgtype;
	private String porgname;
	private String effective;
	private String costomno;
	private String yab003;

	public OrgTreeVO(Long id, Long pId, String name, String orgnamepath, String orgtype, String porgname,
			String effective, String costomno, String yab003) {
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.orgnamepath = orgnamepath;
		this.orgtype = orgtype;
		this.porgname = porgname;
		this.effective = effective;
		this.costomno = costomno;
		this.yab003 = yab003;
	}

	public Map toMap() {
		Map map = new HashMap();
		map.put("id", getId());
		map.put("pId", getpId());
		map.put("name", getName());
		map.put("orgnamepath", getOrgnamepath());
		map.put("orgtype", getOrgtype());
		map.put("porgname", getPorgname());
		map.put("effective", getEffective());
		map.put("costomno", getCostomno());
		map.put("yab003", getYab003());
		return map;
	}

	public String getYab003() {
		return yab003;
	}

	public void setYab003(String yab003) {
		this.yab003 = yab003;
	}

	public String getCostomno() {
		return costomno;
	}

	public void setCostomno(String costomno) {
		this.costomno = costomno;
	}

	public String getPorgname() {
		return porgname;
	}

	public void setPorgname(String porgname) {
		this.porgname = porgname;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEffective() {
		return effective;
	}

	public void setEffective(String effective) {
		this.effective = effective;
	}

	public Long getpId() {
		return pId;
	}

	public void setpId(Long pId) {
		this.pId = pId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrgnamepath() {
		return orgnamepath;
	}

	public void setOrgnamepath(String orgnamepath) {
		this.orgnamepath = orgnamepath;
	}

	public String getOrgtype() {
		return orgtype;
	}

	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}
}
