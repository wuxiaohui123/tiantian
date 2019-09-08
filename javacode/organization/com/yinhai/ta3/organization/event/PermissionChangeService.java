package com.yinhai.ta3.organization.event;

import javax.jws.WebService;

import com.yinhai.sysframework.service.WsService;

@WebService
public interface PermissionChangeService extends WsService {

	public static final String SERVICEKEY = "permissionChangeService";

	public abstract void refreshFunctionMemory(Long paramLong1, Long paramLong2);
}
