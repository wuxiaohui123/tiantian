package com.yinhai.ta3.sysapp.configmg.service.impl;

import java.util.List;

import com.yinhai.sysframework.config.IConfigSyspath;
import com.yinhai.sysframework.config.service.IConfigService;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.service.BaseService;
import com.yinhai.sysframework.util.PinyinUtil;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.sysapp.configmg.service.IConfigSysPathMgService;
import com.yinhai.ta3.system.config.domain.ConfigSyspath;

public class ConfigSysPathMgServiceImpl extends BaseService implements IConfigSysPathMgService {

	private SimpleDao hibernateDao;
	private IConfigService configService;

	public void setConfigService(IConfigService configService) {
		this.configService = configService;
	}

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public void saveUpdateSyspath(ParamDTO dto) {
		String id = dto.getAsString("id");
		String name = dto.getAsString("name");
		String sysipaddress = dto.getAsString("sysipaddress");
		String sysport = dto.getAsString("sysport");
		String contextroot = dto.getAsString("contextroot");
		String url = "http://" + sysipaddress + ":" + sysport + "/" + contextroot + "/";
		String py = PinyinUtil.converterToFirstSpell(name, false);
		String iscur = dto.getAsString("iscur");
		ConfigSyspath configSyspath = (ConfigSyspath) hibernateDao.createQuery("from ConfigSyspath cs where cs.id=?", new Object[] { id })
				.uniqueResult();
		if (ValidateUtil.isEmpty(configSyspath)) {
			configSyspath = new ConfigSyspath(id, name, url, py, iscur);
			hibernateDao.save(configSyspath);
		} else {
			configSyspath.setName(name);
			configSyspath.setPy(py);
			configSyspath.setUrl(url);
			configSyspath.setCursystem(iscur);
			hibernateDao.update(configSyspath);
		}
	}

	public void removeSyspath(String id) {
		hibernateDao.createQuery("delete from ConfigSyspath cs where cs.id=?", new Object[] { id }).executeUpdate();
	}

	@SuppressWarnings("rawtypes")
	public List queryConfigSyspaths() {
		return configService.getConfigSysPaths();
	}

	public IConfigSyspath getConfigSyspath(String id) {
		return configService.getConfigSysPath(id);
	}
}
