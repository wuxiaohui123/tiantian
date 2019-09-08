package com.yinhai.sysframework.cache.spring;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import com.yinhai.sysframework.util.StringUtil;

public class CacheBeanParser extends AbstractSingleBeanDefinitionParser {

	private static final String CACHEPREFIX = "cachePrefix";
	private static final String LAPSEPREFIX = "lapsePrefix";
	private static final String BEANFILTERNAMES = "beanFilterNames";
	private static final String FILTER = "filter";

	protected Class<CacheBean> getBeanClass(Element element) {
		return CacheBean.class;
	}

	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		element.setAttribute("id", "__ta_cache__");
		String cachePrefix = element.getAttribute(CACHEPREFIX);
		String lapsePrefix = element.getAttribute(LAPSEPREFIX);
		String beanFilterNames = element.getAttribute(BEANFILTERNAMES);
		String filter = element.getAttribute(FILTER);
		builder.addPropertyValue(CACHEPREFIX, StringUtil.isBlank(cachePrefix) ? "get" : cachePrefix);
		builder.addPropertyValue(LAPSEPREFIX, StringUtil.isBlank(lapsePrefix) ? new String[] { "insert", "update",
				"delete", "save" } : lapsePrefix.split(","));
		builder.addPropertyValue(BEANFILTERNAMES, beanFilterNames);
		builder.addPropertyValue(FILTER, filter);
	}
}
