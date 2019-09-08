package com.yinhai.sysframework.cache.ehcache.listener;

import java.util.Properties;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

public class TaCacheEventListenerFactory extends CacheEventListenerFactory {

	public CacheEventListener createCacheEventListener(Properties properties) {
		return new TaCacheEventListener();
	}

	public static void main(String[] args) {
		String fileName = "F:\\Ta3.01\\trunk\\04.Implement\\01.ta\\javacode\\config\\ehcache.xml";
		CacheManager manager = CacheManager.create(fileName);

		String[] names = manager.getCacheNames();

		Cache cache = manager.getCache("appCodeCache");

		Element element2 = new Element("key1", "values1");
		cache.put(element2);
		Element element = cache.get("key1");

		Object obj = element.getObjectValue();
		Element element3 = new Element("key1", "values2");
		cache.put(element3);

		cache.remove("key1");
		manager.shutdown();
	}

}
