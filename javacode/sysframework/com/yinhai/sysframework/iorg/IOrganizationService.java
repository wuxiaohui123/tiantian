package com.yinhai.sysframework.iorg;

import java.util.List;

public interface IOrganizationService {

	 String SERVICEKEY = "organizationService";

	 IPosition getPosition(Long paramLong);

	 IUser getUserByLoginId(String paramString);

	 IPosition getUserMainPosition(Long paramLong);

	 void lockUser(Long paramLong);

	 int updateUserFaultNum(Long userId, int FaultNum);

	 List<IPosition> getUserPositions(Long userId);

	 IOrg getOrg(Long orgid);
}
