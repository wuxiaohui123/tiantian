package com.yinhai.ta3.organization.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IDataAccessApi;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.organization.api.OrganizationEntityService;
import com.yinhai.ta3.organization.service.IAdminMgService;
import com.yinhai.ta3.organization.service.IDataAccessDimensionManagementService;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class DataAccessDimensionManagementAction extends OrgBaseAction {

	OrganizationEntityService organizationEntityService = (OrganizationEntityService) getService("organizationEntityService");
	IDataAccessDimensionManagementService dataAccessDimensionManagementService = (IDataAccessDimensionManagementService) getService("dataAccessDimensionManagementService");
	IAdminMgService adminMgService = (IAdminMgService) super.getService("adminMgService");
	IDataAccessApi api = (IDataAccessApi) super.getService("dataAccessApi");

	public String execute() throws Exception {
		Menu menu = (Menu) dataAccessDimensionManagementService.getMenu(IMenu.ROOT_ID);
		menu.setParent(true);
		Map<String, Object> map = menu.toMap();
		map.put("open", "true");
		List list = new ArrayList();
		list.add(map);

		Menu lmenu = (Menu) dataAccessDimensionManagementService.getMenu(Long.valueOf(99L));
		if (!ValidateUtil.isEmpty(lmenu)) {
			lmenu.setParent(true);
			Map<String, Object> lmap = lmenu.toMap();
			lmap.put("open", "true");
			list.add(lmap);
		}
		request.setAttribute("menuTree", JSonFactory.bean2json(list));
		return super.execute();
	}

	public String webGetAsyncMenu() throws Exception {
		Long menuid = Long.valueOf(request.getParameter("menuid"));
		List<Menu> childMenus = dataAccessDimensionManagementService.getChildMenus(menuid);
		for (Menu menu : childMenus) {
			if ("1".equals(menu.getIsleaf())) {
				menu.setParent(true);
			}
		}
		writeJsonToClient(childMenus);
		return null;
	}

	public String queryPositionsByMenuId() throws Exception {
		List<IPosition> list = organizationEntityService.getPositionsByMenuId(getDto().getAsLong("menuid"));
		setList("positionGrid", list);
		return "tojson";
	}

	public String toDimension() throws Exception {
		ParamDTO dto = getDto();
		Long menuid = dto.getAsLong("menuid");
		Long positionid = dto.getAsLong("positionid");
		String dimensiontype = dto.getAsString("dimensiontype");
		if ((ValidateUtil.isEmpty(menuid)) || (ValidateUtil.isEmpty(positionid))) {
			throw new AppException("菜单id或�?岗位id为空");
		}
		if (ValidateUtil.isEmpty(dimensiontype)) {
			dimensiontype = "YAB139";
		}
		List<AppCode> list1 = api.query(menuid, positionid, dimensiontype);
		boolean check = dataAccessDimensionManagementService.checkAllAccess(menuid, positionid, dimensiontype);
		if (check) {
			setData("allAccess", "allAccess");
		}
		setList("yab003Grid2", list1);
		List<Map<String, String>> list = dataAccessDimensionManagementService.queryAdminYab139ScopeNoSelected(getDto().getUserInfo().getUserid(),
				getDto().getUserInfo().getNowPosition().getPositionid(), list1, positionid);
		setList("yab003Grid", list);
		setData("p_isdeveloper", "true");
		if (!IPosition.ADMIN_POSITIONID.equals(getDto().getUserInfo().getNowPosition().getPositionid())) {
			setHideObj("allAccess");
		}
		setData("menuid", menuid);
		setData("positionid", positionid);
		return "toDimension";
	}

	public String toDimension1() throws Exception {
		List<Map<String, String>> list = adminMgService.queryAdminYab139Scope(getDto().getUserInfo().getUserid(), getDto().getUserInfo()
				.getNowPosition().getPositionid());
		setList("yab003Grid", list);
		setData("p_isdeveloper", "true");
		if (!IPosition.ADMIN_POSITIONID.equals(getDto().getUserInfo().getNowPosition().getPositionid())) {
			setData("p_isdeveloper", "false");
		}

		return "toDimension1";
	}

	public String toDetachDimension() throws Exception {
		List<AppCode> list = getCodeList("yab003", getDto().getUserInfo().getYab003());
		setList("yab003Grid", list);
		setShowObj("btnDetachSave");
		setHideObj("btnSave");
		return "toDimension";
	}

	public String save() throws Exception {
		List<Key> list = getSelected("yab003Grid");
		ParamDTO dto = getDto();
		Long menuid = dto.getAsLong("menuid");
		Long positionid = dto.getAsLong("positionid");
		String dimensiontype = dto.getAsString("dimensiontype");
		String allaccess = dto.getAsString("allAccess");
		if ((ValidateUtil.isEmpty(menuid)) || (ValidateUtil.isEmpty(positionid))) {
			throw new AppException("菜单id或�?岗位id为空");
		}
		if (ValidateUtil.isEmpty(dimensiontype)) {
			dimensiontype = "YAB139";
		}
		dataAccessDimensionManagementService.save(menuid, positionid, dimensiontype, allaccess, list);
		api.clearCache(menuid, positionid, dimensiontype);
		return "tojson";
	}

	public String saveAll() throws Exception {
		ParamDTO dto = getDto();
		Long menuid = dto.getAsLong("menuid");
		Long positionid = dto.getAsLong("positionid");
		String dimensiontype = dto.getAsString("dimensiontype");
		String allaccess = dto.getAsString("allAccess");
		if ((ValidateUtil.isEmpty(menuid)) || (ValidateUtil.isEmpty(positionid))) {
			throw new AppException("菜单id或�?岗位id为空");
		}
		if (ValidateUtil.isEmpty(dimensiontype)) {
			dimensiontype = "YAB139";
		}
		dataAccessDimensionManagementService.saveAll(menuid, positionid, dimensiontype, allaccess);
		api.clearCache(menuid, positionid, dimensiontype);
		return "tojson";
	}

	public String removeYab139() throws Exception {
		List<Key> list = getSelected("yab003Grid2");
		ParamDTO dto = getDto();
		Long menuid = dto.getAsLong("menuid");
		Long positionid = dto.getAsLong("positionid");
		String dimensiontype = dto.getAsString("dimensiontype");
		if ((ValidateUtil.isEmpty(menuid)) || (ValidateUtil.isEmpty(positionid))) {
			throw new AppException("菜单id或�?岗位id为空");
		}
		if (ValidateUtil.isEmpty(dimensiontype)) {
			dimensiontype = "YAB139";
		}
		dataAccessDimensionManagementService.removeYab139(menuid, positionid, dimensiontype, list);
		api.clearCache(menuid, positionid, dimensiontype);
		return "tojson";
	}

	public String saveDetach() throws Exception {
		List<Key> list = getSelected("yab003Grid");
		ParamDTO dto = getDto();
		Long menuid = dto.getAsLong("menuid");
		String positionids = dto.getAsString("positionids");
		String dimensiontype = dto.getAsString("dimensiontype");
		String allaccess = dto.getAsString("allAccess");
		if ((ValidateUtil.isEmpty(menuid)) || (ValidateUtil.isEmpty(positionids))) {
			throw new AppException("菜单id或�?岗位id为空");
		}
		if (ValidateUtil.isEmpty(dimensiontype)) {
			dimensiontype = "YAB139";
		}
		String[] plist = positionids.split(",");
		for (String p : plist) {
			dataAccessDimensionManagementService.save(menuid, Long.valueOf(p), dimensiontype, allaccess, list);
			api.clearCache(menuid, Long.valueOf(p), dimensiontype);
		}
		return "tojson";
	}

	public String query() throws Exception {
		ParamDTO dto = getDto();
		Long menuid = dto.getAsLong("menuid");
		Long positionid = dto.getAsLong("positionid");
		String dimensiontype = dto.getAsString("dimensiontype");
		if ((ValidateUtil.isEmpty(menuid)) || (ValidateUtil.isEmpty(positionid))) {
			throw new AppException("菜单id或�?岗位id为空");
		}
		if (ValidateUtil.isEmpty(dimensiontype)) {
			dimensiontype = "YAB139";
		}
		List<AppCode> list = api.query(menuid, positionid, dimensiontype);
		setData("tempList", list);
		setData("isdeveloper", "true");
		if (!IPosition.ADMIN_POSITIONID.equals(getDto().getUserInfo().getNowPosition().getPositionid())) {
			setData("isdeveloper", "false");
		}
		return "tojson";
	}

	public String queryPos() throws Exception {
		PageBean pb = dataAccessDimensionManagementService.queryPos(getDto());
		setList("posGrid", pb);
		return "tojson";
	}

	public String queryTree() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		List<PermissionTreeVO> list = dataAccessDimensionManagementService.queryTrree(positionid);
		setData("treeData", JSonFactory.bean2json(list));
		return "tojson";
	}

	public String saveAccess() throws Exception {
		ParamDTO dto = getDto();
		List<Key> menulist = getJsonParamAsList("menustr");
		Long positionid = dto.getAsLong("positionid");
		List<Key> yab139list = getSelected("yab003Grid");
		String dimensiontype = dto.getAsString("dimensiontype");
		String allaccess = dto.getAsString("allAccess");
		if ((ValidateUtil.isEmpty(menulist)) || (ValidateUtil.isEmpty(positionid))) {
			throw new AppException("未勾选功能菜单或者岗位id为空");
		}
		if (ValidateUtil.isEmpty(dimensiontype)) {
			dimensiontype = "YAB139";
		}
		dataAccessDimensionManagementService.saveAccess(positionid, dimensiontype, allaccess, menulist, yab139list);
		for (Key key : menulist) {
			Long menuid = key.getAsLong("id");
			api.clearCache(menuid, positionid, dimensiontype);
		}
		return "tojson";
	}
}
