package com.yinhai.sysframework.transaction;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TransactionParser extends AbstractSingleBeanDefinitionParser {

	protected Class<?> getBeanClass(Element element) {
		return TransactionBean.class;
	}

	protected void doParse(Element element, BeanDefinitionBuilder bean) {
		element.setAttribute("id", "__ta_id__");
		addProps(element, bean);
		addBeannames(element, bean);
	}

	private void addBeannames(Element element, BeanDefinitionBuilder bean) {
		if (element.getAttribute("beanNames") != null) {
			bean.addPropertyValue("beanFilterNames", element.getAttribute("beanNames").split(","));
		}
	}

	private void addProps(Element element, BeanDefinitionBuilder bean) {
		Map<String, String> props = new HashMap<String, String>();
		NodeList list = element.getChildNodes();
		if (list != null) {
			for (int i = 0; i < list.getLength(); i++) {
				if (list.item(i).getNodeType() == 1) {
					Element e = (Element) list.item(i);
					NodeList nodes = e.getChildNodes();
					for (int j = 0; j < nodes.getLength(); j++) {
						if (nodes.item(j).getNodeType() == 1) {
							Element e1 = (Element) nodes.item(j);
							props.put(e1.getAttribute("key"), e1.getTextContent());
						}
					}
				}
			}
			bean.addPropertyValue("properties", props);
		}
	}
}
