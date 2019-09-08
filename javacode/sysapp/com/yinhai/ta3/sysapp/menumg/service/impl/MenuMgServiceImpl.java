package com.yinhai.ta3.sysapp.menumg.service.impl;

import java.util.List;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.security.IPermissionService;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.sysapp.menumg.service.ICommonMenuService;
import com.yinhai.ta3.sysapp.menumg.service.IMenuMgService;
import com.yinhai.ta3.system.sysapp.dao.MenuDao;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class MenuMgServiceImpl implements IMenuMgService {

	String menuClassName = SysConfig.getSysConfig(Menu.class.getName(), Menu.class.getName());

	private MenuDao menuDao;
	private IPermissionService permissionService;
	private ICommonMenuService commonMenuService;

	public void setCommonMenuService(ICommonMenuService commonMenuService) {
		this.commonMenuService = commonMenuService;
	}

	public void setPermissionService(IPermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public List<Menu> getAllMenuInfo() {
		return menuDao.getAllMenuInfo();
	}

	public List<Menu> getChildMenus(Long pMenuid) {
		return menuDao.getChildMenus(pMenuid);
	}

	public Menu createMenu(ParamDTO dto) {
		Menu m = (Menu) dto.toDomainObject(menuClassName);
		Menu pm = (Menu) getMenu(dto.getAsLong("pmenuid"));
		pm.setIsleaf("1");
		menuDao.updateMenu(pm);
		List<Menu> list = getChildMenus(dto.getAsLong("pmenuid"));
		m.setSortno(Long.valueOf(list.size()));
		m.setIsleaf("0");
		m.setResourcetype("01");
		m.setMenulevel(Long.valueOf(dto.getAsLong("menulevel").longValue() + 1L));
		Menu createMenu = menuDao.createMenu(m);
		m.setMenuidpath(dto.getAsString("menuidpath") + "/" + createMenu.getMenuid());
		m.setMenunamepath(dto.getAsString("menunamepath") + "/" + createMenu.getMenuname());
		menuDao.updateMenu(m);
		insertGrantMethods(dto, m);

		Long positionid = IPosition.ADMIN_POSITIONID;
		Long userid = IUser.ROOT_USERID;
		permissionService.clearUserPermissionMenusCache(userid);
		permissionService.clearUserPermissionMenusCache(userid, positionid);
		permissionService.clearUserPermissionUrlCache(userid);
		permissionService.clearUserPermissionUrlCache(userid, positionid);
		permissionService.clearPositionsByMenuCache(userid, createMenu.getMenuid());
		permissionService.clearUserEffectivePositionsCache(userid);
		return createMenu;
	}

	public void sortMenu(Long[] menuids) {
		if (menuids.length < 1)
			throw new AppException("错误的菜单编号");
		menuDao.sortMenu(menuids);
	}

	public void updateMenu(ParamDTO dto) {
		Menu m = (Menu) dto.toDomainObject(menuClassName);
		menuDao.updateMenu(m);
		insertGrantMethods(dto, m);

		Long positionid = dto.getUserInfo().getNowPosition().getPositionid();
		Long userid = dto.getUserInfo().getUserid();
		permissionService.clearUserPermissionMenusCache(userid);
		permissionService.clearUserPermissionMenusCache(userid, positionid);
		permissionService.clearUserPermissionUrlCache(userid);
		permissionService.clearUserPermissionUrlCache(userid, positionid);
		permissionService.clearPositionsByMenuCache(userid, m.getMenuid());
		permissionService.clearUserEffectivePositionsCache(userid);
	}

	private void insertGrantMethods(ParamDTO dto, Menu m) {
		String methods = dto.getAsString("methods");
		if (ValidateUtil.isNotEmpty(methods)) {
			String[] ms = methods.split(",");
			int i = 0;
			for (String method : ms) {
				Menu methodMenu = (Menu) dto.toDomainObject(menuClassName);
				methodMenu.setResourcetype("02");
				methodMenu.setSecuritypolicy("2");
				methodMenu.setIsleaf("0");
				methodMenu.setMenulevel(Long.valueOf(m.getMenulevel().longValue() + 1L));
				methodMenu.setSortno(Long.valueOf(i++));
				Menu createMethodMenu = menuDao.createMenu(methodMenu);
				String url = methodMenu.getUrl();
				if ((ValidateUtil.isNotEmpty(url)) && (url.endsWith(".do"))) {
					String turl = url.substring(0, url.length() - 3);
					methodMenu.setUrl(turl + "!" + method + ".do");
				}
				methodMenu.setMenuname(m.getMenuname() + "." + method);
				methodMenu.setPmenuid(m.getMenuid());
				methodMenu.setMenuidpath(m.getMenuidpath() + "/" + createMethodMenu.getMenuid());
				methodMenu.setMenunamepath(m.getMenunamepath() + "/" + createMethodMenu.getMenuname());
				menuDao.updateMenu(methodMenu);
				m.setIsleaf("1");
				menuDao.updateMenu(m);
			}
		}
	}

	public void unUserMenu(Long menuid) {
		if (menuid == null)
			throw new AppException("错误的菜单编号");
		menuDao.unUseMenu(menuid);
	}

	public void reUseMenu(Long menuid) {
		if (menuid == null)
			throw new AppException("错误的菜单编号");
		menuDao.reUseMenu(menuid);
	}

	public void removeMenu(Long menuid, ParamDTO dto) {
		if (menuid == null) {
			throw new AppException("错误的菜单编号");
		}

		Long positionid = dto.getUserInfo().getNowPosition().getPositionid();
		Long userid = dto.getUserInfo().getUserid();
		permissionService.clearUserPermissionMenusCache(userid);
		permissionService.clearUserPermissionMenusCache(userid, positionid);
		permissionService.clearUserPermissionUrlCache(userid);
		permissionService.clearUserPermissionUrlCache(userid, positionid);
		permissionService.clearPositionsByMenuCache(userid, menuid);
		permissionService.clearUserEffectivePositionsCache(userid);

		menuDao.removeMenu(menuid, dto.getUserInfo().getUserid());
		commonMenuService.deleteCommonMenu(dto.getUserInfo().getUserid(), menuid);
	}

	public IMenu getMenu(Long orgid) {
		return menuDao.getMenu(orgid);
	}

	public void setMenuDao(MenuDao menuDao) {
		this.menuDao = menuDao;
	}
}
