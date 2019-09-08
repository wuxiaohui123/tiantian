package com.yinhai.ta3.sysapp.scheduler.service;

import javax.jws.WebService;

@WebService
public interface JobRMIService {

	public abstract String rmi(String paramString1, String paramString2, String paramString3);
}
