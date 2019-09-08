package com.yinhai.sysframework.cache.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

public class CacheBean extends BeanNameAutoProxyCreator implements InitializingBean {

	private static final long serialVersionUID = -1278980342433370910L;
	private String cachePrefix;
	private String[] lapsePrefix;
	private String[] beanFilterNames;
	private String filter;

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getCachePrefix() {
		return cachePrefix;
	}

	public void setCachePrefix(String cachePrefix) {
		this.cachePrefix = cachePrefix;
	}

	public String[] getLapsePrefix() {
		return lapsePrefix;
	}

	public void setLapsePrefix(String[] lapsePrefix) {
		this.lapsePrefix = lapsePrefix;
	}

	public String[] getBeanFilterNames() {
		return beanFilterNames;
	}

	public void setBeanFilterNames(String[] beanFilterNames) {
		this.beanFilterNames = beanFilterNames;
	}

	public void afterPropertiesSet() throws Exception {
		ConfigurableBeanFactory cbf = (getBeanFactory() instanceof ConfigurableBeanFactory) ? (ConfigurableBeanFactory) getBeanFactory()
				: null;

		CacheInterceptor interceptor = new CacheInterceptor();
		interceptor.setLapsePrefix(lapsePrefix);
		interceptor.setCachePrefix(cachePrefix);
		interceptor.setFilter(filter);

		cbf.registerSingleton("__cacheInterceptor", interceptor);
		setInterceptorNames("__cacheInterceptor");

		if (beanFilterNames != null) {
			List<String> bnames = new ArrayList<String>(Arrays.asList(beanFilterNames));
			bnames.add("*Service");
			setBeanNames(bnames.toArray(new String[bnames.size()]));
		} else {
			setBeanNames("*Service");
		}
	}
}
