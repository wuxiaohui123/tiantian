package com.yinhai.sysframework.cache.ehcache.dao;

import java.util.List;

import com.yinhai.sysframework.cache.ehcache.ServerAddress;
import com.yinhai.sysframework.cache.ehcache.domain.ServeraddressDomain;

public interface ServerAddressDao {

	List<ServeraddressDomain> getList(ServerAddress serverAddress);

	void insert(ServeraddressDomain serveraddressDomain);

	ServerAddress getServerAddress(String address);

	List<String> getAllUsefulServerAddress();

	int delete(ServerAddress serverAddress);

	int update(ServerAddress serverAddress);
}
