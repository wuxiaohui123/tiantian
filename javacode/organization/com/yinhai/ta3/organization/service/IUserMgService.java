package com.yinhai.ta3.organization.service;

import java.util.List;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.PositionInfoVO;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.org.domain.UserInfoVO;
import com.yinhai.ta3.system.org.domain.UserPerrmissionVO;
import com.yinhai.ta3.system.org.domain.UserPosition;

public interface IUserMgService {

	public static final String SERVICEKEY = "userMgService";

	public abstract User createUser(ParamDTO paramParamDTO);

	public abstract boolean checkSameUserId(ParamDTO paramParamDTO);

	public abstract void updateUser(ParamDTO paramParamDTO);

	public abstract void unUseUser(Long paramLong1, Long paramLong2, ParamDTO paramParamDTO);

	public abstract List<UserInfoVO> queryUsers(ParamDTO paramParamDTO);

	public abstract boolean checkOrgInUserCurrentPositionOrg(ParamDTO paramParamDTO);

	public abstract List<Object[]> queryUserPositions(ParamDTO paramParamDTO);

	public abstract List<Object[]> queryUserPerrmission(ParamDTO paramParamDTO);

	public abstract Position createUserPosition(ParamDTO paramParamDTO);

	public abstract UserPosition createUserPositionRefrence(ParamDTO paramParamDTO);

	public abstract List<UserPosition> createUserPositionRefrences(ParamDTO paramParamDTO);

	public abstract List<Position> getCurrentUserCanDistributionUserPersitions(ParamDTO paramParamDTO);

	public abstract List<PositionInfoVO> getCurrentUserCanDistributionPositionsByOrg(ParamDTO paramParamDTO);

	public abstract List<PositionInfoVO> getCurrentUserCanDistributionUserPositionsByOrg(ParamDTO paramParamDTO);

	public abstract UserPosition removeUserPosition(ParamDTO paramParamDTO);

	public abstract PageBean queryUsersInfo(ParamDTO paramParamDTO, String paramString, int paramInt1, int paramInt2);

	public abstract List<UserPerrmissionVO> queryUserPerrmission(Long paramLong);

	public abstract void unBatchUseUser(Long[] paramArrayOfLong, ParamDTO paramParamDTO);

	public abstract void reUser(Long paramLong1, Long paramLong2, ParamDTO paramParamDTO);

	public abstract void batchReUser(Long[] paramArrayOfLong, ParamDTO paramParamDTO);

	public abstract User getUser(Long paramLong);

	public abstract User getUser(String paramString);

	public abstract List<Position> queryUserPostions(Long paramLong);

	public abstract List<Position> queryUserPersionalPostions(Long paramLong);

	public abstract void resetPassword(ParamDTO paramParamDTO);

	public abstract void batchChangeUserOrg(Long[] paramArrayOfLong, ParamDTO paramParamDTO);

	public abstract void updateDirectAndAffiliatedOrgs(Long paramLong1, List<Key> paramList, Long paramLong2,
			IUser paramIUser);

	public abstract PageBean queryUserOpLogs(String paramString, ParamDTO paramParamDTO);

	public abstract void deleteUsers(List<Key> paramList, ParamDTO paramParamDTO);

	public abstract void unLockUser(Long paramLong, IUser paramIUser);

	public abstract List<AppCode> queryDataField(Long paramLong1, Long paramLong2);
}
