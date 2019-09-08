package com.yinhai.ta3.organization.action;

import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.organization.service.IInnerControlService;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class InnerControlAction extends OrgBaseAction {

	private IInnerControlService innerControlService = (IInnerControlService) super.getService("innerControlService");

	public String execute() throws Exception {
		Menu menu = innerControlService.getMenu(IMenu.ROOT_ID);
		menu.setParent(true);
		Map<String, Object> map = menu.toMap();
		map.put("open", "true");
		request.setAttribute("menuTree", JSonFactory.bean2json(map));
		return super.execute();
	}

	public String queryMenus() throws Exception {
		Long menuid = Long.valueOf(request.getParameter("menuid"));
		List<Menu> childMenus = innerControlService.getChildMenus(menuid);
		for (Menu menu : childMenus) {
			menu.setUrl("");
			if ("1".equals(menu.getIsleaf())) {
				menu.setParent(true);
			}
		}
		writeJsonToClient(childMenus);
		return null;
	}

	public String adminPop() throws Exception {
		return "adminPop";
	}

	public String positionPop() throws Exception {
		return "positionPop";
	}

	public String businessPop() throws Exception {
		return "businessPop";
	}

	public String menuPop() throws Exception {
		return "menuPop";
	}

	public String queryAdminByOrgId() throws Exception {
		ParamDTO dto = getDto();
		Long orgid = dto.getAsLong("orgid");
		String isShowSubOrg = dto.getAsString("isShowSubOrg");
		Long positionid = dto.getUserInfo().getNowPosition().getPositionid();
		List list = innerControlService.queryAdminByOrgId(orgid, isShowSubOrg, positionid);
		setList("adminGrid", list);
		return "tojson";
	}

	public String queryPositionByOrgId() throws Exception {
		ParamDTO dto = getDto();
		Long orgid = dto.getAsLong("orgid");
		String isShowSubOrg = dto.getAsString("isShowSubOrg");
		Long positionid = dto.getUserInfo().getNowPosition().getPositionid();
		List list = innerControlService.queryPositionByOrgId(orgid, isShowSubOrg, positionid);
		setList("positionGrid", list);
		return "tojson";
	}

	public String queryLogByAdmin() throws Exception {
		List list = innerControlService.queryLogByAdmin(getDto());
		setList("grid1", list);
		return "tojson";
	}

	public String queryBusinessByOrgId() throws Exception {
		ParamDTO dto = getDto();
		Long orgid = dto.getAsLong("orgid");
		String isShowSubOrg = dto.getAsString("isShowSubOrg");
		Long positionid = dto.getUserInfo().getNowPosition().getPositionid();
		List list = innerControlService.queryBusinessByOrgId(orgid, isShowSubOrg, positionid);
		setList("businessGrid", list);
		return "tojson";
	}

	public String queryLogByBusiness() throws Exception {
		List list = innerControlService.queryLogByBusiness(getDto());
		setList("grid2", list);
		return "tojson";
	}

	public String queryLogByMenu() throws Exception {
		List list = innerControlService.queryLogByMenu(getDto());
		setList("grid3", list);
		return "tojson";
	}
}
