package com.yinhai.ta3.system.config;

import java.util.ArrayList;
import java.util.List;

import com.yinhai.sysframework.config.IConfigSyspath;
import com.yinhai.sysframework.config.service.IConfigService;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.ta3.system.config.domain.ConfigSyspath;

public class ConfigServiceImpl extends SimpleDao implements IConfigService {

	private ICloneConfigService cloneConfigService;

	public void setCloneConfigService(ICloneConfigService cloneConfigService) {
		this.cloneConfigService = cloneConfigService;
	}

	public List<IConfigSyspath> getConfigSysPaths() {
		List<ConfigSyspath> list = cloneConfigService.getConfigSyspaths();
		List<IConfigSyspath> list1 = new ArrayList<IConfigSyspath>();
		for (ConfigSyspath configSyspath : list) {
			list1.add(configSyspath);
		}
		return list1;
	}

	public IConfigSyspath getConfigSysPath(String id) {
		return cloneConfigService.getConfigSyspath(id);
	}

	public ICloneConfigService getCloneConfigService() {
		return cloneConfigService;
	}
	
	
}
