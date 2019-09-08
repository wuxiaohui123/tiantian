package com.yinhai.abpmn.app.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.Deployment;
import org.apache.commons.codec.binary.Base64;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.AllowedMethods;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

import com.yinhai.abpmn.app.service.ProcessModelService;
import com.yinhai.abpmn.core.AbpmnAppAction;
import com.yinhai.abpmn.domain.ProcessModel;
import com.yinhai.sysframework.dto.ParamDTO;

/**
 * 流程模型类
 * 
 * @author wuxiaohui
 * 
 */
@Namespace("/abpmn")
@AllowedMethods(value = { "queryModel", "getProcessModelSvg", "saveProcessModel", "deleteProcessModel",
		"deployProcessModel", "toAddProcessModel" })
@Action(value = "processModelAction", results = {
		@Result(name = "success", location = "/abpmn/processDesiger/processModel.jsp"),
		@Result(name = "addModel", location = "/abpmn/processDesiger/addProcessModel.jsp") })
public class ProcessModelAction extends AbpmnAppAction {

	private ProcessModelService desigerService = (ProcessModelService) super.getService("processModelService");

	@Override
	public String execute() throws Exception {
		String value = request.getParameter("value");
		List list = desigerService.queryProcessModel(value);
		setListView("myProcess", list);
		return SUCCESS;
	}

	/**
	 * 查询流程模型
	 * 
	 * @return
	 * @throws Exception
	 */
	public String queryModel() throws Exception {
		String value = request.getParameter("value");
		List list = desigerService.queryProcessModel(value);
		if (list != null && list.size() > 0) {
			// setList("processModelGrid", list);
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("list", list);
			result.put("start", 0);
			result.put("end", list.size());
			result.put("total", list.size());
			writeJsonToClient(result);
			return null;
		} else {
			setMsg("查询结果为空！");
			return JSON;
		}
	}

	public String getProcessModelSvg() throws Exception {
		ProcessModel processModel = desigerService.getProcessModel(getDto());
		String imageBase64Data = "";
		if (processModel != null) {
			byte[] imageBytes = processModel.getBytes();
			imageBase64Data = new String(Base64.encodeBase64(imageBytes));
		}
		setData("imgData", imageBase64Data);
		return JSON;
	}

	public String toAddProcessModel() throws Exception {
		return "addModel";
	}

	public String saveProcessModel() throws Exception {
		ParamDTO dto = getDto();
		String modelId = desigerService.saveProcessModelData(dto);
		System.out.println("新建的模型ID:" + modelId);
		setData("modelId", modelId);
		return JSON;
	}

	public String updateProcessModel() throws Exception {

		return JSON;
	}

	public String deleteProcessModel() throws Exception {
		String modelId = request.getParameter("id");
		desigerService.deleteProcessModel(modelId);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("code", 200);
		result.put("msg", "删除模型成功！");
		writeJsonToClient(result);
		return null;
	}

	public String deployProcessModel() throws Exception {
		Deployment deploy = desigerService.deploymentProcessModel(getDto());
		if (deploy != null && deploy.getId() != null) {
			setMsg("部署成功！");
		} else {
			setMsg("部署失败！");
		}
		return JSON;
	}

}
