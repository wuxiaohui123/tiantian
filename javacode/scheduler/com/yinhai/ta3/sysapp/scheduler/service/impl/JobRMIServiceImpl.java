package com.yinhai.ta3.sysapp.scheduler.service.impl;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.ta3.sysapp.scheduler.service.JobRMIService;
import com.yinhai.ta3.sysapp.scheduler.service.JobService;

@WebService
public class JobRMIServiceImpl implements JobRMIService {

	@Resource
	private WebServiceContext wsContext;

	public String rmi(String serviceId, String addr, String jsonData) {
		try {
			JobService jobService = (JobService) ServiceLocator.getService(serviceId);
			return jobService.run(addr, jsonData);
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
}
