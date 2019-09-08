package com.yinhai.ta3.organization.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.service.IOrgMgService;
import com.yinhai.ta3.organization.service.IPositionMgService;
import com.yinhai.ta3.organization.service.IYab003MgService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.UserInfoVO;

public class OrgMgAction extends OrgBaseAction {

	private IOrgMgService orgMgService = (IOrgMgService) ServiceLocator.getService("orgMgService");
	private IPositionMgService positionMgService = (IPositionMgService) ServiceLocator.getService("positionMgService");
	private IYab003MgService yab003MgService = (IYab003MgService) ServiceLocator.getService("yab003MgService");

	public String execute() throws Exception {
		request.setAttribute("orgTree", super.queryJsonOrgsTree(getDto()));
		String isOpenAgencies = SysConfig.getSysConfig("isOpenAgencies", "false");
		if ("true".equals(isOpenAgencies)) {
			setShowObj("yab003");
			setDisRequired("yab139");
		} else {
			setHideObj("yab003");
		}
		setSelectInputList("yab139", super.getCodeList("yab139", "9999"));
		return "success";
	}

	public String webGetOrgEditData() throws Exception {
		Org org = orgMgService.queryOrgNode(getDto());
		if (org == null) {
			setSuccess(false);
			setMsg("无编辑该部门的权限或部门不存在");
			return "tojson";
		}
		Map<String, Object> map = org.toMap();

		Long positionid = org.getOrgmanager();
		if (!ValidateUtil.isEmpty(positionid)) {
			Position position = positionMgService.getPosition(positionid);
			map.put("orgmanager_name", position.getPositionname());
		}

		setData("maxUdi", "0");
		String isOpenAgencies = SysConfig.getSysConfig("isOpenAgencies", "false");
		if ("true".equals(isOpenAgencies)) {
			String yab003 = (String) map.get("yab003");
			setSelectInputList("yab139", yab003MgService.queryCurYab139(getDto().getUserInfo().getNowPosition().getPositionid(), yab003));
		}
		setData(map, false);
		return "tojson";
	}

	public String webGetMaxCostomNo() throws Exception {
		String maxCostomNo = orgMgService.getMaxCostomNo(getDto().getAsLong("porgid"));
		setData("maxUdi", maxCostomNo);
		return "tojson";
	}

	public String webSaveOrg() throws Exception {
		Org org = orgMgService.createOrg(getDto());
		setData("orgid", org.getOrgid());
		Map<String, Object> map = org.toMap();
		map.put("admin", Boolean.valueOf(true));
		if ("01".equals(org.getOrgtype())) {
			map.put("iconSkin", "tree-depart-area");
		} else {
			map.put("iconSkin", "tree-depart-labor");
		}
		setData("childOrg", map);
		return "tojson";
	}

	public String webDeleteOrg() throws Exception {
		orgMgService.deleteOrg(getDto());
		return "tojson";
	}

	public String webUpdateOrg() throws Exception {
		orgMgService.updateOrg(getDto());
		setMsg("部门信息修改成功");
		return "tojson";
	}

	public String webUnEffectiveOrg() throws Exception {
		orgMgService.unUseOrg(getDto());
		return "tojson";
	}

	public String webSortOrg() throws Exception {
		List<Key> sortids = getJsonParamAsList("sortorgids");
		List<Long> sortidslong = new ArrayList();
		for (Key key : sortids) {
			sortidslong.add(key.getAsLong("orgid"));
		}
		orgMgService.sortOrg(sortidslong, getDto().getUserInfo().getUserid());
		return "tojson";
	}

	public String webSelectOrgManager() throws Exception {
		ParamDTO dto = getDto();
		String managerType = request.getParameter("managerType");
		Long chief = null;
		String deputies = "";
		if (ValidateUtil.isNotEmpty(request.getParameter("orgmanager"))) {
			chief = Long.valueOf(request.getParameter("orgmanager"));
			deputies = request.getParameter("orgmanager_deputy");
		}

		setData("managerType", managerType);
		setData("chief", chief);
		setData("deputies", deputies);
		request.setAttribute("orgTree", queryAllOrgTreeNodes(dto));

		return "selectOrgManager";
	}

	public String webGetUserInfo() throws Exception {
		List<UserInfoVO> list = orgMgService.getUserInfo(getDto());
		setList("grid1", list);
		return "tojson";
	}

	public String queryYab139ByYab003() throws Exception {
		String yab003 = getDto().getAsString("yab003");
		setSelectInputList("yab139", yab003MgService.queryCurYab139(getDto().getUserInfo().getNowPosition().getPositionid(), yab003));
		return "tojson";
	}
}
