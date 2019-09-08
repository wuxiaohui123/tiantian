package com.yinhai.abpmn.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;

import com.yinhai.sysframework.util.ValidateUtil;

public class ProcessEngineConfig {

	private static Map<String, String> PopMap = new HashMap<String, String>();

	static {
		Properties props = new Properties();
		try {
			ClassPathResource classPath = new ClassPathResource("resource/processEngine.properties");
			BufferedReader bs = new BufferedReader(new InputStreamReader(classPath.getInputStream(),"UTF-8"));
			props.load(bs);
			Iterator<String> it = props.stringPropertyNames().iterator();
			while (it.hasNext()) {
				String key = it.next();
				PopMap.put(key, props.getProperty(key));
			}
			bs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProcessConfig(String key){
		return getPopMap().get(key);
	}
	public static boolean getProcesConfig(String key){
		return Boolean.valueOf(getPopMap().get(key));
	}
	public static String getProcessConfig(String key,String defaultValue){
		String config = getPopMap().get(key);
		if(ValidateUtil.isNotEmpty(config)){
			return config;
		}else{
			return defaultValue;
		}
	}
	public static Map<String, String> getPopMap() {
		return PopMap;
	}

	public static void setPopMap(Map<String, String> popMap) {
		PopMap = popMap;
	}
}
