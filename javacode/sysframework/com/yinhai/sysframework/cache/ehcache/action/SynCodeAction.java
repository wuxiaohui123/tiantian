package com.yinhai.sysframework.cache.ehcache.action;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.webframework.BaseAction;

public class SynCodeAction extends BaseAction {

	public String execute() throws Exception {
		String key = request.getParameter("key");
		String cacheName = request.getParameter("cacheName");
		if ((ValidateUtil.isEmpty(key)) || (ValidateUtil.isEmpty(cacheName))) {
			writeFailure();
			return null;
		}
		CacheManager manager = (CacheManager) ServiceLocator.getService("ehCacheManager");
		Cache cache = manager.getCache(cacheName);
		Element e = cache.getQuiet(key);
		if (ValidateUtil.isEmpty(e)) {
			writeSuccess();
		} else {
			boolean ret = cache.removeQuiet(key);
			if (ret) {
				writeSuccess();
			} else
				writeFailure();
		}
		return null;
	}
}
