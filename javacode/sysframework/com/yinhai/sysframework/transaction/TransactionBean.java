package com.yinhai.sysframework.transaction;

import java.util.Map;

import org.springframework.aop.TargetSource;
import org.springframework.beans.BeansException;

public class TransactionBean {

	private Map<String, String> properties;
	private String[] beanFilterNames;
	private String transactionManager;

	public void setTransactionManager(String transactionManager) {
		this.transactionManager = transactionManager;
	}

	public String getTransactionManager() {
		return transactionManager;
	}

	public String[] getBeanFilterNames() {
		return beanFilterNames;
	}

	public void setBeanFilterNames(String[] beanNames) {
		beanFilterNames = beanNames;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	protected Object[] getAdvicesAndAdvisorsForBean(Class<?> arg0, String arg1, TargetSource arg2)
			throws BeansException {
		return null;
	}

	public void afterPropertiesSet() throws Exception {
	}
}
