package com.yinhai.ta3.organization.service;

import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.service.Service;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.org.domain.UserInfoVO;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public interface IAdminUserMgService extends Service {

	public static final String SERVICEKEY = "adminUserMgService";

	public abstract PageBean queryNoAdminUsers(String paramString, ParamDTO paramParamDTO);

	public abstract List queryYab139sByPositionId(Long paramLong);

	public abstract void addAdminUser(ParamDTO paramParamDTO);

	public abstract List<UserInfoVO> getAdminMgUsersByPositionid(Long paramLong);

	public abstract List<UserInfoVO> getAdminMgUsersNoTransformPositionByPositionid(Long paramLong1, Long paramLong2);

	public abstract void transformAuthority(Long paramLong, List<Key> paramList, IUser paramIUser);

	public abstract void removeAdminMgUser(Long paramLong, IUser paramIUser);

	public abstract List<PermissionTreeVO> getRePermissionTreeByPositionId(Long paramLong);

	public abstract Map<String, List<PermissionTreeVO>> getRePermissionAndAuthrityTreeByPositionid(Long paramLong);

	public abstract void grantAdminUsePermissions(List<Key> paramList1, List<Key> paramList2, ParamDTO paramParamDTO);

	public abstract void recycleAdminUsePermissions(List<Key> paramList1, List<Key> paramList2, ParamDTO paramParamDTO);

	public abstract List<PermissionTreeVO> getAdminRePermissionTreeByPositionid(Long paramLong);

	public abstract List<PermissionTreeVO> getAdminUsePermissionTreeByPositionid(Long paramLong);

	public abstract List<Org> getCurPositionOrgMgScope(Long paramLong);

	public abstract List<Org> getTargetPositionOrgMgScope(Long paramLong);

	public abstract List<Map<String, String>> queryAdminYab003Scope(Long paramLong1, Long paramLong2);

	public abstract void saveAdminYab003Scope(List<Key> paramList, ParamDTO paramParamDTO);

	public abstract void grantAuthorityPermissions(List<Key> paramList1, List<Key> paramList2, ParamDTO paramParamDTO);

	public abstract void recycleAuthorityPermissions(List<Key> paramList1, List<Key> paramList2, ParamDTO paramParamDTO);

	public abstract void saveAdminUsePermission(List<Key> paramList, Long paramLong, ParamDTO paramParamDTO);

	public abstract void saveRoleScopeAclGranting(ParamDTO paramParamDTO);

	public abstract void saveOrgMgScope(Long paramLong, List<Key> paramList, ParamDTO paramParamDTO);

	public abstract List<Menu> queryChildrenMenus(Long paramLong1, Long paramLong2, Long paramLong3);

	public abstract List<Org> queryChildrenOrgs(Long paramLong1, Long paramLong2, Long paramLong3);
}
