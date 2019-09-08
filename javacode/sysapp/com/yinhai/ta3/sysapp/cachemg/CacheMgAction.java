package com.yinhai.ta3.sysapp.cachemg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import com.yinhai.sysframework.cache.ehcache.CacheUtil;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.webframework.BaseAction;

public class CacheMgAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9150087586568252334L;

	public String execute() throws Exception {
		CacheManager cm = ServiceLocator.getService("ehCacheManager", CacheManager.class);
		String[] cacheNames = cm.getCacheNames();
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < cacheNames.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", cacheNames[i]);
			Ehcache cache = cm.getCache(cacheNames[i]);
			map.put("size", cache.getSize() + "");
//			map.put("avgSearchTime", cache.getAverageSearchTime() + "");
//			map.put("avgSearchTime", cache.get + "");
//			map.put("avgGetTime", cache.getAverageGetTime() + "");
			list.add(map);
		}
		setList("cacheList", list);
		System.out.println(list.size());
		return "success";
	}

	@SuppressWarnings("rawtypes")
	public String query() throws Exception {
		CacheManager cm = (CacheManager) ServiceLocator.getService("ehCacheManager");
		Ehcache cache = cm.getCache(getDto().getAsString("cacheName"));
		List list = cache.getKeys();
		List<HashMap<String, String>> elementList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("cacheName", getDto().getAsString("cacheName"));
			map.put("key", list.get(i) + "");
			map.put("value", cache.get(list.get(i)).getObjectValue().toString());
			elementList.add(map);
		}
		setList("cacheElementList", elementList);
		return JSON;
	}

	public String delete() throws Exception {
		CacheUtil.cacheElementRemove(getDto().getAsString("cacheName"), getDto().getAsString("key"));
		setMsg("删除成功");
		return JSON;
	}
}
