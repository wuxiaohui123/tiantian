package com.yinhai.sysframework.cache.spring;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;

public class CacheProxyBean extends ProxyFactoryBean implements InitializingBean {

	private Object interceptor;
	private String tname;
	private BeanFactory beanFactory;
	private static final long serialVersionUID = -3700109044261100162L;

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	public Object getInterceptor() {
		return interceptor;
	}

	public void setInterceptor(Object interceptor) {
		this.interceptor = interceptor;
	}

	public void afterPropertiesSet() throws Exception {
		ConfigurableBeanFactory cbf = (beanFactory instanceof ConfigurableBeanFactory) ? (ConfigurableBeanFactory) beanFactory
				: null;

		if (cbf != null) {
			cbf.registerSingleton(tname + "_cache", interceptor);
			setTarget(new RuntimeBeanReference(tname));
			setInterceptorNames(tname + "_cache");
			cbf.registerSingleton(tname, this);
		}
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
