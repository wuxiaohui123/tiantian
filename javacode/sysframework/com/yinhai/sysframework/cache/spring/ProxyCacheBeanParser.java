package com.yinhai.sysframework.cache.spring;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class ProxyCacheBeanParser extends AbstractSingleBeanDefinitionParser {

	private static final String CACHEPREFIX = "cachePrefix";
	private static final String LAPSEPREFIX = "lapsePrefix";
	private static final String TARGET = "target";

	protected Class<CacheProxyBean> getBeanClass(Element element) {
		return CacheProxyBean.class;
	}

	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		element.setAttribute("id", "__cache_bean_id__");
		String cachePrefix = element.getAttribute(CACHEPREFIX);
		String targetName = element.getAttribute(TARGET);
		String lapsePrefix = element.getAttribute(LAPSEPREFIX);

		CacheInterceptor interceptor = new CacheInterceptor();
		interceptor.setCachePrefix(cachePrefix);
		interceptor.setLapsePrefix(lapsePrefix == null ? null : lapsePrefix.split(","));

		builder.addPropertyValue("tname", targetName);
		builder.addPropertyValue("interceptor", interceptor);
	}
}
