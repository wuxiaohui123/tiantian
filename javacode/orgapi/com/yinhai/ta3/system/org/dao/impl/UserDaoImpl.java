package com.yinhai.ta3.system.org.dao.impl;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.BaseDao;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.ta3.system.org.dao.UserDao;
import com.yinhai.ta3.system.org.domain.User;

public class UserDaoImpl extends BaseDao<IUser, Long> implements UserDao {

	private String getEntityClass(String userClassName) {
		return SysConfig.getSysConfig(userClassName, userClassName);
	}

	protected Class<IUser> getEntityClass() {
		return IUser.class;
	}

	public IUser getUserByLoginId(String loginId) {
		return (IUser) createQuery("from " + getEntityClass(User.class.getName()) + " u where u.loginid=? and (u.destory=? or u.destory is null)", loginId, "1" ).uniqueResult();
	}

	public IUser getUser(Long userId) {
		return (IUser) super.findUniqueByProperty("userid", userId);
	}

	public void lockUser(Long userid) {
		super.createQuery("update " + getEntityClass(User.class.getName()) + "  set islock=? where userid=?", "1", userid ).executeUpdate();
	}

	public int updateUserFaultNum(Long userid, Integer num) {
		return super.createQuery("update " + getEntityClass(User.class.getName()) + " set passwordfaultnum=? where userid=?", num, userid).executeUpdate();
	}
}
