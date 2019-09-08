package com.yinhai.ta3.organization.service;

import java.util.List;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.service.Service;
import com.yinhai.ta3.system.org.domain.MenuPositionVO;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.PositionAuthrity;
import com.yinhai.ta3.system.org.domain.PositionInfoVO;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.org.domain.UserInfoVO;
import com.yinhai.ta3.system.org.domain.Yab139Mg;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public interface IPositionUserMgService extends Service {

	public static final String SERVICEKEY = "positionUserMgService";

	public abstract PageBean queryUsersByParamDto(String paramString, ParamDTO paramParamDTO);

	public abstract PageBean queryPositionByParamDto(String paramString, ParamDTO paramParamDTO);

	public abstract PositionInfoVO queryPerMission();

	public abstract List<PositionInfoVO> queryPositionByUserid(Long paramLong);

	public abstract User queryUserByUserid(Long paramLong);

	public abstract List<MenuPositionVO> queryPositionPermissionsByUserId(Long paramLong);

	public abstract List<MenuPositionVO> queryPositionPermissionsByPositionId(Long paramLong);

	public abstract List<UserInfoVO> queryUserInPosition(Long paramLong);

	public abstract List<Org> querySharePosition(Long paramLong);

	public abstract PageBean getPubPositionsNoCurUseridByOrgId(ParamDTO paramParamDTO, String paramString,
			int paramInt1, int paramInt2);

	public abstract PageBean queryUsers(ParamDTO paramParamDTO, String paramString, int paramInt1, int paramInt2);

	public abstract void recyclePermissions(List<Key> paramList1, List<Key> paramList2, ParamDTO paramParamDTO);

	public abstract List<Menu> queryReUsePermissions(Long paramLong);

	public abstract List<PositionAuthrity> queryUsePermissions(Long paramLong);

	public abstract void saveRoleScopeAclOperate(ParamDTO paramParamDTO);

	public abstract String queryDefaultYab139s(Long paramLong);

	public abstract List<Yab139Mg> queryDefaultYab139List(Long paramLong);

	public abstract List<String> queryAdminMgYab139List(Long paramLong);

	public abstract List<AppCode> queryYab139List(Long paramLong1, Long paramLong2);

	public abstract void delDataAccessDimension(Long paramLong1, Long paramLong2);

	public abstract List queryPositionsHaveMenuUsePermission(Long paramLong1, Long paramLong2);
}
