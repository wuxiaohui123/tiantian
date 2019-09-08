package com.yinhai.abpmn.core;

import org.activiti.engine.ProcessEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.ta3.organization.service.IUserMgService;
import com.yinhai.webframework.BaseAction;
/**
 * 流程引擎公共Action
 * @author wuxiaohui 
 *
 */
public class AbpmnAppAction extends BaseAction {

	private IUserMgService userMgService = super.getService("userMgService", IUserMgService.class);
	
	public ProcessEngine getProcessEngine(){
		return super.getService("processEngine", ProcessEngine.class);
	}
	
	public ObjectMapper getObjectMapper(){
		return super.getService("objectMapper",ObjectMapper.class);
	}
	
	public IUser getUserInfoByUserId(String userId){
		return userMgService.getUser(Long.valueOf(userId));
	}
}
