package com.yinhai.abpmn.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yinhai.abpmn.domain.ProcessModel;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.service.BaseService;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.ta3.organization.service.IUserMgService;

public class AbpmnAppServiceImpl extends BaseService {

	private IUserMgService userMgService = (IUserMgService) ServiceLocator.getService("userMgService");
	
	public ProcessEngine getProcessEngine(){
		return (ProcessEngine) ServiceLocator.getService("processEngine");
	}
	
	public RepositoryService getRepositoryService(){
		return (RepositoryService)ServiceLocator.getService("repositoryService");
	}
	
	public RuntimeService getRuntimeService(){
		return (RuntimeService)ServiceLocator.getService("runtimeService");
	}
	
	public TaskService getTaskService(){
		return (TaskService)ServiceLocator.getService("taskService");
	}
	
	public HistoryService getHistoryService(){
		return (HistoryService)ServiceLocator.getService("historyService");
	}
	
	public ManagementService getManagementService(){
		return (ManagementService)ServiceLocator.getService("managementService");
	}
	
	public IdentityService getIdentityService(){
		return (IdentityService)ServiceLocator.getService("identityService");
	}
	
	public ObjectMapper getObjectMapper(){
		return (ObjectMapper)ServiceLocator.getService("objectMapper");
	}
	
	public ProcessModel getProcessModel(Map<String, String> map){
		return (ProcessModel) dao.queryForObject("processEngine.queryProcessModel", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcessModel> getProcessModel(String deploymentId){
		Map<String, String> map = new HashMap<String, String>();
		map.put("deploymentId", deploymentId);
		return dao.queryForList("processEngine.queryProcessModel", map);
	}
	
	public ProcessDefinition getProcessDefinitionById(String id) throws Exception{
		ProcessDefinitionQuery  query = getRepositoryService().createProcessDefinitionQuery();
		return query.processDefinitionId(id).singleResult();
	}
	
	public IUser getUserInfoByUserId(String userId){
		return userMgService.getUser(Long.valueOf(userId));
	}
} 
