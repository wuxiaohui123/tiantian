package com.yinhai.ta3.organization.api.impl;

import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.ta3.organization.api.IQueryUsers;
import com.yinhai.ta3.system.org.domain.User;

public class MyQueryUsers implements IQueryUsers {

	private SimpleDao hibernateDao;
	public static MyQueryUsers instance = new MyQueryUsers();

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public PageBean queryUsers(User user, Long orgid, Long positionid, boolean isDisSubOrgs, int start, int limit) {
		return null;
	}

}
