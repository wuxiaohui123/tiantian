package com.yinhai.ta3.organization.dao;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.ta3.system.org.domain.Org;

public class TestService implements ITestServcie {

	private SimpleDao hibernateDao;

	@Override
	public void createOrg(ParamDTO dto) {

		Org org = (Org) dto.toDomainObject(SysConfig.getSysConfig(Org.class.getName(), Org.class.getName()));
	}

	@Override
	public void updateOrg(ParamDTO paramParamDTO) {
		// TODO Auto-generated method stub

	}

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

}
