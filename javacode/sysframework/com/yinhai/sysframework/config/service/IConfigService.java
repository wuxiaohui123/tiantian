package com.yinhai.sysframework.config.service;

import java.util.List;

import com.yinhai.sysframework.config.IConfigSyspath;

public interface IConfigService {

	 String SERVICEKEY = "configService";

	 List<IConfigSyspath> getConfigSysPaths();

	 IConfigSyspath getConfigSysPath(String paramString);
}
