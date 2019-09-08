package com.yinhai.ta3.system.sysapp.domain;

import java.util.HashMap;
import java.util.Map;

import com.yinhai.sysframework.app.domain.BaseDomain;
import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.menu.IMenu;

public class Menu extends BaseDomain implements IMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1027911703111413745L;
	private Long menuid;
	private Long pmenuid;
	private String menuname;
	private String url;
	private String menuidpath;
	private String menunamepath;
	private String iconSkin;
	private String selectImage;
	private String reportid;
	private String accesstimeel;
	private String effective;
	private String securitypolicy;
	private String isdismultipos;
	private String quickcode;
	private Long sortno;
	private String resourcetype;
	private Long menulevel;
	private String isleaf;
	private String menutype;
	private String iscache;
	private String syspath;
	private String useyab003;
	private String isaudite;
	private String consolemodule;
	private boolean isParent = false;

	private boolean open;

	public Menu() {
	}

	public Menu(Long menuid, Long pmenuid, String menuname, String url, String menuidpath, String menunamepath,
			String iconSkin, String selectImage, String reportid, String accesstimeel, String effective,
			String securitypolicy, String isdismultipos, String quickcode, Long sortno, String resourcetype,
			Long menulevel, String isleaf, String menutype, String iscache, boolean isParent, boolean open,
			String syspath, String useyab003, String isaudite, String consolemodule) {
		this.menuid = menuid;
		this.pmenuid = pmenuid;
		this.menuname = menuname;
		this.url = url;
		this.menuidpath = menuidpath;
		this.menunamepath = menunamepath;
		this.iconSkin = iconSkin;
		this.selectImage = selectImage;
		this.reportid = reportid;
		this.accesstimeel = accesstimeel;
		this.effective = effective;
		this.securitypolicy = securitypolicy;
		this.isdismultipos = isdismultipos;
		this.quickcode = quickcode;
		this.sortno = sortno;
		this.resourcetype = resourcetype;
		this.menulevel = menulevel;
		this.isleaf = isleaf;
		this.menutype = menutype;
		this.iscache = iscache;
		this.isParent = isParent;
		this.open = open;
		this.syspath = syspath;
		this.useyab003 = useyab003;
		this.isaudite = isaudite;
		this.consolemodule = consolemodule;
	}

	public Long getMenuid() {
		return menuid;
	}

	public void setMenuid(Long menuid) {
		this.menuid = menuid;
	}

	public Long getPmenuid() {
		return pmenuid;
	}

	public void setPmenuid(Long pmenuid) {
		this.pmenuid = pmenuid;
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

	public String getMenuidpath() {
		return menuidpath;
	}

	public void setMenuidpath(String menuidpath) {
		this.menuidpath = menuidpath;
	}

	public String getMenunamepath() {
		return menunamepath;
	}

	public void setMenunamepath(String menunamepath) {
		this.menunamepath = menunamepath;
	}

	public String getIconSkin() {
		return iconSkin;
	}

	public void setIconSkin(String iconSkin) {
		this.iconSkin = iconSkin;
	}

	public String getSelectImage() {
		return selectImage;
	}

	public void setSelectImage(String selectImage) {
		this.selectImage = selectImage;
	}

	public boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(boolean isParent) {
		this.isParent = isParent;
	}

	public String getReportid() {
		return reportid;
	}

	public void setReportid(String reportid) {
		this.reportid = reportid;
	}

	public String getAccesstimeel() {
		return accesstimeel;
	}

	public void setAccesstimeel(String accesstimeel) {
		this.accesstimeel = accesstimeel;
	}

	public String getEffective() {
		return effective;
	}

	public void setEffective(String effective) {
		this.effective = effective;
	}

	public String getSecuritypolicy() {
		return securitypolicy;
	}

	public void setSecuritypolicy(String securitypolicy) {
		this.securitypolicy = securitypolicy;
	}

	public String getIsdismultipos() {
		return isdismultipos;
	}

	public void setIsdismultipos(String isdismultipos) {
		this.isdismultipos = isdismultipos;
	}

	public String getQuickcode() {
		return quickcode;
	}

	public void setQuickcode(String quickcode) {
		this.quickcode = quickcode;
	}

	public Long getSortno() {
		return sortno;
	}

	public void setSortno(Long sortno) {
		this.sortno = sortno;
	}

	public boolean effective() {
		return "0".equals(getEffective());
	}

	public boolean isDismultipos() {
		return "1".equals(getIsdismultipos());
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("menuid", getMenuid());
		map.put("pmenuid", getPmenuid());
		map.put("menuname", getMenuname());
		map.put("url", getUrl());
		map.put("menuidpath", getMenuidpath());
		map.put("menunamepath", getMenunamepath());
		map.put("iconSkin", getIconSkin());
		map.put("selectImage", getSelectImage());
		map.put("reportid", getReportid());
		map.put("accesstimeel", getAccesstimeel());
		map.put("effective", getEffective());
		map.put("securitypolicy", getSecuritypolicy());
		map.put("isdismultipos", getIsdismultipos());
		map.put("quickcode", getQuickcode());
		map.put("isParent", Boolean.valueOf(isParent()));
		map.put("open", Boolean.valueOf(isOpen()));
		map.put("resourcetype", getResourcetype());
		map.put("menulevel", getMenulevel());
		map.put("isleaf", getIsleaf());
		map.put("menutype", getMenutype());
		map.put("iscache", getIscache());
		map.put("syspath", getSyspath());
		map.put("useyab003", getUseyab003());
		map.put("isaudite", getIsaudite());
		map.put("consolemodule", getConsolemodule());
		return map;
	}

	@SuppressWarnings("unchecked")
	public Key getPK() {
		Key key = new Key();
		key.put("menuid", getMenuid());
		return key;
	}

	public String getResourcetype() {
		return resourcetype;
	}

	public void setResourcetype(String resourcetype) {
		this.resourcetype = resourcetype;
	}

	public Long getMenulevel() {
		return menulevel;
	}

	public void setMenulevel(Long menulevel) {
		this.menulevel = menulevel;
	}

	public String getIsleaf() {
		return isleaf;
	}

	public void setIsleaf(String isleaf) {
		this.isleaf = isleaf;
	}

	public String getMenutype() {
		return menutype;
	}

	public void setMenutype(String menutype) {
		this.menutype = menutype;
	}

	public String getIscache() {
		return iscache;
	}

	public void setIscache(String iscache) {
		this.iscache = iscache;
	}

	public String getSyspath() {
		return syspath;
	}

	public void setSyspath(String syspath) {
		this.syspath = syspath;
	}

	public String getUseyab003() {
		return useyab003;
	}

	public void setUseyab003(String useyab003) {
		this.useyab003 = useyab003;
	}

	public boolean isParent() {
		return getIsParent();
	}

	public void setParent(boolean isParent) {
		setIsParent(isParent);
	}

	public String getIsaudite() {
		return isaudite;
	}

	public void setIsaudite(String isaudite) {
		this.isaudite = isaudite;
	}

	public String getConsolemodule() {
		return consolemodule;
	}

	public void setConsolemodule(String consolemodule) {
		this.consolemodule = consolemodule;
	}
}
