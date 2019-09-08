package com.yinhai.ta3.sysapp.configmg.service;

import java.util.List;

import com.yinhai.sysframework.config.IConfigSyspath;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.service.Service;

public interface IConfigSysPathMgService extends Service {

	public static final String SERVICEKEY = "configSysPathMgService";

	public abstract void saveUpdateSyspath(ParamDTO paramParamDTO);

	public abstract void removeSyspath(String paramString);

	@SuppressWarnings("rawtypes")
	public abstract List queryConfigSyspaths();

	public abstract IConfigSyspath getConfigSyspath(String paramString);
}
