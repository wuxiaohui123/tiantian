package com.yinhai.ta3.organization.api;

import java.util.List;

import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.iorg.IOrg;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.service.Service;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.User;

public interface OrganizationEntityService extends Service {

	public static final String SERVICEKEY = "organizationEntityService";

	public abstract IUser getUserByUserId(Long paramLong);

	public abstract IUser getUserByLoginId(String paramString);

	public abstract List<IUser> getUserByName(String paramString);

	public abstract IUser getUserBySameLevelName(Long paramLong, String paramString);

	public abstract List<IUser> getAllUsers(Boolean paramBoolean1, Boolean paramBoolean2);

	public abstract List<IUser> getAllUsersByOrg(Org paramOrg);

	public abstract List<IUser> getAllUsersByDepartId(Long paramLong);

	public abstract List<IUser> getUsersByOrg(Org paramOrg);

	public abstract List<IUser> getUsersByDepartId(Long paramLong);

	public abstract List<IUser> getUsersByPositionId(Long paramLong);

	public abstract List<IUser> getUsersByMenuId(Long paramLong);

	public abstract IUser getOrgmanagerUserByDepartId(Long paramLong);

	public abstract List<IUser> getDeputyUsersByDepartId(Long paramLong);

	public abstract IOrg getDepart(Long paramLong);

	public abstract IOrg getDepartByUser(User paramUser);

	public abstract IOrg getDepartByUserId(Long paramLong);

	public abstract IOrg getDepartByPositionId(Long paramLong);

	public abstract List<IOrg> getDepartByDepartName(String paramString);

	public abstract IOrg getDepartBySameLevelDepartName(Long paramLong, String paramString);

	public abstract List<IOrg> getAllDeparts();

	public abstract List<IOrg> getDepartsByDepartId(Long paramLong);

	public abstract List<IOrg> getDepartsAndSelfByDepartId(Long paramLong);

	public abstract List<IOrg> getChildDepartsByPorg(Org paramOrg);

	public abstract List<IOrg> getChildDepartsByDepartId(Long paramLong);

	public abstract IOrg getParentDepartByOrg(Org paramOrg);

	public abstract IOrg getParentDepartByDepartId(Long paramLong);

	public abstract IPosition getPositionByPositionId(Long paramLong);

	public abstract IPosition getPositionBySameLevelPositionName(Long paramLong, String paramString);

	public abstract IPosition getPositionByUserId(Long paramLong);

	public abstract List<IPosition> getAllPositionsByOrg(Org paramOrg);

	public abstract List<IPosition> getAllPositionsByDepartId(Long paramLong);

	public abstract List<IPosition> getPubPositionsByOrg(Org paramOrg);

	public abstract List<IPosition> getPubPositionsByDepartId(Long paramLong);

	public abstract List<IPosition> getPerPositionsByDepartId(Long paramLong, Boolean paramBoolean);

	public abstract List<IPosition> getPositionsByMenuId(Long paramLong);

	public abstract List<IPosition> getPositionsByUserId(Long paramLong);

	public abstract List<IPosition> getPubPositionsByUserId(Long paramLong);

	public abstract List<IPosition> getPerPositionsByUserId(Long paramLong);

	public abstract IPosition getDirectPerPositionByUserId(Long paramLong);

	public abstract List<IPosition> getSharePositionBySPositionId(Long paramLong);

	public abstract boolean checkUserLoginIdAndPass(String paramString1, String paramString2);

	@Deprecated
	public abstract List<IUser> getUsersByYab003AndPositionName(String yab003, String positionname,
			String positionCategory);

	public abstract List<IUser> getUsersByYab139AndPositionName(String yab139, String positionname,
			String positionCategory);

	@Deprecated
	public abstract List<IUser> queryUserByMenuIdAndYab003(Long paramLong, String paramString1, String paramString2);

	public abstract List<IUser> queryUserByMenuIdAndYab139(Long paramLong, String paramString1, String paramString2);

	public abstract List<String> queryYab139ByYab003(String paramString);

	public abstract List<String> queryYab003ByYab139(String paramString);

	public abstract List<AppCode> queryYab139ByUserIdAndMenuId(Long paramLong1, Long paramLong2);

	public abstract List<IMenu> queryMenusByPositionId(Long paramLong);

	public abstract List<IUser> queryUsersByYab139AndCategory(String paramString1, String paramString2);
}
