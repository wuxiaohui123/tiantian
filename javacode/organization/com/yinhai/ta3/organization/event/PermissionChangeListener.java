package com.yinhai.ta3.organization.event;

import java.util.concurrent.Executor;

import com.yinhai.sysframework.event.TaEvent;
import com.yinhai.sysframework.event.TaEventListener;
import com.yinhai.sysframework.service.BaseService;
import com.yinhai.ta3.system.org.domain.PermissionInfoVO;

public class PermissionChangeListener extends BaseService implements TaEventListener {

	private PermissionChangeService permissionChangeService;

	public void setPermissionChangeService(PermissionChangeService permissionChangeService) {
		this.permissionChangeService = permissionChangeService;
	}

	public void handleEvent(TaEvent ooe) {
		PermissionInfoVO vo = (PermissionInfoVO) ooe.getSource().getTarget();
		Long menuid = vo.getPermissionid();
		Long positionid = vo.getPositionid();
		permissionChangeService.refreshFunctionMemory(positionid, menuid);
	}

	public String getEventType() {
		return "permission_change";
	}

	public void setTaskExecutor(Executor taskExecutor) {
	}

	public Executor getTaskExecutor() {
		return null;
	}

}
