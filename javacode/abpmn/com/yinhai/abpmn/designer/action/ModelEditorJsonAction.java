package com.yinhai.abpmn.designer.action;

import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.AllowedMethods;
import org.apache.struts2.convention.annotation.Namespace;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yinhai.abpmn.core.AbpmnAppAction;

@Namespace("/abpmn")
@AllowedMethods(value = {"getEditorJson"})
@Action(value="modelEditorJsonAction")
public class ModelEditorJsonAction extends AbpmnAppAction implements ModelDataJsonConstants {

	ProcessEngine processEngine = getProcessEngine();
	private RepositoryService repositoryService = processEngine.getRepositoryService();

	private ObjectMapper objectMapper = new ObjectMapper();

	public String getEditorJson() throws ActivitiException{
		String modelId = request.getParameter("modelId");
		ObjectNode modelNode = null;
		Model model = repositoryService.getModel(modelId);
		if (model != null) {
			try {
				if (StringUtils.isNotEmpty(model.getMetaInfo())) {
					modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
				} else {
					modelNode = objectMapper.createObjectNode();
					modelNode.put("name", model.getName());
				}
				modelNode.put("modelId", model.getId());
				String str = new String(repositoryService.getModelEditorSource(model.getId()),"UTF-8");
				ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(str);
				modelNode.put("model", editorJsonNode);
			} catch (Exception e) {
				throw new ActivitiException("Error creating model JSON", e);
			}
		}
		setData("objmapper", modelNode.toString());
		return JSON;
	}
}
