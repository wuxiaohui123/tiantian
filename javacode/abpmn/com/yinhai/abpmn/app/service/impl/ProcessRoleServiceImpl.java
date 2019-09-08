package com.yinhai.abpmn.app.service.impl;

import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;

import com.yinhai.abpmn.app.service.ProcessRoleService;
import com.yinhai.abpmn.core.AbpmnAppServiceImpl;
import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.dto.ParamDTO;
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ProcessRoleServiceImpl extends AbpmnAppServiceImpl implements ProcessRoleService {

	public void saveGroup(ParamDTO dto) throws Exception {
		String groupId = getSequence("SEQ_ACTGROUPID");
		
		Group group = getIdentityService().newGroup(groupId);
		
		group.setName(dto.getAsString("groupname"));
		group.setType(dto.getAsString("grouptype"));
		
		getIdentityService().saveGroup(group);
	}

	@Override
	public int saveRoleUser(String groupid,List<Key> list) throws Exception {
		IdentityService identityService = getIdentityService();
		Map<String,String> map = null;
		for (int i = 0; i < list.size(); i++) {
			map = (Map<String,String>)list.get(i);
			User user = identityService.newUser(map.get("userid"));
			user.setId(map.get("userid"));
			user.setName(map.get("name"));
			user.setDepartment(map.get("orgname"));
			user.setSex(map.get("sex"));
			user.setEmail(map.get("email"));
			user.setPhone(map.get("tel"));
			identityService.saveUser(user);
			identityService.createMembership(map.get("userid"), groupid);	
		}
		return list.size();
	}

	@Override
	public void deleteGroup(String groupId) throws Exception {
		getIdentityService().deleteGroup(groupId);
	}

	@Override
	public void deleteRoleUser(String groupid, String userid) throws Exception {
		IdentityService identityService = getIdentityService();
		identityService.deleteUserInfoByUserId(userid);
		identityService.deleteMembership(userid, groupid);
		identityService.deleteUser(userid);
	}
}
