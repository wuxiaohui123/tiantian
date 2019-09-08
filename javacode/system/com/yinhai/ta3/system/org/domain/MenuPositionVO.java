package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;
import java.util.Date;

import com.yinhai.sysframework.util.ValidateUtil;

public class MenuPositionVO implements Serializable {

	private Long menuid;
	private Long pmenuid;
	private String menuidpath;
	private String useyab003;
	private String securitypolicy;
	private String menuname;
	private Long menulevel;
	private Date effecttime;
	private String auditstate;
	private Long positionid;
	private String positionname;
	private String positionnamepath;
	private String positiontype;
	private String isleaf;
	private String nbsp;
	private String hasdp;
	private String dp;
	private String perview;

	public String getIsleaf() {
		return isleaf;
	}

	public void setIsleaf(String isleaf) {
		this.isleaf = isleaf;
	}

	public String getNbsp() {
		return nbsp;
	}

	public void setNbsp(String nbsp) {
		this.nbsp = nbsp;
	}

	public String getHasdp() {
		return hasdp;
	}

	public void setHasdp(String hasdp) {
		this.hasdp = hasdp;
	}

	public String getDp() {
		return dp;
	}

	public void setDp(String dp) {
		this.dp = dp;
	}

	public String getPerview() {
		return perview;
	}

	public void setPerview(String perview) {
		this.perview = perview;
	}

	public String getPositiontype() {
		return positiontype;
	}

	public void setPositiontype(String positiontype) {
		this.positiontype = positiontype;
	}

	public Long getMenuid() {
		return menuid;
	}

	public void setMenuid(Long menuid) {
		this.menuid = menuid;
	}

	public String getUseyab003() {
		return useyab003;
	}

	public void setUseyab003(String useyab003) {
		this.useyab003 = useyab003;
	}

	public String getSecuritypolicy() {
		return securitypolicy;
	}

	public void setSecuritypolicy(String securitypolicy) {
		this.securitypolicy = securitypolicy;
	}

	public Long getPmenuid() {
		return pmenuid;
	}

	public void setPmenuid(Long pmenuid) {
		this.pmenuid = pmenuid;
	}

	public String getMenuidpath() {
		return menuidpath;
	}

	public void setMenuidpath(String menuidpath) {
		this.menuidpath = menuidpath;
	}

	public String getMenuname() {
		return menuname;
	}

	public void setMenuname(String menuname) {
		this.menuname = menuname;
	}

	public Long getMenulevel() {
		return menulevel;
	}

	public void setMenulevel(Long menulevel) {
		this.menulevel = menulevel;
	}

	public Date getEffecttime() {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
		if (!ValidateUtil.isEmpty(effecttime)) {
			try {
				return sdf.parse(effecttime.toString());
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
		}
		return effecttime;
	}

	public void setEffecttime(Date effecttime) {
		this.effecttime = effecttime;
	}

	public String getAuditstate() {
		return auditstate;
	}

	public void setAuditstate(String auditstate) {
		this.auditstate = auditstate;
	}

	public Long getPositionid() {
		return positionid;
	}

	public void setPositionid(Long positionid) {
		this.positionid = positionid;
	}

	public String getPositionname() {
		return positionname;
	}

	public void setPositionname(String positionname) {
		this.positionname = positionname;
	}

	public String getPositionnamepath() {
		return positionnamepath;
	}

	public void setPositionnamepath(String positionnamepath) {
		this.positionnamepath = positionnamepath;
	}

	public MenuPositionVO() {
	}

	public MenuPositionVO(Long menuid, Long pmenuid, String menuidpath, String isleaf, String useyab003,
			String securitypolicy, String menuname, Long menulevel, Date effecttime, String auditstate) {
		this.menuid = menuid;
		this.pmenuid = pmenuid;
		this.menuidpath = menuidpath;
		this.isleaf = isleaf;
		this.useyab003 = useyab003;
		this.securitypolicy = securitypolicy;
		this.menuname = menuname;
		this.menulevel = menulevel;
		this.effecttime = effecttime;
		this.auditstate = auditstate;
	}

	public MenuPositionVO(Long menuid, Long pmenuid, String menuidpath, String isleaf, String useyab003,
			String securitypolicy, String menuname, Long menulevel, Date effecttime, String auditstate,
			Long positionid, String positionname, String positionnamepath, String positiontype) {
		this.menuid = menuid;
		this.pmenuid = pmenuid;
		this.menuidpath = menuidpath;
		this.isleaf = isleaf;
		this.useyab003 = useyab003;
		this.securitypolicy = securitypolicy;
		this.menuname = menuname;
		this.menulevel = menulevel;
		this.effecttime = effecttime;
		this.auditstate = auditstate;
		this.positionid = positionid;
		this.positionname = positionname;
		this.positionnamepath = positionnamepath;
		this.positiontype = positiontype;
	}

	public MenuPositionVO(Long menuid, String menuname, Long menulevel, Date effecttime, String auditstate,
			Long positionid, String positionname, String positionnamepath, String positiontype) {
		this.menuid = menuid;
		this.menuname = menuname;
		this.menulevel = menulevel;
		this.effecttime = effecttime;
		this.auditstate = auditstate;
		this.positionid = positionid;
		this.positionname = positionname;
		this.positionnamepath = positionnamepath;
		this.positiontype = positiontype;
	}

	public MenuPositionVO(Long menuid, String menuname, Date effecttime, Long pmenuid, String nbsp, String hasdp,
			String dp, String perview, String menuidpath) {
		this.menuid = menuid;
		this.menuname = menuname;
		this.effecttime = effecttime;
		this.pmenuid = pmenuid;
		this.nbsp = nbsp;
		this.hasdp = hasdp;
		this.dp = dp;
		this.perview = perview;
		this.menuidpath = menuidpath;
	}

	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = 31 * result + (auditstate == null ? 0 : auditstate.hashCode());

		result = 31 * result + (dp == null ? 0 : dp.hashCode());
		result = 31 * result + (effecttime == null ? 0 : effecttime.hashCode());

		result = 31 * result + (hasdp == null ? 0 : hasdp.hashCode());
		result = 31 * result + (isleaf == null ? 0 : isleaf.hashCode());
		result = 31 * result + (menuid == null ? 0 : menuid.hashCode());
		result = 31 * result + (menuidpath == null ? 0 : menuidpath.hashCode());

		result = 31 * result + (menulevel == null ? 0 : menulevel.hashCode());

		result = 31 * result + (menuname == null ? 0 : menuname.hashCode());

		result = 31 * result + (nbsp == null ? 0 : nbsp.hashCode());
		result = 31 * result + (perview == null ? 0 : perview.hashCode());
		result = 31 * result + (pmenuid == null ? 0 : pmenuid.hashCode());
		result = 31 * result + (positionid == null ? 0 : positionid.hashCode());

		result = 31 * result + (positionname == null ? 0 : positionname.hashCode());

		result = 31 * result + (positionnamepath == null ? 0 : positionnamepath.hashCode());

		result = 31 * result + (positiontype == null ? 0 : positiontype.hashCode());

		result = 31 * result + (securitypolicy == null ? 0 : securitypolicy.hashCode());

		result = 31 * result + (useyab003 == null ? 0 : useyab003.hashCode());

		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MenuPositionVO other = (MenuPositionVO) obj;
		if (auditstate == null) {
			if (auditstate != null)
				return false;
		} else if (!auditstate.equals(auditstate))
			return false;
		if (dp == null) {
			if (dp != null)
				return false;
		} else if (!dp.equals(dp))
			return false;
		if (effecttime == null) {
			if (effecttime != null)
				return false;
		} else if (!effecttime.equals(effecttime))
			return false;
		if (hasdp == null) {
			if (hasdp != null)
				return false;
		} else if (!hasdp.equals(hasdp))
			return false;
		if (isleaf == null) {
			if (isleaf != null)
				return false;
		} else if (!isleaf.equals(isleaf))
			return false;
		if (menuid == null) {
			if (menuid != null)
				return false;
		} else if (!menuid.equals(menuid))
			return false;
		if (menuidpath == null) {
			if (menuidpath != null)
				return false;
		} else if (!menuidpath.equals(menuidpath))
			return false;
		if (menulevel == null) {
			if (menulevel != null)
				return false;
		} else if (!menulevel.equals(menulevel))
			return false;
		if (menuname == null) {
			if (menuname != null)
				return false;
		} else if (!menuname.equals(menuname))
			return false;
		if (nbsp == null) {
			if (nbsp != null)
				return false;
		} else if (!nbsp.equals(nbsp))
			return false;
		if (perview == null) {
			if (perview != null)
				return false;
		} else if (!perview.equals(perview))
			return false;
		if (pmenuid == null) {
			if (pmenuid != null)
				return false;
		} else if (!pmenuid.equals(pmenuid))
			return false;
		if (positionid == null) {
			if (positionid != null)
				return false;
		} else if (!positionid.equals(positionid))
			return false;
		if (positionname == null) {
			if (positionname != null)
				return false;
		} else if (!positionname.equals(positionname))
			return false;
		if (positionnamepath == null) {
			if (positionnamepath != null)
				return false;
		} else if (!positionnamepath.equals(positionnamepath))
			return false;
		if (positiontype == null) {
			if (positiontype != null)
				return false;
		} else if (!positiontype.equals(positiontype))
			return false;
		if (securitypolicy == null) {
			if (securitypolicy != null)
				return false;
		} else if (!securitypolicy.equals(securitypolicy))
			return false;
		if (useyab003 == null) {
			if (useyab003 != null)
				return false;
		} else if (!useyab003.equals(useyab003))
			return false;
		return true;
	}
}
