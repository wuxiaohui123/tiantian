package com.yinhai.sysframework.service;

import com.yinhai.sysframework.config.SysConfig;

public class AppManager {

	public static String getSysConfig(String key) {
		return SysConfig.getSysConfig(key);
	}
}
