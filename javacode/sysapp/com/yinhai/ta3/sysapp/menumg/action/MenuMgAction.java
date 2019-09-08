package com.yinhai.ta3.sysapp.menumg.action;

import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.config.IConfigSyspath;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.config.service.IConfigService;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.sysapp.menumg.service.IMenuMgService;
import com.yinhai.ta3.system.sysapp.domain.Menu;
import com.yinhai.webframework.BaseAction;

public class MenuMgAction extends BaseAction {

	IMenuMgService menuMgService = (IMenuMgService) getService("menuMgService");

	public String execute() throws Exception {
		Menu menu = (Menu) menuMgService.getMenu(IMenu.ROOT_ID);
		menu.setParent(true);
		Map<String, Object> map = menu.toMap();
		map.put("open", "true");
		request.setAttribute("menuTree", JSonFactory.bean2json(map));

		boolean isPortal = SysConfig.getSysconfigToBoolean("isPortal", false);
		IConfigService configService = (IConfigService) ServiceLocator.getService("configService");
		List<IConfigSyspath> syslist = configService.getConfigSysPaths();
		setData("syspath", syslist);
		setData("syspath", SysConfig.getSysConfig("curSyspathId", "sysmg"));
		if (!isPortal) {
			setHideObj("syspath");
		}
		String isaudite = SysConfig.getSysConfig("isAudite", "false");
		if (!"true".equals(isaudite)) {
			setHideObj("isaudite");
		}
		return super.execute();
	}

	public String webGetSyncMenu() throws Exception {
		return "";
	}

	public String webGetAsyncMenu() throws Exception {
		Long menuid = Long.valueOf(request.getParameter("menuid"));
		List<Menu> childMenus = menuMgService.getChildMenus(menuid);
		for (Menu menu : childMenus) {
			if ("1".equals(menu.getIsleaf())) {
				menu.setParent(true);
			}
		}
		writeJsonToClient(childMenus);
		return null;
	}

	public String webSortMenus() throws Exception {
		List<Key> menuidKey = getJsonParamAsList("sortMenuids");
		Long[] menuids = new Long[menuidKey.size()];
		for (int i = 0; i < menuidKey.size(); i++) {
			menuids[i] = Long.valueOf(((Key) menuidKey.get(i)).getAsLong("menuid") == null ? 999L : ((Key) menuidKey.get(i)).getAsLong("menuid")
					.longValue());
		}
		menuMgService.sortMenu(menuids);
		return "tojson";
	}

	public String webSaveMenu() throws Exception {
		menuMgService.createMenu(getDto());
		return "tojson";
	}

	public String webUpdateMenu() throws Exception {
		menuMgService.updateMenu(getDto());
		return "tojson";
	}

	public String webGetMenu() throws Exception {
		Long menuid = getDto().getAsLong("menuid");
		Menu menu = (Menu) menuMgService.getMenu(menuid);
		setData(menu.toMap(), true);
		return "tojson";
	}

	public String webUnUseMenu() throws Exception {
		Long menuid = getDto().getAsLong("menuid");
		if (menuid == null) {
			setSuccess(false);
		} else
			menuMgService.unUserMenu(menuid);
		return "tojson";
	}

	public String webDeleteMenu() throws Exception {
		Long menuid = getDto().getAsLong("menuid");
		if (menuid == null) {
			setSuccess(false);
		} else
			menuMgService.removeMenu(menuid, getDto());
		return "tojson";
	}
}
