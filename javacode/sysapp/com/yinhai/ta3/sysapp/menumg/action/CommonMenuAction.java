package com.yinhai.ta3.sysapp.menumg.action;

import java.util.Iterator;
import java.util.List;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.config.IConfigSyspath;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.config.service.IConfigService;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.ConvertUtil;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.WebUtil;
import com.yinhai.ta3.sysapp.menumg.service.ICommonMenuService;
import com.yinhai.ta3.system.sysapp.domain.Menu;
import com.yinhai.webframework.BaseAction;

public class CommonMenuAction extends BaseAction {

	private ICommonMenuService commonMenuService = (ICommonMenuService) super.getService("commonMenuService");

	public String execute() throws Exception {
		List<Menu> list = commonMenuService.getCommonMenusByUserId(getDto().getUserInfo().getUserid());
		IConfigService configService = (IConfigService) ServiceLocator.getService("configService");
		List<IConfigSyspath> syslist = configService.getConfigSysPaths();
		boolean isPortal = SysConfig.getSysconfigToBoolean("isPortal", false);
		String curSyspath = "";
		if ((syslist != null) && (syslist.size() > 1) && (isPortal)) {
			for (Iterator i$ = syslist.iterator(); i$.hasNext();) {
				IConfigSyspath sysobj = (IConfigSyspath) i$.next();
				curSyspath = sysobj.getUrl();
				for (Menu m : list) {
					String url = m.getUrl();
					if ((sysobj.getId().equals(m.getSyspath())) && (ValidateUtil.isNotEmpty(url)) && (!url.contains("http"))) {
						m.setUrl(curSyspath + url);
					}
				}
			}
		}
		request.setAttribute("commonMenus", list);
		return super.execute();
	}

	public String toAddCommonMenus() throws Exception {
		List<IMenu> list = WebUtil.getCurrentUserPermissionMenus(request.getSession());
		List<Menu> clist = commonMenuService.getCommonMenusByUserId(getDto().getUserInfo().getUserid());
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < list.size(); i++) {
			IMenu menu = ConvertUtil.ObjectToMenu(list.get(i));
			sb.append("{\"menuid\":").append(menu.getMenuid()).append(",\"menuname\":\"").append(menu.getMenuname()).append("\"")
					.append(",\"pmenuid\":").append(menu.getPmenuid());

			if (ValidateUtil.isEmpty(menu.getUrl())) {
				sb.append(",\"nocheck\":true");
			}
			for (IMenu cmenu : clist) {
				if (menu.getMenuid().equals(cmenu.getMenuid())) {
					sb.append(",\"checked\":true");
					break;
				}
			}
			if (i < 50) {
				sb.append(",\"open\":true");
			}
			if (i == list.size() - 1) {
				sb.append("}");
			} else {
				sb.append("},");
			}
		}
		sb.append("]");
		request.setAttribute("commonMenuTree", sb.toString());
		return "toAddCommonMenus";
	}

	public String saveCommonMenu() throws Exception {
		commonMenuService.saveCommonMenu(getDto().getUserInfo().getUserid(), getDto().getAsLong("menuId"));
		return JSON;
	}

	public String saveCommonMenus() throws Exception {
		List<Key> list = getJsonParamAsList("commonmenus");
		commonMenuService.saveCommonMenus(getDto().getUserInfo().getUserid(), list);
		return JSON;
	}

	public String deleteCommonMenu() throws Exception {
		commonMenuService.deleteCommonMenu(getDto().getUserInfo().getUserid(), getDto().getAsLong("menuId"));
		return JSON;
	}

	public String deleteCommonMenus() throws Exception {
		List<Key> list = getJsonParamAsList("commonmenus");
		commonMenuService.deleteCommonMenus(getDto().getUserInfo().getUserid(), list);
		return JSON;
	}
}
