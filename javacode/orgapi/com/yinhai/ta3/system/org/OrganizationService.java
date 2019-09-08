package com.yinhai.ta3.system.org;

import java.util.List;

import org.springframework.util.Assert;

import com.yinhai.sysframework.iorg.IOrg;
import com.yinhai.sysframework.iorg.IOrganizationService;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.time.ITimeService;
import com.yinhai.ta3.system.org.dao.OrgDao;
import com.yinhai.ta3.system.org.dao.PositionDao;
import com.yinhai.ta3.system.org.dao.UserDao;

public class OrganizationService implements IOrganizationService {

	UserDao userDao;
	PositionDao positionDao;
	OrgDao orgDao;
	ITimeService timeService;

	public List<IPosition> getUserPositions(Long userId) {
		Assert.notNull(userId, "userid不能为空");
		return positionDao.getUserEffectivePosition(userId, timeService.getSysDate());
	}

	public IUser getUserByLoginId(String loginId) {
		Assert.notNull(loginId, "loginId不能为空");
		return userDao.getUserByLoginId(loginId);
	}

	public IPosition getUserMainPosition(Long userid) {
		Assert.notNull(userid, "userid不能为空");
		return positionDao.getUserMainPosition(userid);
	}

	public void lockUser(Long userId) {
		Assert.notNull(userId, "userid不能为空");
		userDao.lockUser(userId);
	}

	public int updateUserFaultNum(Long userId, int FaultNum) {
		Assert.notNull(userId, "userid不能为空");
		return userDao.updateUserFaultNum(userId, Integer.valueOf(FaultNum));
	}

	public IOrg getOrg(Long orgid) {
		Assert.notNull(orgid, "orgid不能为空");
		return orgDao.getOrg(orgid);
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setPositionDao(PositionDao positionDao) {
		this.positionDao = positionDao;
	}

	public void setOrgDao(OrgDao orgDao) {
		this.orgDao = orgDao;
	}

	public void setTimeService(ITimeService timeService) {
		this.timeService = timeService;
	}

	public IPosition getPosition(Long positionid) {
		return positionDao.getPosition(positionid);
	}

}
