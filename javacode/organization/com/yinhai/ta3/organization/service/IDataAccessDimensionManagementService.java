package com.yinhai.ta3.organization.service;

import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.service.Service;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public interface IDataAccessDimensionManagementService extends Service {

	public static final String SERVICEKEY = "dataAccessDimensionManagementService";

	public abstract IMenu getMenu(Long paramLong);

	public abstract List<Menu> getChildMenus(Long paramLong);

	public abstract void save(Long paramLong1, Long paramLong2, String paramString1, String paramString2,
			List<Key> paramList);

	public abstract PageBean queryPos(ParamDTO paramParamDTO);

	public abstract List<PermissionTreeVO> queryTrree(Long paramLong);

	public abstract void saveAccess(Long paramLong, String paramString1, String paramString2, List<Key> paramList1,
			List<Key> paramList2);

	public abstract List<Map<String, String>> queryAdminYab139ScopeNoSelected(Long paramLong1, Long paramLong2,
			List<AppCode> paramList, Long paramLong3);

	public abstract void removeYab139(Long paramLong1, Long paramLong2, String paramString, List<Key> paramList);

	public abstract void saveAll(Long paramLong1, Long paramLong2, String paramString1, String paramString2);

	public abstract boolean checkAllAccess(Long paramLong1, Long paramLong2, String paramString);
}
