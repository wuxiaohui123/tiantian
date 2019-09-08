package com.yinhai.ta3.system.sysapp.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public interface MenuDao {

	public abstract IMenu getMenu(Long paramLong);

	public abstract List<IMenu> getUserPermissionMenus(Long paramLong, Date paramDate);

	public abstract List<IMenu> getEffectiveMenus();

	public abstract List<IMenu> getEffectiveMenus(Long paramLong);

	public abstract List<Menu> getAllMenuInfo();

	public abstract List<Menu> getChildMenus(Long paramLong);

	public abstract Menu createMenu(Menu paramMenu);

	public abstract void sortMenu(Long[] paramArrayOfLong);

	public abstract void updateMenu(Menu paramMenu);

	public abstract void unUseMenu(Long paramLong);

	public abstract void reUseMenu(Long paramLong);

	public abstract void removeMenu(Long paramLong);

	public abstract void removeMenu(Long paramLong1, Long paramLong2);

	public abstract Set<String> getAllNeedCacheUrl();

	public abstract List<IMenu> getUserPermissionMenus(Long paramLong1, Date paramDate, Long paramLong2);

	public abstract Set<String> getEffectiveUrls(Long paramLong1, Long paramLong2, Date paramDate);

	public abstract Set<String> getEffectiveUrls(Long paramLong, Date paramDate);

	public abstract IMenu getMenuByUrl(String paramString);
}
