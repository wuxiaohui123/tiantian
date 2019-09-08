package com.yinhai.abpmn.app.action;

import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.AllowedMethods;
import org.apache.struts2.convention.annotation.Namespace;

import com.yinhai.abpmn.core.AbpmnAppAction;

/**
 * 流程处理人
 * 
 * @author wuxiaohui
 * 
 */
@Namespace("/abpmn")
@AllowedMethods({ "getProcessRoleData", "getProcessUserData" })
@Action(value = "processAssignmentAction")
public class ProcessAssignmentAction extends AbpmnAppAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2899906199974541698L;
	
	private IdentityService identityService = getProcessEngine().getIdentityService();
	
	
	public String getProcessUserData() throws Exception{
		List<User> ulist = identityService.createUserQuery().orderByUserId().asc().list();
		writeJsonToClient(ulist);
		return null;
	}
	
	
	public String getProcessRoleData() throws Exception {
		List<Group> glist = identityService.createGroupQuery().orderByGroupId().asc().list();
		writeJsonToClient(glist);
		return null;
	}

	
}
