package com.yinhai.ta3.system.i118n;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.core.io.ClassPathResource;



public class TextConfig extends PropertyResourceConfigurer {

	private static Log logger = LogFactory.getLog(TextConfig.class);

	private static Properties properties;

	public static String getText(String key) {
		return properties.getProperty(key);
	}

	public static String getText(String key, boolean isSuperposition, String... args) {
		String tmp = properties.getProperty(key);
		if (isSuperposition) {
			tmp = tmp == null ? "" : tmp;
			for (String arg : args) {
				tmp = tmp + arg;
			}
			return tmp;
		}
		return MessageFormat.format(tmp, args);
	}

	public static String getText(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public static String getText(String key, String defaultValue, boolean isSuperposition, String... args) {
		String tmp = getText(key, defaultValue);
		if (isSuperposition) {
			tmp = tmp == null ? "" : tmp;
			for (String arg : args) {
				tmp = tmp + arg;
			}
			return tmp;
		}
		return MessageFormat.format(tmp, args);
	}

	public void reloadProperties(String fileName) {
		synchronized (properties) {
			if (properties != null) {
				properties.clear();
			} else {
				properties = new Properties();
			}
			InputStream is = null;
			try {
				logger.info("重新加载i118n国际化资�?..");
				ClassPathResource classPathResource = new ClassPathResource(fileName);

				is = new BufferedInputStream(classPathResource.getInputStream());
				properties.load(is);
				logger.info("加载i118n国际化资源成功！");

				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				}
			}
		}
	}

	public static void appendProperties(Properties paramProperties) {
		if (properties == null) {
			properties = paramProperties;
		} else {
			Enumeration<?> enums = paramProperties.propertyNames();
			while (enums.hasMoreElements()) {
				String key = (String) enums.nextElement();
				properties.setProperty(key, paramProperties.getProperty(key));
				logger.info("add ConfigUtil:  " + key + " = '" + paramProperties.getProperty(key));
			}
		}
	}

	protected void processProperties(ConfigurableListableBeanFactory paramConfigurableListableBeanFactory,
			Properties paramProperties) throws BeansException {
		logger.info("加载i118n国际化资源！");
		appendProperties(paramProperties);
	}

}
