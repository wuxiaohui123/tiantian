package com.yinhai.sysframework.cache.ehcache.service;

import java.util.List;

import com.yinhai.sysframework.cache.ehcache.ServerAddress;
import com.yinhai.sysframework.cache.ehcache.domain.ServeraddressDomain;
import com.yinhai.sysframework.dto.ParamDTO;

public interface ServerAddressService {

	List<String> getAllUsefulServerAddress();

	List<ServeraddressDomain> query(ParamDTO paramParamDTO);

	ServerAddress getServerAddress(String paramString);

	void addServerAddress(ServeraddressDomain paramServeraddressDomain);

	void updateServerAddress(ServerAddress paramServerAddress);

	int removeServerAddress(ServerAddress paramServerAddress);
}
