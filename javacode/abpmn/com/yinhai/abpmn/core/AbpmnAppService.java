package com.yinhai.abpmn.core;

import java.util.Map;

import org.activiti.engine.repository.ProcessDefinition;

import com.yinhai.abpmn.domain.ProcessModel;
import com.yinhai.sysframework.service.Service;

public interface AbpmnAppService extends Service{
	
	public ProcessModel getProcessModel(Map<String, String> map) throws Exception;
	
	public ProcessDefinition getProcessDefinitionById(String id) throws Exception;
}
