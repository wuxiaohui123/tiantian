package com.yinhai.sysframework.codetable.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.yinhai.sysframework.app.domain.BaseDomain;
import com.yinhai.sysframework.app.domain.DomainMeta;
import com.yinhai.sysframework.util.StringUtil;

public class AppLevelCode extends BaseDomain {

	private String id;
	private String pid;
	private String name;
	private String isparent;
	private String type;
	private String icon;
	private String open;
	private String codedesc;
	private String py;
	private BigDecimal levelvalue;
	private String orgId;
	private String leaf;

	public AppLevelCode() {
	}

	public AppLevelCode(String id) {
		this.id = id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPid() {
		return pid;
	}

	public void setName(String name) {
		this.name = name;
		setPy(StringUtil.getPYString(name));
	}

	public String getName() {
		return name;
	}

	public void setIsparent(String isparent) {
		this.isparent = isparent;
	}

	public String getIsparent() {
		return isparent;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIcon() {
		return icon;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getOpen() {
		return open;
	}

	public void setLevelvalue(BigDecimal levelvalue) {
		this.levelvalue = levelvalue;
	}

	public BigDecimal getLevelvalue() {
		return levelvalue;
	}

	public void setLeaf(String leaf) {
		this.leaf = leaf;
	}

	public String getLeaf() {
		return leaf;
	}

	public String getCodedesc() {
		return codedesc;
	}

	public void setCodedesc(String codedesc) {
		this.codedesc = codedesc;
	}

	public Map toMap() {
		Map map = new HashMap();
		map.put("id", getId());
		map.put("pid", getPid());
		map.put("name", getName());
		map.put("isparent", getIsparent());
		map.put("type", getType());
		map.put("icon", getIcon());
		map.put("open", getOpen());
		map.put("levelvalue", getLevelvalue());
		map.put("leaf", getLeaf());
		map.put("codedesc", getCodedesc());
		map.put("py", getPy());

		return map;
	}

	public DomainMeta getMetadata() {
		DomainMeta domainMeta = new DomainMeta("Aa10tree1Domain", "aa10tree1", "null", "aa10tree1", "");

		domainMeta.appendField("id", "id", "", "String", "VARCHAR2(15)", 15, false, true, false);

		domainMeta.appendField("pid", "pid", "", "String", "VARCHAR2(15)", 15, false, false, false);

		domainMeta.appendField("name", "name", "", "String", "VARCHAR2(50)", 50, false, false, false);

		domainMeta.appendField("isparent", "isparent", "", "String", "VARCHAR2(0)", 0, false, false, false);

		domainMeta.appendField("type", "type", "", "String", "CHAR(6)", 6, false, false, false);

		domainMeta.appendField("icon", "icon", "", "String", "CHAR(0)", 0, false, false, false);

		domainMeta.appendField("open", "open", "", "String", "CHAR(0)", 0, false, false, false);

		domainMeta.appendField("levelvalue", "levelvalue", "", "java.math.BigDecimal", "NUMBER(22)", 22, false, false,
				false);

		domainMeta.appendField("leaf", "leaf", "", "String", "CHAR(1)", 1, false, false, false);

		return domainMeta;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getPy() {
		if (null == py)
			setPy(StringUtil.getPYString(name));
		return py;
	}

	public void setPy(String py) {
		this.py = py;
	}
}
