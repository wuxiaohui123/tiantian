package com.yinhai.abpmn.designer.action;

import java.io.InputStream;

import org.activiti.engine.ActivitiException;
import org.apache.commons.io.IOUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.AllowedMethods;
import org.apache.struts2.convention.annotation.Namespace;

import com.yinhai.abpmn.core.AbpmnAppAction;

@Namespace("/abpmn")
@AllowedMethods(value = {"getStencilset"})
@Action(value="stencilsetRestResourceAction")
public class StencilsetRestResourceAction extends AbpmnAppAction{

	public String getStencilset() throws ActivitiException{
		InputStream stencilsetStream = getClass().getClassLoader().getResourceAsStream("resource/stencilset.json");
		try {
			String stencilsetStr = IOUtils.toString(stencilsetStream, "utf-8");
			writeJsonToClient(stencilsetStr);
		} catch (Exception e) {
			throw new ActivitiException("Error while loading stencil set", e);
		}
		return null;
	}
	
	
}
