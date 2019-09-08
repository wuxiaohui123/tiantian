package com.yinhai.ta3.organization.service;

import java.util.List;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.service.Service;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.UserInfoVO;

public interface IDelegatePositionService extends Service {

	public static final String SERVICEKEY = "delegatePositionService";

	public abstract List<UserInfoVO> queryScropOrgUsers(Long paramLong);

	public abstract void deletegatePosition(List<Key> paramList, ParamDTO paramParamDTO);

	public abstract List<Position> queryDelegateeUsers(Long paramLong);

	public abstract void recycleDeletegatePosition(List<Key> paramList, IUser paramIUser);

	public abstract void updateDeletegatePositionPermissions(List<Key> paramList, ParamDTO paramParamDTO);
}
