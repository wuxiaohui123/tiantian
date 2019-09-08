package com.yinhai.ta3.sysapp.scheduler.service;

import com.yinhai.sysframework.service.Service;

public interface JobDataRangeService extends Service {

	public abstract Object genData(String paramString1, String paramString2);
}
