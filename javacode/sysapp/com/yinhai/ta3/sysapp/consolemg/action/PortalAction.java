package com.yinhai.ta3.sysapp.consolemg.action;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.WebUtil;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.sysapp.consolemg.service.PortalService;
import com.yinhai.webframework.BaseAction;

public class PortalAction extends BaseAction {

	public String execute() throws Exception {
		return null;
	}

	public String getPortalInfo() throws Exception {
		ParamDTO dto = getDto();
		StringBuffer json = new StringBuffer("{");

		PortalService portalService = ServiceLocator.getService("portalService", PortalService.class);
		List lst = portalService.getUserModuleList(dto.getUserInfo().getUserid(), WebUtil.getCurrentUserPermissionUrls(request.getSession()));
		String locationInfo = portalService.getLocationInfo(request.getParameter("pageFlag"), dto.getUserInfo().getNowPosition().getPositionid());
		if (null != locationInfo) {
			json.append("\"location\":" + locationInfo + "");
		} else {
			json.append("\"location\":null");
		}
		json.append(",\"defaultItems\":" + JSonFactory.bean2json(lst));
		json.append("}");
		writeJsonToClient(json);
		return null;
	}

	protected HashSet<String> dearUrl(List<Map> list) {
		HashSet<String> set = new HashSet<String>();
		for (Map menu : list) {
			if ((menu.get("module_url") != null) && (!"".equals(menu.get("module_url")))) {
				String url = menu.get("module_url") + "";
				int b = url.indexOf("?");
				if (b > -1) {
					url = url.substring(0, b);
				}
				set.add("/" + url);
			}
		}
		return set;
	}

	public String savePortalInfo() throws Exception {
		ParamDTO dto = getDto();
		PortalService portalService = ServiceLocator.getService("portalService", PortalService.class);
		portalService.saveLocationInfo(dto.getAsString("location"), dto.getAsString("pageFlag"), dto.getUserInfo().getNowPosition().getPositionid());
		return null;
	}
}
