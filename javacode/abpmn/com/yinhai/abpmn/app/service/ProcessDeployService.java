package com.yinhai.abpmn.app.service;

import java.util.List;
import java.util.Map;

import com.yinhai.abpmn.core.AbpmnAppService;
import com.yinhai.abpmn.domain.ProcessModel;
import com.yinhai.sysframework.dto.ParamDTO;
@SuppressWarnings("rawtypes")
public interface ProcessDeployService extends AbpmnAppService {

	public String saveFileData(Map paramMap, ParamDTO paramParamDTO) throws Exception;
	
	public List<Map<String,Object>> queryProcessDefine(ParamDTO dto) throws Exception;

	public byte[] getProcessModelResource(Map<String, String> paramterMap) throws Exception;
	
	public List<ProcessModel> getProcessModelResource(String deploymentId) throws Exception;
	
	public void deleteProcDefine(ParamDTO paramParamDTO) throws Exception;
}
