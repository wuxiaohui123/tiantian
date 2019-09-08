package com.yinhai.ta3.sysapp.consolemg.service;

import java.util.List;
import java.util.Set;

import javax.jws.WebService;

import com.yinhai.sysframework.service.WsService;
import com.yinhai.ta3.sysapp.consolemg.domain.ConsoleModule;

@WebService
public interface PortalService extends WsService {

	List<ConsoleModule> getUserModuleList(Long paramLong, Set<String> paramSet);

	void saveLocationInfo(String paramString1, String paramString2, Long paramLong);

	String getLocationInfo(String paramString, Long paramLong);

}
