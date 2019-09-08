package com.yinhai.abpmn.app.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.Deployment;

import com.yinhai.abpmn.core.AbpmnAppService;
import com.yinhai.sysframework.dto.ParamDTO;

public interface ProcessModelService extends AbpmnAppService {

	List<Map<String,Object>> queryProcessModel(String value) throws Exception;
	
	String saveProcessModelData(ParamDTO dto) throws Exception;
	
	Integer updateProcessModel(ParamDTO dto) throws Exception;
	
	Integer deleteProcessModel(String modelid) throws Exception;
	
	Deployment deploymentProcessModel(ParamDTO dto) throws Exception;
}
