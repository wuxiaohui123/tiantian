package com.yinhai.ta3.system.security.ta3;

import com.yinhai.sysframework.security.ta3.IFailLoginCheckUser;
import com.yinhai.ta3.system.org.dao.UserDao;

public class FailLoginCheckUser implements IFailLoginCheckUser {

	private UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public void setUserLocked(Long userid) {
		userDao.lockUser(userid);
	}

	@Override
	public void updateUserFaultNum(Long userId, int num) {
		userDao.updateUserFaultNum(userId, num);
	}
}
