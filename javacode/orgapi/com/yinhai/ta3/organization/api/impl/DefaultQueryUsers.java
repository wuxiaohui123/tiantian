package com.yinhai.ta3.organization.api.impl;

import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.ta3.organization.api.IQueryUsers;
import com.yinhai.ta3.system.org.domain.User;

public class DefaultQueryUsers implements IQueryUsers {

	private SimpleDao hibernateDao;
	public static DefaultQueryUsers instance = new DefaultQueryUsers();

	public PageBean queryUsers(User user, Long orgid, Long positionid, boolean isDisSubOrgs, int start, int limit) {
		StringBuffer sb = new StringBuffer();
		sb.append("select u from User u,UserPosition up, where 1=1").append(" and u.loginid=?").append(" and u.islock=?").append(" and u.sex=?")
				.append(" and u.name like ?").append(" and u.effective=?");

		hibernateDao.createQuery(sb.toString()).setMaxResults(limit).setFirstResult(start);
		return null;
	}

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}
}
