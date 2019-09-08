package com.yinhai.ta3.system.config;

import java.util.List;

import javax.jws.WebService;

import com.yinhai.sysframework.service.WsService;
import com.yinhai.ta3.system.config.domain.ConfigSyspath;

@WebService
public interface ICloneConfigService extends WsService {

	public static final String SERVICEKEY = "cloneConfigService";

	public abstract List<ConfigSyspath> getConfigSyspaths();

	public abstract ConfigSyspath getConfigSyspath(String paramString);
}
