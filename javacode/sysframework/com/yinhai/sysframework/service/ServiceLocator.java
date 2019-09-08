package com.yinhai.sysframework.service;

import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ServiceLocator implements ApplicationContextAware {

	private static ApplicationContext appContext;

	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {

		appContext = ac;

	}

	public static Object getService(String key) {
		Object ret = null;
		try {
			Class<?> c = Class.forName("com.yinhai.plugin.core.PluginClassLoader");
			if (Thread.currentThread().getContextClassLoader().getClass() == c) {
				Object pcloader = Thread.currentThread().getContextClassLoader();
				Method getPlugin = c.getDeclaredMethod("getPlugin", new Class[0]);
				Object plugin = getPlugin.invoke(pcloader, new Object[0]);
				Class<?> ta = Class.forName("com.yinhai.plugin.core.ta.TaPlugin");
				if (plugin.getClass() == ta) {
					Method getBean = ta.getDeclaredMethod("getBean", new Class[] { String.class });
					ret = getBean.invoke(plugin, new Object[] { key });
				}
			} else {
				ret = appContext.getBean(key);
			}
		} catch (Exception e) {
			return appContext.getBean(key);
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getService(String key, Class<T> requiredType){
		T ret = null;
		try {
			Class<?> c = Class.forName("com.yinhai.plugin.core.PluginClassLoader");
			if (Thread.currentThread().getContextClassLoader().getClass() == c) {
				Object pcloader = Thread.currentThread().getContextClassLoader();
				Method getPlugin = c.getDeclaredMethod("getPlugin", new Class[0]);
				Object plugin = getPlugin.invoke(pcloader, new Object[0]);
				Class<?> ta = Class.forName("com.yinhai.plugin.core.ta.TaPlugin");
				if (plugin.getClass() == ta) {
					Method getBean = ta.getDeclaredMethod("getBean", new Class[] { String.class });
					ret = (T) getBean.invoke(plugin, new Object[] { key });
				}
			} else {
				ret = (T) appContext.getBean(key, requiredType);
			}
		} catch (Exception e) {
			return (T) appContext.getBean(key, requiredType);
		}
		return ret;
	}

	public static boolean containsBean(String key) {
		boolean containsBean = false;
		try {
			Class<?> c = Class.forName("com.yinhai.plugin.core.PluginClassLoader");
			if (Thread.currentThread().getContextClassLoader().getClass() == c) {
				Object pcloader = Thread.currentThread().getContextClassLoader();
				Method getPlugin = c.getDeclaredMethod("getPlugin", new Class[0]);
				Object plugin = getPlugin.invoke(pcloader, new Object[0]);
				Class<?> ta = Class.forName("com.yinhai.plugin.core.ta.TaPlugin");
				if (plugin.getClass() == ta) {
					Method getBean = ta.getDeclaredMethod("containsBean", new Class[] { String.class });
					containsBean = ((Boolean) getBean.invoke(plugin, new Object[] { key })).booleanValue();
				}
			} else {
				containsBean = appContext.containsBean(key);
			}
		} catch (Exception e) {
			return appContext.containsBean(key);
		}
		return containsBean;
	}

	public static ApplicationContext getAppContext() {
		return appContext;
	}
}
