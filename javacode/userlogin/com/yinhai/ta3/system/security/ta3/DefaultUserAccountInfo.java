package com.yinhai.ta3.system.security.ta3;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.security.ta3.IRoleAuthrity;
import com.yinhai.sysframework.security.ta3.IUserAccountInfo;
import com.yinhai.ta3.system.org.domain.User;

public class DefaultUserAccountInfo implements IUserAccountInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4568363935018162264L;
	private IUser user;
	private Set<IRoleAuthrity> roleAuthoritys;

	public DefaultUserAccountInfo(IUser user) {
		this.user = user;
	}

	public Collection<IRoleAuthrity> getRoles() {
		return roleAuthoritys;
	}

	public IUser getUser() {
		return user;
	}

	public String getLoginId() {
		return user.getLoginid();
	}

	public boolean isAccountNonExpired() {
		return "0".equals(((User) user).getEffective());
	}

	public boolean isAccountNonLocked() {
		return !((User) user).isLock();
	}

	public boolean isEnabled() {
		return "0".equals(((User) user).getEffective());
	}

	public Set<IRoleAuthrity> getRoleAuthoritys() {
		return roleAuthoritys;
	}

	public void setRoleAuthoritys(Set<IRoleAuthrity> roleAuthoritys) {
		this.roleAuthoritys = roleAuthoritys;
	}

	public void setUser(IUser user) {
		this.user = user;
	}

	public String getPassword() {
		return ((User) user).getPassword();
	}

	public boolean isAccountFirstLogin() {
		return ((User) user).getPwdlastmodifydate() == null;
	}

	public Date lastModifyDate() {
		return ((User) user).getPwdlastmodifydate() == null ? null : ((User) user).getPwdlastmodifydate();
	}

	public int getPasswordfaultnum() {
		return (((User) user).getPasswordfaultnum() == null ? null : ((User) user).getPasswordfaultnum()).intValue();
	}

	public Date getCreateUserDate() {
		return ((User) user).getCreatetime();
	}
}
