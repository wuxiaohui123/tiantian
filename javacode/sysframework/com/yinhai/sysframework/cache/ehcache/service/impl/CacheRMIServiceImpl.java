package com.yinhai.sysframework.cache.ehcache.service.impl;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import com.yinhai.sysframework.cache.ehcache.service.CacheRMIService;
import com.yinhai.sysframework.service.ServiceLocator;

public class CacheRMIServiceImpl implements CacheRMIService {

	@Resource
	private WebServiceContext wsContext;

	@Override
	public boolean removeElement(String cacheName, String key) {
		CacheManager manager = (CacheManager) ServiceLocator.getService("ehCacheManager");
		Cache cache = manager.getCache("appCodeCache");
		boolean ret = cache.removeQuiet(key);
		return ret;
	}

}
