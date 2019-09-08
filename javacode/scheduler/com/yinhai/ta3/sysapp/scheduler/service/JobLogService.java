package com.yinhai.ta3.sysapp.scheduler.service;

import java.util.Map;

import com.yinhai.sysframework.service.Service;

public interface JobLogService extends Service {

	public abstract void insertJobLog(Map paramMap);

	public abstract void clearSuccLog();
}
