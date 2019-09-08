package com.yinhai.abpmn.app.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.AllowedMethods;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

import com.yinhai.abpmn.app.service.ProcessRoleService;
import com.yinhai.abpmn.core.AbpmnAppAction;
import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.ValidateUtil;
@SuppressWarnings({ "unchecked", "rawtypes" })
@Namespace("/abpmn")
@AllowedMethods({"getGroups", "queryRoleUsers","queryOrgUsers","addGroup","deleteGroup","toAddGroup","toAddUser","addRoleUserByGroup","deleteRoleUsers"})
@Action(value = "processRoleAction",results = {
		@Result(name = "success",location = "/abpmn/processManage/processRole/processRole.jsp"),
		@Result(name = "toaddgroup",location = "/abpmn/processManage/processRole/addProcessRole.jsp"),
		@Result(name = "toadduser",location = "/abpmn/processManage/processRole/addProcessUser.jsp")})
public class ProcessRoleAction extends AbpmnAppAction{

	private IdentityService identityService = getProcessEngine().getIdentityService();
	
	private ProcessRoleService processRoleService = (ProcessRoleService)super.getService("processRoleService");
	@Override
	public String execute() throws Exception {
		List<Group> glist = identityService.createGroupQuery().orderByGroupId().asc().list();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		for (Group group : glist) {
			map = new HashMap<String,String>();
			map.put("groupid", group.getId());
			map.put("groupname", group.getName());
			map.put("grouptype", group.getType());
			list.add(map);
		}
		setListView("myGroup", list);
		return SUCCESS;
	}
	
	public String getGroups() throws Exception {
		String group = getDto().getAsString("paramter");
		GroupQuery query = identityService.createGroupQuery();
		if(ValidateUtil.isNotEmpty(group)){
			query.groupName(group);
		}
		List<Group> grouplist = query.orderByGroupId().asc().list();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		for (Group group1 : grouplist) {
			map = new HashMap<String,String>();
			map.put("groupid", group1.getId());
			map.put("groupname", group1.getName());
			map.put("grouptype", group1.getType());
			list.add(map);
		}
		setListView("myGroup", list);
		return JSON;
	}
	
	public String toAddGroup() throws Exception{
		return "toaddgroup";
	}
	
	public String toAddUser() throws Exception {
		setData("groupId",getDto().getAsString("groupId"));
		return "toadduser";
	}
	public String addGroup() throws Exception {
		processRoleService.saveGroup(getDto());
		setMsg("新增角色成功！");
		return JSON;
	}
	
	public String deleteGroup() throws Exception {
		String groupId = getDto().getAsString("groupId");
		if(ValidateUtil.isNotEmpty(groupId)){
			processRoleService.deleteGroup(groupId);
			setMsg("删除角色成功！");			
		}else{
			setMsg("参数为空！");
		}
		return JSON;
	}
	
	public String queryOrgUsers() throws Exception {
		PageBean pageBean = getDao().queryForPageWithCount("userGrid", "processEngine.queryUsers", getDto(), getDto());
		setList("userGrid", pageBean);
		return JSON;
	}
	
	public String addRoleUserByGroup() throws Exception {
		String groupId = getDto().getAsString("groupId");	
		List<Key> selList = getSelected("userGrid");
		if(selList.size() > 0){
			processRoleService.saveRoleUser(groupId, selList);
		}else{
			setMsg("请选择人员！");
		}
		return JSON;
	}
	
	public String queryRoleUsers() throws Exception {
		String groupid = getDto().getAsString("groupid");
		List<Map<String, String>> list = getDao().queryForList("processEngine.getAissgenByGroupId", groupid);
		for (Map<String, String> map : list) {
			String id = String.valueOf(map.get("id"));
			List<String> keys = identityService.getUserInfoKeys(id);
			for (String key : keys) {
				map.put(key, identityService.getUserInfo(id, key));
			}
		}
		setList("puserGrid", list);
		return JSON;
	}
	
	public String deleteRoleUsers() throws Exception {
		List<Key> selList = getSelected("puserGrid");
		for (Map map : selList) {
			String groupid = String.valueOf(map.get("groupid"));
			String userid = String.valueOf(map.get("userid"));
			processRoleService.deleteRoleUser(groupid, userid);
		}
		setMsg("删除成功！");
		return JSON;
	}
}