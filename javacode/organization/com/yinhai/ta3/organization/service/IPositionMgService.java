package com.yinhai.ta3.organization.service;

import java.util.Date;
import java.util.List;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.PositionInfoVO;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.org.domain.UserInfoVO;

public interface IPositionMgService {

	public static final String SERVICEKEY = "positionMgService";

	public abstract Position createPosition(ParamDTO paramParamDTO);

	public abstract Position updatePosition(ParamDTO paramParamDTO);

	public abstract void removePosition(List<Key> paramList, ParamDTO paramParamDTO);

	/**
	 * @deprecated
	 */
	public abstract void unUsePosition(Long paramLong, ParamDTO paramParamDTO, String paramString);

	public abstract void unUsePosition(List<Key> paramList, ParamDTO paramParamDTO);

	/**
	 * @deprecated
	 */
	public abstract void usePosition(Long paramLong, ParamDTO paramParamDTO, String paramString);

	public abstract void usePosition(List<Key> paramList, ParamDTO paramParamDTO);

	public abstract Position getPosition(Long paramLong);

	public abstract List<Position> getPositionsByOrgId(Long paramLong);

	public abstract PageBean getDescendantsPositionsByCount(String paramString, ParamDTO paramParamDTO);

	public abstract List<UserInfoVO> getHaveThePositionUsersByPositionId(Long paramLong);

	public abstract List<User> getAllUsersNotInThePosition(Long paramLong);

	public abstract List<PermissionTreeVO> getUsePermissionTreeByPositionId(Long paramLong);

	public abstract List<PermissionTreeVO> getRePermissionTreeByPositionId(Long paramLong);

	public abstract List<PositionInfoVO> getPubPositionsCurUserid(Long paramLong);

	public abstract void saveRoleScopeAclOperate(Long paramLong, List<Key> paramList, ParamDTO paramParamDTO);

	public abstract void recyclePermissions(List<Key> paramList1, List<Key> paramList2, ParamDTO paramParamDTO);

	public abstract void grantUsePermissions(List<Key> paramList1, List<Key> paramList2, ParamDTO paramParamDTO);

	public abstract void clonePermissions(Long paramLong, String[] paramArrayOfString, IUser paramIUser);

	public abstract List<Position> getPubPositionsNoCurUseridByOrgId(ParamDTO paramParamDTO);

	public abstract void removeUserPosition(ParamDTO paramParamDTO);

	public abstract void saveUserAddPositions(List<Key> paramList, ParamDTO paramParamDTO);

	public abstract void setMainPosition(Long paramLong1, Long paramLong2, ParamDTO paramParamDTO);

	public abstract List<UserInfoVO> queryUsers(ParamDTO paramParamDTO);

	public abstract void saveAssignUsers(List<Key> paramList, ParamDTO paramParamDTO);

	public abstract void removeAssignUsers(List<Key> paramList, Long paramLong1, Long paramLong2);

	public abstract List<Position> getAllPositions(ParamDTO paramParamDTO);

	public abstract void saveSharePositions(ParamDTO paramParamDTO, List<Key> paramList);

	public abstract List<Org> queryCopyPositionInOrgBySharePositionId(Long paramLong);

	public abstract void saveEffectiveTimePanel(ParamDTO paramParamDTO);

	public abstract Date queryEffectiveTime(Long paramLong1, Long paramLong2);

	public abstract PageBean queryUsersByDto(ParamDTO paramParamDTO);

	public abstract PageBean queryPubAndSharePositions(ParamDTO paramParamDTO);
}
