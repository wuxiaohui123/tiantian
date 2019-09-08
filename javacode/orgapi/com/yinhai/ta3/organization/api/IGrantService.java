package com.yinhai.ta3.organization.api;

import java.util.Date;

import com.yinhai.ta3.system.org.domain.PermissionInfoVO;
import com.yinhai.ta3.system.org.domain.UserPositionId;

public interface IGrantService {

	String SERVICEKEY = "grantService";

	UserPositionId grantPositionToUser(Long userId, Long positionId, Long operator, Date operateTime);

	boolean retrievePositionFromUser(Long paramLong1, Long paramLong2, Long paramLong3, Date paramDate);

	boolean grantUserFunctionUsePermission(Long paramLong1, Long paramLong2, Long paramLong3, Long paramLong4, Date paramDate);

	boolean retrieveUserFunctionUsePermission(Long paramLong1, Long paramLong2, Long paramLong3, Long paramLong4, Date paramDate);

	boolean grantUserFunctionAuthrityPermission(Long paramLong1, Long paramLong2, Long paramLong3, boolean paramBoolean, Long paramLong4, Date paramDate);

	boolean retrieveUserFunctionAuthtiryPermission(Long paramLong1, Long paramLong2, Long paramLong3, Long paramLong4, Date paramDate);

	boolean retrieveUserFunctionReAuthtiryPermission(Long paramLong1, Long paramLong2, Long paramLong3, Long paramLong4,
			Date paramDate);

	boolean grantPositionFunctionUsePermission(Long paramLong1, Long paramLong2, Long paramLong3, Date paramDate);

	boolean retrievePositionFunctionUsePermission(Long paramLong1, Long paramLong2, Long paramLong3, Date paramDate);

	boolean grantPositionFunctionAuthrityPermission(Long paramLong1, Long paramLong2, boolean paramBoolean, Long paramLong3,
			Date paramDate);

	boolean retrievePositionFunctionAuthtiryPermission(Long paramLong1, Long paramLong2, Long paramLong3, Date paramDate);

	boolean retrievePositionFunctionReAuthtiryPermission(Long paramLong1, Long paramLong2, Long paramLong3, Date paramDate);

	boolean grantUserFunctionUsePermission(Long paramLong1, Long paramLong2, Long paramLong3, Date paramDate);

	boolean retrieveUserFunctionUsePermission(Long paramLong1, Long paramLong2, Long paramLong3, Date paramDate);

	boolean grantUserFunctionAuthrityPermission(Long paramLong1, Long paramLong2, boolean paramBoolean, Long paramLong3,
			Date paramDate);

	boolean retrieveUserFunctionAuthtiryPermission(Long paramLong1, Long paramLong2, Long paramLong3, Date paramDate);

	boolean retrieveUserFunctionReAuthtiryPermission(Long paramLong1, Long paramLong2, Long paramLong3, Date paramDate);

	boolean permissionChangeUniteFunction(PermissionInfoVO paramPermissionInfoVO);
}
