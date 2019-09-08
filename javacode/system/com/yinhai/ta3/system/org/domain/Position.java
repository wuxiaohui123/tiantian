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
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class Position extends BaseDomain implements IPosition {

	private static final long serialVersionUID = 7221663281492992484L;
	private Long positionid;
	private Org taorg;
	private String positionname;
	private String positiontype;
	private Long createpositionid;
	private Long createuser;
	private String orgidpath;
	private String orgnamepath;
	private Date validtime;
	private Date createtime;
	private String effective;
	private String isadmin;
	private String isshare;
	private String iscopy;
	private String positioncategory;
	private Set<UserPosition> tauserpositions = new HashSet<UserPosition>(0);
	private Set<PositionAuthrity> tapositionauthrities = new HashSet<PositionAuthrity>(0);

	public Position() {
	}

	public Position(Org taorg) {
		this.taorg = taorg;
	}

	public Position(Org taorg, String positionname, String positiontype, Long createpositionid, String orgidpath,
			String orgnamepath, Date validtime, Long createuser, Date createtime, String effective, String isadmin,
			String isshare, String iscopy, String positioncategory, Set<UserPosition> tauserpositions, Set<PositionAuthrity> tapositionauthrities) {
		this.taorg = taorg;
		this.positionname = positionname;
		this.positiontype = positiontype;
		this.createpositionid = createpositionid;
		this.orgidpath = orgidpath;
		this.orgnamepath = orgnamepath;
		this.validtime = validtime;
		this.createuser = createuser;
		this.createtime = createtime;
		this.effective = effective;
		this.isadmin = isadmin;
		this.isshare = isshare;
		this.iscopy = iscopy;
		this.positioncategory = positioncategory;
		this.tauserpositions = tauserpositions;
		this.tapositionauthrities = tapositionauthrities;
	}

	public Long getPositionid() {
		return positionid;
	}

	public String getIsadmin() {
		return isadmin;
	}

	public void setIsadmin(String isadmin) {
		this.isadmin = isadmin;
	}

	public void setPositionid(Long positionid) {
		this.positionid = positionid;
	}

	public Org getTaorg() {
		return taorg;
	}

	public void setTaorg(Org taorg) {
		this.taorg = taorg;
	}

	public String getPositionname() {
		return positionname;
	}

	public void setPositionname(String positionname) {
		this.positionname = positionname;
	}

	public String getPositiontype() {
		return positiontype;
	}

	public void setPositiontype(String positiontype) {
		this.positiontype = positiontype;
	}

	public Long getCreateuser() {
		return createuser;
	}

	public void setCreateuser(Long createuser) {
		this.createuser = createuser;
	}

	public Long getCreatepositionid() {
		return createpositionid;
	}

	public void setCreatepositionid(Long createpositionid) {
		this.createpositionid = createpositionid;
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

	public Date getValidtime() {
		return validtime;
	}

	public void setValidtime(Date validtime) {
		this.validtime = validtime;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getEffective() {
		return effective;
	}

	public void setEffective(String effective) {
		this.effective = effective;
	}

	public String getIsshare() {
		return isshare;
	}

	public void setIsshare(String isshare) {
		this.isshare = isshare;
	}

	public String getIscopy() {
		return iscopy;
	}

	public void setIscopy(String iscopy) {
		this.iscopy = iscopy;
	}

	public String getPositioncategory() {
		return positioncategory;
	}

	public void setPositioncategory(String positioncategory) {
		this.positioncategory = positioncategory;
	}

	@XmlTransient
	@WebMethod(exclude = true)
	public Set<UserPosition> getTauserpositions() {
		return tauserpositions;
	}

	public void setTauserpositions(Set<UserPosition> tauserpositions) {
		this.tauserpositions = tauserpositions;
	}

	@XmlTransient
	@WebMethod(exclude = true)
	public Set<PositionAuthrity> getTapositionauthrities() {
		return tapositionauthrities;
	}

	public void setTapositionauthrities(Set<PositionAuthrity> tapositionauthrities) {
		this.tapositionauthrities = tapositionauthrities;
	}

	public boolean isDelegatesPosition() {
		return "3".equals(getPositiontype());
	}

	public boolean isPerson() {
		return "2".equals(getPositiontype());
	}

	public boolean isPublicPosition() {
		return "1".equals(getPositiontype());
	}

	public Long getOrgid() {
		return taorg.getOrgid();
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("positionid", getPositionid());
		map.put("orgid", getOrgid());
		map.put("positionname", getPositionname());
		map.put("positiontype", getPositiontype());
		map.put("orgidpath", getOrgidpath());
		map.put("orgnamepath", getOrgnamepath());
		map.put("validtime", getValidtime());
		map.put("createuser", getCreateuser());
		map.put("createtime", getCreatetime());
		map.put("effective", getEffective());
		map.put("isadmin", getIsadmin());
		map.put("isshare", getIsshare());
		map.put("iscopy", getIscopy());
		map.put("positioncategory", getPositioncategory());
		return map;
	}

	@SuppressWarnings("unchecked")
	public Key getPK() {
		Key key = new Key();
		key.put("positionid", getPositionid());
		return key;
	}
}
