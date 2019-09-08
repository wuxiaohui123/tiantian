package com.yinhai.ta3.system.sysapp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;

import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.menu.IMenuService;
import com.yinhai.sysframework.util.StringUtil;
import com.yinhai.ta3.redis.annotation.LapseMethod;
import com.yinhai.ta3.system.sysapp.dao.MenuDao;

public class MenuService implements IMenuService {

	private MenuDao menuDao;

	public IMenu getMenu(Long orgid) {
		return menuDao.getMenu(orgid);
	}

	public void setMenuDao(MenuDao menuDao) {
		this.menuDao = menuDao;
	}

	@Cacheable(value = "menuCache")
	public Set<String> getAllMenusUrl() {
		List<IMenu> menus = getEffectiveMenus(null);
		Set<String> set = new HashSet<String>();
		for (IMenu menu : menus) {
			set.add(StringUtil.delUrlParam(menu.getUrl()));
		}
		return set;
	}

	@LapseMethod(name = "getAllMenusUrl")
	public void clearMenuCach() {
	}

	public List<IMenu> getEffectiveMenus(Long pmenuid) {
		if (pmenuid == null)
			return menuDao.getEffectiveMenus();
		return menuDao.getEffectiveMenus(pmenuid);
	}

	public Set<String> getAllNeedCacheUrl() {
		return menuDao.getAllNeedCacheUrl();
	}

	public IMenu getMenuByUrl(String url) {
		return menuDao.getMenuByUrl(url);
	}
}
