package com.yinhai.sysframework.transaction;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class TransactionNamespaceSupport extends NamespaceHandlerSupport {

	@Override
	public void init() {
		
		 registerBeanDefinitionParser("transaction-driven", new TransactionParser());

	}

}
