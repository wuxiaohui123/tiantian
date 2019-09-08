package com.yinhai.abpmn.app.service;

import java.util.List;

import com.yinhai.abpmn.core.AbpmnAppService;
import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.dto.ParamDTO;

@SuppressWarnings("rawtypes")
public interface ProcessRoleService extends AbpmnAppService {

	void saveGroup(ParamDTO dto) throws Exception;
	
	void deleteGroup(String groupId) throws Exception;
	
	int saveRoleUser(String groupid,List<Key> selList) throws Exception;
	
	void deleteRoleUser(String groupid,String userid) throws Exception;
}
