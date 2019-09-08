package com.yinhai.ta3.organization.api;

import java.util.Date;
import java.util.List;

import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.User;

public interface IUserService extends IQueryUsers {

	public abstract User createUser(User paramUser, Long[] paramArrayOfLong, IPosition paramIPosition);

	public abstract boolean updateUser(User paramUser, Long[] paramArrayOfLong, Long paramLong);

	public abstract boolean unUseUser(Long paramLong1, Long paramLong2, Date paramDate);

	public abstract boolean reUseUser(Long paramLong1, Long paramLong2, Date paramDate);

	public abstract boolean batchChangeOrg(Long[] paramArrayOfLong, Long paramLong1, Long paramLong2);

	public abstract List<Position> queryUserPositions(Long paramLong);

	public abstract List<UserAuthrityVO> queryUserUserAuthrity(Long paramLong);
}
