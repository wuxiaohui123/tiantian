package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;

public class PermissionTreeVO implements Serializable {

	private Long id;
	private Long PId;
	private String title;
	private String policy;
	private Long permissionid;
	private String name;
	private boolean open;
	private Long menulevel;
	private String isleaf;
	private boolean nocheck;
	private boolean parent;
	private boolean checked;
	private boolean checked1;
	private boolean checked2;
	private String useyab003;
	private String iconSkin;
	private String isyab003;
	private boolean chkDisabled;
	private boolean effectivetimeover;
	private boolean isaudite;

	public String getIsyab003() {
		return isyab003;
	}

	public void setIsyab003(String isyab003) {
		this.isyab003 = isyab003;
	}

	public String getIconSkin() {
		return iconSkin;
	}

	public void setIconSkin(String iconSkin) {
		this.iconSkin = iconSkin;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPId() {
		return PId;
	}

	public void setPId(Long pId) {
		PId = pId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public Long getPermissionid() {
		return permissionid;
	}

	public void setPermissionid(Long permissionid) {
		this.permissionid = permissionid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public boolean isChecked1() {
		return checked1;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public void setChecked1(boolean checked1) {
		this.checked1 = checked1;
	}

	public boolean isChecked2() {
		return checked2;
	}

	public void setChecked2(boolean checked2) {
		this.checked2 = checked2;
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

	public boolean getNocheck() {
		return nocheck;
	}

	public void setNocheck(boolean nocheck) {
		this.nocheck = nocheck;
	}

	public boolean isParent() {
		return parent;
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}

	public String getUseyab003() {
		return useyab003;
	}

	public void setUseyab003(String useyab003) {
		this.useyab003 = useyab003;
	}

	public boolean isChkDisabled() {
		return chkDisabled;
	}

	public void setChkDisabled(boolean chkDisabled) {
		this.chkDisabled = chkDisabled;
	}

	public boolean isEffectivetimeover() {
		return effectivetimeover;
	}

	public void setEffectivetimeover(boolean effectivetimeover) {
		this.effectivetimeover = effectivetimeover;
	}

	public boolean isIsaudite() {
		return isaudite;
	}

	public void setIsaudite(boolean isaudite) {
		this.isaudite = isaudite;
	}
}
