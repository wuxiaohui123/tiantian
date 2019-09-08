package com.yinhai.sysframework.cache.ehcache.service;

import javax.jws.WebService;

@WebService
public interface CacheRMIService {

	boolean removeElement(String cacheName, String key);
}
