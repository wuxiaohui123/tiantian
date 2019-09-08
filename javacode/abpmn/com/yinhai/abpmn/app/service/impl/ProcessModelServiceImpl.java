package com.yinhai.abpmn.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.raq.common.DateUtil;
import com.yinhai.abpmn.app.service.ProcessModelService;
import com.yinhai.abpmn.core.AbpmnAppServiceImpl;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.util.ValidateUtil;

public class ProcessModelServiceImpl extends AbpmnAppServiceImpl implements ProcessModelService {

	@Override
	public List<Map<String,Object>> queryProcessModel(String value) throws Exception {
		String querysql = "SELECT * FROM ACT_RE_MODEL";
		if(ValidateUtil.isNotEmpty(value)){
			querysql += " WHERE ID_ LIKE '%" + value +"%' OR NAME_ LIKE '%" + value + "%'";
		}
		querysql += " ORDER BY CREATE_TIME_";
		List<Model> modellist = getRepositoryService().createNativeModelQuery().sql(querysql).list();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = null;
		for (Model model : modellist) {
			map = new HashMap<String,Object>();
			map.put("modelId", model.getId());//模型ID
			map.put("modelName", model.getName());//模型名称
			map.put("modelVersion", model.getVersion());//模型版本
			map.put("modelKey", model.getKey());//模型Key
			map.put("modelType", model.getCategory());//模型类型
			map.put("modelCreateTime", DateUtil.formatDate(model.getCreateTime()));//模型创建时间
			map.put("modelLastUpdateTime", DateUtil.formatDate(model.getLastUpdateTime()));//模型最后修改时间
			map.put("modelMetaInfo", model.getMetaInfo());
			map.put("modelDeployId", model.getDeploymentId());
			map.put("modelTenantId", model.getTenantId());
			list.add(map);
		}
		return list;
	}
	
	/** (non-Javadoc)
	 * @see com.yinhai.abpmn.app.service.ProcessModelDesigerService#saveProcessModelData(com.yinhai.sysframework.dto.ParamDTO)
	 */
	@Override
	public String saveProcessModelData(ParamDTO dto) throws Exception {
		String name = dto.getAsString("pName");
		String key = dto.getAsString("pKey");
		String description = StringUtils.defaultString(dto.getAsString("pDesc"));
		Model modelData = getRepositoryService().newModel();
		ObjectNode editorNode = getObjectMapper().createObjectNode();  
        editorNode.put("id", "processId");  
        editorNode.put("resourceId", "processId");  
        
        ObjectNode stencilSetNode = getObjectMapper().createObjectNode();  
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");  
        editorNode.put("stencilset", stencilSetNode); 
        //流程配置信息
        ObjectNode propertiesNode = getObjectMapper().createObjectNode();
        propertiesNode.put("process_id", key);//流程唯一标识
        propertiesNode.put("name", name);//流程名称
        propertiesNode.put("documentation", description);//流程文档描述
        propertiesNode.put("process_author", dto.getUserInfo().getName());//流程作者
        editorNode.put("properties", propertiesNode); 
        
		ObjectNode modelObjectNode = getObjectMapper().createObjectNode();  
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);  
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);  
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description); 
        modelData.setName(name);  
        modelData.setKey(StringUtils.defaultString(key)); 
        modelData.setMetaInfo(modelObjectNode.toString());  
        modelData.setTenantId(dto.getUserInfo().getUserId());
        modelData.setCategory("http://www.activiti.org/processdef");
        getRepositoryService().saveModel(modelData);  
       
        getRepositoryService().addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("UTF-8"));
        return modelData.getId();
	}

	@Override
	public Integer updateProcessModel(ParamDTO dto) throws Exception {
		return dao.update("processEngine.updateProcessModel", dto);
	}

	@Override
	public Integer deleteProcessModel(String modelid) throws Exception {
		getRepositoryService().deleteModel(modelid);
		return null;
	}

	@Override
	public Deployment deploymentProcessModel(ParamDTO dto) throws Exception {
    	//String userid = dto.getUserInfo().getUserId();
		//获取模型数据
    	Model modelData = getRepositoryService().getModel(dto.getAsString("modelId"));
    	//将模型数据转换为二进制数据
    	byte[] objetByte = getRepositoryService().getModelEditorSource(modelData.getId());
    	//将模型数据转换为JSON数据
    	ObjectNode modelNode = (ObjectNode) getObjectMapper().readTree(objetByte);
    	//将JOSN数据转换为BPMN模型数据
    	BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
    	String processName = modelData.getName() + ".bpmn";
    	//创建流程模型部署对象
    	DeploymentBuilder deployment = getRepositoryService().createDeployment();
    	//添加流程模型部署属性
    	deployment.addBpmnModel(processName, model).name(modelData.getName()).category(modelData.getCategory());
    	//将流程模型部署并返回部署对象
    	Deployment deploy = deployment.deploy();
    	if(deploy != null && deploy.getId() != null){
    		dto.put("id", modelData.getId());
    		dto.put("deploymentId", deploy.getId());
    		dto.put("category", deploy.getCategory());
    		updateProcessModel(dto);
    	}
		return deploy;
	}

	

}
