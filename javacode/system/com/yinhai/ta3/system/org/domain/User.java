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
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;

public class User extends BaseDomain implements IUser {

	private Long userid;
	private String name;
	private String sex;
	private String loginid;
	private String password;
	private Integer passwordfaultnum;
	private Date pwdlastmodifydate;
	private String islock;
	private Integer sort;
	private String effective;
	private Integer age;
	private Date birth;
	private String job;
	private String tel;
	private String officetel;
	private String qq;
	private String email;
	private String weixin;
	private String weibo;
	private String address;
	private Long createuser;
	private Date createtime;
	private Long directorgid;
	private String destory;
	private Set<UserPosition> tauserpositions = new HashSet<UserPosition>(0);

	private String yab003;

	private IPosition nowPosition;

	private String departId;

	private String yab139;

	public User() {
	}

	public User(String name, String sex, Integer age,Date birth,String job,String officetel,String qq,String email,String weixin,
			String loginid, String password, Integer passwordfaultnum,String weibo,String address,
			Date pwdlastmodifydate, String islock, Integer sort, String effective, String tel, Long createuser,
			Date createtime, Long directorgid, String destory, Set<UserPosition> tauserpositions) {
		this.name = name;
		this.sex = sex;
		this.loginid = loginid;
		this.password = password;
		this.passwordfaultnum = passwordfaultnum;
		this.pwdlastmodifydate = pwdlastmodifydate;
		this.islock = islock;
		this.sort = sort;
		this.effective = effective;
		this.age = age;
		this.birth = birth;
		this.job = job;
		this.tel = tel;
		this.officetel = officetel;
		this.email = email;
		this.qq = qq;
		this.weixin = weixin;
		this.weibo = weibo;
		this.address = address;
		this.createuser = createuser;
		this.createtime = createtime;
		this.directorgid = directorgid;
		this.destory = destory;
		this.tauserpositions = tauserpositions;
	}

	public static String getCurrentClassName() {
		return SysConfig.getSysConfig(User.class.getName(), User.class.getName());
	}

	public Key getPK() {
		Key key = new Key();
		if (getUserid() == null) {
			throw new IllegalArgumentException("主键userid不能为空。");
		}
		key.put("userid", getUserid());
		return key;
	}

	public Map toMap() {
		Map map = new HashMap();
		map.put("userid", getUserid());
		map.put("name", getName());
		map.put("sex", getSex());
		map.put("loginid", getLoginid());
		map.put("passwordfaultnum", getPasswordfaultnum());
		map.put("pwdlastmodifydate", getPwdlastmodifydate());
		map.put("islock", getIslock());
		map.put("sort", getSort());
		map.put("effective", getEffective());
		map.put("age", getAge());
		map.put("birth", getBirth());
		map.put("job", getJob());
		map.put("officetel", getOfficetel());
		map.put("qq", getQq());
		map.put("weixin", getWeixin());
		map.put("weibo", getWeibo());
		map.put("address", getAddress());
		map.put("tel", getTel());
		map.put("createuser", getCreateuser());
		map.put("createtime", getCreatetime());
		map.put("directorgid", getDirectorgid());
		map.put("destory", getDestory());
		return map;
	}

	public String getDestory() {
		return destory;
	}

	public void setDestory(String destory) {
		this.destory = destory;
	}

	public Long getDirectorgid() {
		return directorgid;
	}

	public void setDirectorgid(Long directorgid) {
		this.directorgid = directorgid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getUserId() {
		return String.valueOf(userid);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getOfficetel() {
		return officetel;
	}

	public void setOfficetel(String officetel) {
		this.officetel = officetel;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWeixin() {
		return weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public String getWeibo() {
		return weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getLoginid() {
		return loginid;
	}

	public String getLoginId() {
		return loginid;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getPasswordfaultnum() {
		return passwordfaultnum;
	}

	public void setPasswordfaultnum(Integer passwordfaultnum) {
		this.passwordfaultnum = passwordfaultnum;
	}

	public Date getPwdlastmodifydate() {
		return pwdlastmodifydate;
	}

	public void setPwdlastmodifydate(Date pwdlastmodifydate) {
		this.pwdlastmodifydate = pwdlastmodifydate;
	}

	public String getIslock() {
		return islock;
	}

	public void setIslock(String islock) {
		this.islock = islock;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getEffective() {
		return effective;
	}

	public void setEffective(String effective) {
		this.effective = effective;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
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
	public Set<UserPosition> getTauserpositions() {
		return tauserpositions;
	}

	public void setTauserpositions(Set<UserPosition> tauserpositions) {
		this.tauserpositions = tauserpositions;
	}

	public boolean isLock() {
		return "1".equals(getIslock());
	}

	public void setNowPosition(IPosition nowPosition) {
		this.nowPosition = nowPosition;
	}

	public Position getNowPosition() {
		return (Position) nowPosition;
	}

	public void setDepartId(String departId) {
		this.departId = departId;
	}

	public String getDepartId() {
		return departId;
	}

	public String getYab003() {
		return yab003;
	}

	@Deprecated
	public String getOrgId() {
		return yab003;
	}

	public void setYab003(String yab003) {
		this.yab003 = yab003;
	}

	public String getYab139() {
		return yab139;
	}

	public void setYab139(String yab139) {
		this.yab139 = yab139;
	}

}
