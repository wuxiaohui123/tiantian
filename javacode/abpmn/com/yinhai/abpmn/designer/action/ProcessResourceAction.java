package com.yinhai.abpmn.designer.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yinhai.abpmn.core.AbpmnAppAction;
import com.yinhai.abpmn.designer.model.BaseProcessResource;

@Namespace("/abpmn")
@Action(value="processResourceAction")
public class ProcessResourceAction extends AbpmnAppAction{
    
	private BaseProcessResource baseProcess =  new BaseProcessResource();
	
	//@RequestMapping(value={"/process-definition/{processDefinitionId}/diagram-layout"}, method={RequestMethod.GET}, produces={"application/json"})
	public String getProcessDefinitionDiagram(String processDefinitionId){
		ObjectNode objetNode = baseProcess.getDiagramNode(null, processDefinitionId);
	    setData("objetNode", objetNode);
	    return JSON;
	}
	//@RequestMapping(value={"/process-instance/{processInstanceId}/diagram-layout"}, method={RequestMethod.GET}, produces={"application/json"})
	public String getProcessInstanceDiagram(String processDefinitionId){
		ObjectNode objetNode = baseProcess.getDiagramNode(null, processDefinitionId);
	    setData("objetNode", objetNode);
	    return JSON;
	}
}
