package com.yinhai.ta3.system.sysapp;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;

import com.yinhai.sysframework.cache.ehcache.CacheUtil;
import com.yinhai.sysframework.cache.ehcache.ServerAddress;
import com.yinhai.sysframework.cache.ehcache.dao.ServerAddressDao;
import com.yinhai.sysframework.cache.ehcache.domain.ServeraddressDomain;
import com.yinhai.sysframework.cache.ehcache.service.ServerAddressService;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.service.BaseService;

public class ServerAddressServiceImpl extends BaseService implements ServerAddressService {

	public static final String CACHEKEY = "com.yinhai.ta3.system.sysapp.ServerAddressServiceImpl.getALlUserfullServerAddress";
	private ServerAddressDao serverAddressDao;

	public ServerAddressDao getServerAddressDao() {
		return serverAddressDao;
	}

	public void setServerAddressDao(ServerAddressDao serverAddressDao) {
		this.serverAddressDao = serverAddressDao;
	}

	@Cacheable(value = "serverAddressCache")
	public List<String> getAllUsefulServerAddress() {
		return serverAddressDao.getAllUsefulServerAddress();
	}

	public void addServerAddress(ServeraddressDomain serverAddress) {
		serverAddressDao.insert(serverAddress);
		CacheUtil.cacheElementRemove("serverAddressCache", CACHEKEY);
	}

	public ServerAddress getServerAddress(String address) {
		return serverAddressDao.getServerAddress(address);
	}

	public List<ServeraddressDomain> query(ParamDTO dto) {
		ServeraddressDomain server = (ServeraddressDomain) dto.toDomainObject(ServeraddressDomain.class);

		return serverAddressDao.getList(server);
	}

	public int removeServerAddress(ServerAddress serverAddress) {
		int i = serverAddressDao.delete(serverAddress);
		CacheUtil.cacheElementRemove("serverAddressCache", CACHEKEY);
		if (1 != i) {
			throw new AppException("删除错误，请重试");
		}
		return i;
	}

	public void updateServerAddress(ServerAddress serverAddress) {
		int count = serverAddressDao.update(serverAddress);
		if (count != 1) {
			throw new AppException("更新服务器地址失败，请刷新重试！");
		}
		CacheUtil.cacheElementRemove("serverAddressCache", CACHEKEY);
	}
}
