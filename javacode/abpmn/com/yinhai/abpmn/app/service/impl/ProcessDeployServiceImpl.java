package com.yinhai.abpmn.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;

import com.yinhai.abpmn.app.service.ProcessDeployService;
import com.yinhai.abpmn.core.AbpmnAppServiceImpl;
import com.yinhai.abpmn.domain.ProcessModel;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.util.DateUtil;
import com.yinhai.sysframework.util.ValidateUtil;
@SuppressWarnings("rawtypes")
public class ProcessDeployServiceImpl extends AbpmnAppServiceImpl implements ProcessDeployService {

	@Override
	public String saveFileData(Map paramMap, ParamDTO paramParamDTO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteProcDefine(ParamDTO dto) throws Exception {
		String deploymentId = dto.getAsString("deploymentId");
		getRepositoryService().deleteDeployment(deploymentId, true);
	}

	@Override
	public List<Map<String,Object>> queryProcessDefine(ParamDTO dto) throws Exception {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		ProcessDefinitionQuery  processDefinitionQuery = getRepositoryService().createProcessDefinitionQuery();
		DeploymentQuery deploymentQuery = getRepositoryService().createDeploymentQuery();
		if (ValidateUtil.isNotEmpty(dto.getAsString("processKey"))) {
			processDefinitionQuery.processDefinitionKey(dto.getAsString("processKey"));
		}
		if (ValidateUtil.isNotEmpty(dto.getAsString("processName"))) {
			processDefinitionQuery.processDefinitionName(dto.getAsString("processName"));
		}
		if (ValidateUtil.isNotEmpty(dto.getAsString("processVers"))) {
			processDefinitionQuery.processDefinitionVersion(dto.getAsInteger("processVers"));
		}else{
			processDefinitionQuery.latestVersion();
		}
		List<ProcessDefinition> processDefinitionList = processDefinitionQuery.orderByProcessDefinitionVersion().asc().list();
		Map<String,Object> map = null;
		for (ProcessDefinition definition : processDefinitionList) {
			map = new HashMap<String,Object>();
			map.put("processid", definition.getId());
			map.put("processname", definition.getName());
			map.put("processkey", definition.getKey());
			map.put("processversion", definition.getVersion());
			map.put("processdesc", definition.getDescription());
			map.put("processdeployid", definition.getDeploymentId());
			map.put("filename", definition.getResourceName());
			if(definition.getTenantId() != ""){
				IUser user = getUserInfoByUserId(definition.getTenantId());
				map.put("uploador", user.getName());
			}
			Deployment deployment = deploymentQuery.deploymentId(definition.getDeploymentId()).singleResult();
			map.put("uploadtime",DateUtil.dateToString(deployment.getDeploymentTime()));
			list.add(map);
		}
		return list;
	}

	@Override
	public byte[] getProcessModelResource(Map<String, String> paramterMap) throws Exception {
		return getProcessModel(paramterMap).getBytes();
	}

	@Override
	public List<ProcessModel> getProcessModelResource(String deploymentId) throws Exception {
		return getProcessModel(deploymentId);
	}

}
