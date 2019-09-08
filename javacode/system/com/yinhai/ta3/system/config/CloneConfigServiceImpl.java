package com.yinhai.ta3.system.config;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.service.WsBaseService;
import com.yinhai.ta3.system.config.domain.ConfigSyspath;

@WebService
public class CloneConfigServiceImpl extends WsBaseService implements ICloneConfigService {

	private SimpleDao hibernateDao;

	@WebMethod(exclude = true)
	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	@SuppressWarnings("unchecked")
	public List<ConfigSyspath> getConfigSyspaths() {
		return hibernateDao.createQuery("from ConfigSyspath").list();
	}

	public ConfigSyspath getConfigSyspath(String id) {
		return (ConfigSyspath) hibernateDao.createQuery("from ConfigSyspath cs where cs.id=?", new Object[] { id })
				.uniqueResult();
	}

	public SimpleDao getHibernateDao() {
		return hibernateDao;
	}
	
	
}
