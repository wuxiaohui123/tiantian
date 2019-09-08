package com.yinhai.ta3.system.org.dao.impl;

import com.yinhai.sysframework.dao.hibernate.BaseDao;
import com.yinhai.ta3.system.org.dao.OrgDao;
import com.yinhai.ta3.system.org.domain.Org;

public class OrgDaoImpl extends BaseDao<Org, Long> implements OrgDao {

	protected Class<Org> getEntityClass() {
		return Org.class;
	}

	public Org getOrg(Long orgid) {
		return (Org) super.findUniqueByProperty("orgid", orgid);
	}
}
