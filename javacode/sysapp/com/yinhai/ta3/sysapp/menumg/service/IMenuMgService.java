package com.yinhai.ta3.sysapp.menumg.service;

import java.util.List;

import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public interface IMenuMgService {

	public static final String SERVICEKEY = "menuMgService";

	public abstract IMenu getMenu(Long paramLong);

	public abstract List<Menu> getAllMenuInfo();

	public abstract List<Menu> getChildMenus(Long paramLong);

	public abstract Menu createMenu(ParamDTO paramParamDTO);

	public abstract void sortMenu(Long[] paramArrayOfLong);

	public abstract void updateMenu(ParamDTO paramParamDTO);

	public abstract void unUserMenu(Long paramLong);

	public abstract void reUseMenu(Long paramLong);

	public abstract void removeMenu(Long paramLong, ParamDTO paramParamDTO);
}
