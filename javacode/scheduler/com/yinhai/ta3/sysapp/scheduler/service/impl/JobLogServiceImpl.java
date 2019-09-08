package com.yinhai.ta3.sysapp.scheduler.service.impl;

import java.util.Map;

import com.yinhai.sysframework.service.BaseService;
import com.yinhai.ta3.sysapp.scheduler.service.JobLogService;

public class JobLogServiceImpl extends BaseService implements JobLogService {

	public void insertJobLog(Map map) {
		map.put("log_id", getSequence("seq_default"));
		dao.insert("tascheduler.insertJobLog", map);
	}

	public void clearSuccLog() {
		int count = dao.delete("tascheduler.clearSuccLog");
	}

}
