package com.yinhai.sysframework.cache.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class CacheNamespaceSupport extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("cache-support", new CacheBeanParser());
		registerBeanDefinitionParser("bean", new ProxyCacheBeanParser());

	}

}
