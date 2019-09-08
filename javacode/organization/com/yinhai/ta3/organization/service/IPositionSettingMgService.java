package com.yinhai.ta3.organization.service;

import java.util.List;

import com.yinhai.sysframework.service.Service;
import com.yinhai.ta3.system.org.domain.Org;

public interface IPositionSettingMgService extends Service {

	public static final String SERVICEKEY = "positionSettingMgService";

	public abstract List<Org> getTargetPositionOrgMgScope(Long paramLong);
}
