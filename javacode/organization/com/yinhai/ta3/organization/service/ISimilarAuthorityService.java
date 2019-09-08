package com.yinhai.ta3.organization.service;

import java.util.List;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.service.Service;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.org.domain.Position;

public interface ISimilarAuthorityService extends Service {

	public static final String SERVICEKEY = "similarAuthorityService";

	public abstract List<PermissionTreeVO> getRePermissionTreeByPositionid(Long paramLong);

	public abstract List<Position> querypositionsByAuthorities(List<Key> paramList, Long paramLong);
}
