package com.yinhai.ta3.sysapp.menumg.service.impl;

import java.util.List;

import javax.jws.WebMethod;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.config.IConfigSyspath;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.config.service.IConfigService;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.service.WsBaseService;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.sysapp.menumg.service.ICommonMenuService;
import com.yinhai.ta3.system.sysapp.domain.CommonMenu;
import com.yinhai.ta3.system.sysapp.domain.CommonMenuId;
import com.yinhai.ta3.system.sysapp.domain.Menu;

@SuppressWarnings("unchecked")
public class CommonMenuServiceImpl extends WsBaseService implements ICommonMenuService {

	private static final String menuclassname = SysConfig.getSysConfig(Menu.class.getName(), Menu.class.getName());
	private SimpleDao hibernateDao;

	@WebMethod(exclude = true)
	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public List<Menu> getCommonMenusByUserId(Long userId) {
		if (ValidateUtil.isEmpty(userId)) {
			throw new AppException("用户为空");
		}
		IConfigService configService = (IConfigService) ServiceLocator.getService("configService");
		List<IConfigSyspath> syslist = configService.getConfigSysPaths();
		String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
		boolean isPortal = SysConfig.getSysconfigToBoolean("isPortal", false);
		if (userId.equals(IUser.ROOT_USERID)) {
			if ((!isPortal) && (syslist != null) && (syslist.size() > 1)) {
				return hibernateDao.createQuery(
						"select distinct m from CommonMenu cm," + menuclassname
								+ " m where 1=1 and cm.id.userid=? and cm.id.menuid=m.menuid and m.effective=? and m.syspath=?",
						new Object[] { userId, "0", curSyspathId }).list();
			}
			return hibernateDao.createQuery(
					"select distinct m from CommonMenu cm," + menuclassname
							+ " m where 1=1 and cm.id.userid=? and cm.id.menuid=m.menuid and m.effective=? ", new Object[] { userId, "0" }).list();
		}

		if ((!isPortal) && (syslist != null) && (syslist.size() > 1)) {
			return hibernateDao
					.createQuery(
							"select distinct m from CommonMenu cm,"
									+ menuclassname
									+ " m,PositionAuthrity pa where 1=1 and cm.id.userid=? and cm.id.menuid=m.menuid and m.effective=? and m.syspath=? and m.menuid=pa.id.tamenu.menuid and pa.usepermission=? order by m.menulevel,m.menuidpath",
							new Object[] { userId, "0", curSyspathId, "1" }).list();
		}
		return hibernateDao
				.createQuery(
						"select distinct m from CommonMenu cm,"
								+ menuclassname
								+ " m,PositionAuthrity pa where 1=1 and cm.id.userid=? and cm.id.menuid=m.menuid and m.effective=? and m.menuid=pa.id.tamenu.menuid and pa.usepermission=? order by m.menulevel,m.menuidpath",
						new Object[] { userId, "0", "1" }).list();
	}

	public void saveCommonMenus(Long userId, List<Key> list) {
		for (Key key : list) {
			Long menuId = key.getAsLong("menuId");
			String checked = key.getAsString("checked");
			if ("true".equals(checked)) {
				saveCommonMenu(userId, menuId);
			} else {
				deleteCommonMenu(userId, menuId);
			}
		}
	}

	public void deleteCommonMenu(Long userId, Long menuId) {
		if ((ValidateUtil.isEmpty(userId)) || (ValidateUtil.isEmpty(menuId))) {
			throw new AppException("用户为空或者菜单为空");
		}
		CommonMenuId id = new CommonMenuId(userId, menuId);
		hibernateDao.delete(new CommonMenu(id));
	}

	public void saveCommonMenu(Long userId, Long menuId) {
		if ((ValidateUtil.isEmpty(userId)) || (ValidateUtil.isEmpty(menuId))) {
			throw new AppException("用户为空或者菜单为空");
		}
		CommonMenuId id = new CommonMenuId(userId, menuId);
		hibernateDao.save(new CommonMenu(id));
	}

	public void deleteCommonMenus(Long userId, List<Key> list) {
		for (Key key : list) {
			Long menuId = key.getAsLong("menuId");
			deleteCommonMenu(userId, menuId);
		}
	}
}
