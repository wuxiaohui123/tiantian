package com.yinhai.ta3.system.org.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jws.WebMethod;
import javax.xml.bind.annotation.XmlTransient;

import com.yinhai.sysframework.app.domain.BaseDomain;
import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.iorg.IOrg;

public class Org extends BaseDomain implements IOrg {

	private Long orgid;
	private String orgname;
	private Org pOrg;
	private String costomno;
	private String orgidpath;
	private String orgnamepath;
	private String costomnopath;
	private String orgtype;
	private Integer sort;
	private String yab003;
	private String dimension;
	private Long createuser;
	private Date createtime;
	private String effective;
	private Long orglevel;
	private String isleaf;
	private Long orgmanager;
	private String destory;
	private String yab139;
	private Set<Position> tapositions = new HashSet<Position>(0);

	public Org() {
	}

	public Org(Long orgid) {
		this.orgid = orgid;
	}

	public Org(String orgname, String orgtype) {
		this.orgname = orgname;
		this.orgtype = orgtype;
	}

	public Org(String orgname, Long porgid, String costomno, String orgidpath, String orgnamepath, String costomnopath,
			String orgtype, Integer sort, String yab003, String dimension, Long createuser, Date createtime,
			Set<Position> tapositions, Org pOrg, Long orglevel, String isleaf, Long orgmanager, String destory, String yab139) {
		this.orgname = orgname;
		this.costomno = costomno;
		this.orgidpath = orgidpath;
		this.orgnamepath = orgnamepath;
		this.costomnopath = costomnopath;
		this.orgtype = orgtype;
		this.sort = sort;
		this.yab003 = yab003;
		this.dimension = dimension;
		this.createuser = createuser;
		this.createtime = createtime;
		this.tapositions = tapositions;
		this.pOrg = pOrg;
		this.isleaf = isleaf;
		this.orglevel = orglevel;
		this.orgmanager = orgmanager;
		this.destory = destory;
		this.yab139 = yab139;
	}

	public Long getOrgid() {
		return orgid;
	}

	public void setOrgid(Long orgid) {
		this.orgid = orgid;
	}

	public String getOrgname() {
		return orgname;
	}

	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}

	@XmlTransient
	public Org getpOrg() {
		return pOrg;
	}

	public void setpOrg(Org pOrg) {
		this.pOrg = pOrg;
	}

	public String getCostomno() {
		return costomno;
	}

	public void setCostomno(String costomno) {
		this.costomno = costomno;
	}

	public String getOrgidpath() {
		return orgidpath;
	}

	public void setOrgidpath(String orgidpath) {
		this.orgidpath = orgidpath;
	}

	public String getOrgnamepath() {
		return orgnamepath;
	}

	public void setOrgnamepath(String orgnamepath) {
		this.orgnamepath = orgnamepath;
	}

	public String getCostomnopath() {
		return costomnopath;
	}

	public void setCostomnopath(String costomnopath) {
		this.costomnopath = costomnopath;
	}

	public String getOrgtype() {
		return orgtype;
	}

	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getYab003() {
		return yab003;
	}

	public void setYab003(String yab003) {
		this.yab003 = yab003;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
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

	@XmlTransient
	@WebMethod(exclude = true)
	public Set<Position> getTapositions() {
		return tapositions;
	}

	public void setTapositions(Set<Position> tapositions) {
		this.tapositions = tapositions;
	}

	public Long getPorgid() {
		if (getpOrg() != null)
			return getpOrg().getOrgid();
		return null;
	}

	public void setPorgid(Long porgid) {
		pOrg = new Org(porgid);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orgid", getOrgid());
		map.put("orgname", getOrgname());
		map.put("porgid", getPorgid());
		map.put("costomno", getCostomno());
		map.put("orgidpath", getOrgidpath());
		map.put("orgnamepath", getOrgnamepath());
		map.put("costomnopath", getCostomnopath());
		map.put("orgtype", getOrgtype());
		map.put("sort", getSort());
		map.put("yab003", getYab003());
		map.put("dimension", getDimension());
		map.put("createuser", getCreateuser());
		map.put("createtime", getCreatetime());
		map.put("effective", getEffective());
		map.put("isleaf", getIsleaf());
		map.put("orglevel", getOrglevel());
		map.put("orgmanager", getOrgmanager());
		map.put("destory", getDestory());
		map.put("yab139", getYab139());
		return map;
	}

	public Key getPK() {
		Key key = new Key();
		key.put("orgid", getOrgid());
		return key;
	}

	public String getDestory() {
		return destory;
	}

	public void setDestory(String destory) {
		this.destory = destory;
	}

	public String getEffective() {
		return effective;
	}

	public void setEffective(String effective) {
		this.effective = effective;
	}

	public Long getOrglevel() {
		return orglevel;
	}

	public void setOrglevel(Long orglevel) {
		this.orglevel = orglevel;
	}

	public String getIsleaf() {
		return isleaf;
	}

	public void setIsleaf(String isleaf) {
		this.isleaf = isleaf;
	}

	public Long getOrgmanager() {
		return orgmanager;
	}

	public void setOrgmanager(Long orgmanager) {
		this.orgmanager = orgmanager;
	}

	public String getYab139() {
		return yab139;
	}

	public void setYab139(String yab139) {
		this.yab139 = yab139;
	}

	public String toString() {
		return "Org [orgid=" + orgid + ", orgname=" + orgname + ", costomno=" + costomno + ", orgidpath=" + orgidpath
				+ ", orgnamepath=" + orgnamepath + ", costomnopath=" + costomnopath + ", orgtype=" + orgtype
				+ ", sort=" + sort + ", yab003=" + yab003 + ", dimension=" + dimension + ", createuser=" + createuser
				+ ", createtime=" + createtime + ", effective=" + effective + ", orglevel=" + orglevel + ", isleaf="
				+ isleaf + ", orgmanager=" + orgmanager + ", destory=" + destory + ", yab139=" + yab139 + "]";
	}

}
