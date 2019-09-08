package com.yinhai.sysframework.menu;

import java.util.List;
import java.util.Set;

public interface IMenuService {

	 String SERVICEKEY = "menuService";

	 IMenu getMenu(Long paramLong);

	 Set<String> getAllMenusUrl();

	 void clearMenuCach();

	 List<IMenu> getEffectiveMenus(Long paramLong);

	 Set<String> getAllNeedCacheUrl();

	 IMenu getMenuByUrl(String paramString);
}
