package com.yinhai.abpmn.app.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.AllowedMethods;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

import com.yinhai.abpmn.app.domain.ProcesstypeDomain;
import com.yinhai.abpmn.app.service.ProcessConfigService;
import com.yinhai.abpmn.core.AbpmnAppAction;
import com.yinhai.ta3.organization.service.IAdminUserMgService;

@Namespace("/abpmn")
@AllowedMethods({ "queryProcessDefinition", "getAsyncData", "getProcessFlowNodes" })
@Action(value = "abpmnConfigAction", results = { @Result(name = "success", location = "/abpmn/processManage/processConfig/processConfig.jsp") })
public class AbpmnConfigAction extends AbpmnAppAction {

	private static final String USER_TASK = "UserTask";
	private IAdminUserMgService adminUserMgService = (IAdminUserMgService) super.getService("adminUserMgService");
	private ProcessConfigService processConfigService = getService("processConfigService",ProcessConfigService.class);

	@Override
	public String execute() throws Exception {
		RepositoryService repService = getProcessEngine().getRepositoryService();
		ProcessDefinitionQuery proDefQuery = repService.createProcessDefinitionQuery();
		List<ProcessDefinition> proDeflist = proDefQuery.orderByProcessDefinitionVersion().latestVersion().asc().list();
		List<Map<String, String>> processDefList = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		for (ProcessDefinition p : proDeflist) {
			map = new HashMap<String, String>();
			map.put("id", p.getId());
			map.put("name", p.getName());
			processDefList.add(map);
		}
		//setSelectInputList("processName", processDefList);

		/*
		 * List<Org> corgs =
		 * adminUserMgService.getCurPositionOrgMgScope(getDto()
		 * .getUserInfo().getNowPosition().getPositionid());
		 * 
		 * List<Map<String,Object>> newOrgs = new
		 * ArrayList<Map<String,Object>>(); Map<String, Object> orgMap = null;
		 * for (int i = 0; i < corgs.size(); i++) { Org org = (Org)
		 * corgs.get(i); orgMap = new HashMap<String,Object>(); orgMap.put("id",
		 * org.getOrgid()); orgMap.put("name", org.getOrgname());
		 * newOrgs.add(orgMap); } setSelectInputList("busOrg", newOrgs);
		 */
		return SUCCESS;
	}

	public String queryProcessDefinition() throws Exception {
		ProcessDefinition processDef = processConfigService.getProcessDefinitionById(getDto().getAsString("processDefId"));
		Map<String, String> map = new HashMap<String, String>();
		if (processDef != null) {
			map.put("id", processDef.getId());
			map.put("name", processDef.getName());
			map.put("key", processDef.getKey());
			map.put("version", String.valueOf(processDef.getVersion()));
			map.put("desc", processDef.getDescription());
		}
		setData("processDef", map);
		return JSON;
	}

	public String getAsyncData() throws Exception {
		List<ProcesstypeDomain> processtypelist = getDao().queryForList("abpmn_processtype.getList");
		StringBuffer treedata = new StringBuffer("[");
		for (int i = 0; i < processtypelist.size(); i++) {
			ProcesstypeDomain domain = processtypelist.get(i);
			treedata.append("{id:'").append(domain.getTypecode()).append("',");
			treedata.append("name:'").append(domain.getTypename()).append("',");
			if (("".equals(domain.getParenttypecode())) || (domain.getParenttypecode() == null)) {
				treedata.append("pId:'").append("0").append("'}");
			} else {
				treedata.append("pId:'").append(domain.getParenttypecode()).append("'}");
			}
			if (i < processtypelist.size() - 1) {
				treedata.append(",");
			}
		}
		treedata.append("]");
		writeJsonToClient(treedata);
		return null;
	}

	public String getProcessFlowNodes() throws Exception {
		String processDefId = getDto().getAsString("processDefId");
		BpmnModel model = getProcessEngine().getRepositoryService().getBpmnModel(processDefId);
		if (model != null) {
			Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
			for (FlowElement e : flowElements) {
				System.out.println("flowelement id:" + e.getId() + "  name:" + e.getName() + "  class:" + e.getClass().getSimpleName());
				if (USER_TASK.equals(e.getClass().getSimpleName())) {

				}
			}
		}
		return JSON;
	}

}
