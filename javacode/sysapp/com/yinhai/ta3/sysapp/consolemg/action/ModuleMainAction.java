package com.yinhai.ta3.sysapp.consolemg.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.organization.service.IOrgMgService;
import com.yinhai.ta3.sysapp.consolemg.service.ModuleMainService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.webframework.BaseAction;

public class ModuleMainAction extends BaseAction {

	ModuleMainService moduleMainService = (ModuleMainService) super.getService("moduleMainService");

	IOrgMgService orgMgService = (IOrgMgService) super.getService("orgMgService");

	public String execute() throws Exception {
		List lst = moduleMainService.getModuleList(getDto());
		setList("moduleList", lst);
		return "init";
	}

	public String query() throws Exception {
		List lst = moduleMainService.getModuleList(getDto());
		setList("moduleList", lst);
		return JSON;
	}

	public String toAdd() throws Exception {
		request.setAttribute("addFlag", Boolean.valueOf(true));
		return "edit";
	}

	public String toUpdate() throws Exception {
		request.setAttribute("addFlag", Boolean.valueOf(false));
		ParamDTO dto = getDto();
		Map m = moduleMainService.getModuleItem(dto);
		setData(m, true);
		return "edit";
	}

	public String doAdd() throws Exception {
		request.setAttribute("addFlag", Boolean.valueOf(true));
		ParamDTO dto = getDto();
		moduleMainService.addModuleItem(dto);
		return JSON;
	}

	public String doUpdate() throws Exception {
		request.setAttribute("addFlag", Boolean.valueOf(false));
		try {
			ParamDTO dto = getDto();
			if ("1".equals(dto.getAsString("isDelete"))) {
				ParamDTO m = new ParamDTO();
				m.put("moduleId", dto.getAsString("moduleId"));
				m.put("sta", "0");
				if (moduleMainService.updateModuleItem(m) != 1) {
					throw new AppException("删除失败!");
				}
			} else if (moduleMainService.updateModuleItem(dto) != 1) {
				throw new AppException("修改失败!");
			}
		} catch (Exception e) {
			throw new AppException("操作失败，原因：</br>" + e.getMessage());
		}
		return JSON;
	}

	public String toGrant() throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		Long rootId = Org.ORG_ROOT_ID;

		getDto().append("orgid", rootId);
		Org org = orgMgService.queryOrgNode(getDto());
		sb.append("{orgid:'").append(org.getOrgid()).append("',porgid:'").append(org.getPorgid()).append("',orgname:'").append(org.getOrgname())
				.append("',departType:'").append(org.getOrgtype()).append("',iconSkin:'")
				.append(org.getOrgtype().equals("02") ? "tree-depart-area" : org.getOrgtype().equals("01") ? "tree-depart-labor" : "")
				.append("',isParent:true}");

		sb.append("]");

		request.setAttribute("deptForRoleScope", sb);

		return "grant";
	}

	public String doGrant() throws Exception {
		return "grant";
	}

	public String toGrantWin() throws Exception {
		ParamDTO dto = getDto();
		List lst = moduleMainService.getModuleList(dto);
		setList("moduleList", lst);

		setData("roleId", dto.getAsLong("roleId"));
		List grantLst = moduleMainService.getGrantList(dto);
		request.setAttribute("selectedItems", JSonFactory.bean2json(grantLst));
		return "grantWin";
	}

	public String saveGrant() throws Exception {
		try {
			List lst = super.getSelected("moduleList");
			ParamDTO dto = getDto();
			moduleMainService.saveGrant(dto, lst);
		} catch (Exception e) {
			throw new AppException("授权失败，原因：<br/>" + e.getMessage());
		}

		return JSON;
	}

	public String webQueryAsyncOrgTree() throws Exception {
		getDto().append("orgid", request.getParameter("orgid"));

		String orgs = queryAsyncOrgTreeNodes(getDto());
		writeJsonToClient(orgs);
		return null;
	}

	private String queryAsyncOrgTreeNodes(ParamDTO dto) throws Exception {
		Long porgid = dto.getAsLong("orgid");

		List<Org> orgs = orgMgService.querySubOrgs(porgid, true, false, "-1");
		Long positionid = getDto().getUserInfo().getNowPosition().getPositionid();
		List<Long> opsitionids = orgMgService.queryPositionCouldManageOrgIds(positionid);
		List<Map<String, Object>> lOrgs = buildOrgTreeJSON(orgs, opsitionids, positionid.equals(Position.ADMIN_POSITIONID));
		return JSonFactory.bean2json(lOrgs);
	}

	private List<Map<String, Object>> buildOrgTreeJSON(List<Org> orgs, List<Long> opsitionids, boolean isDeveloper) {
		List<Map<String, Object>> lOrgs = new ArrayList();
		for (Org org : orgs) {
			Map<String, Object> mOrg = org.toMap();
			if ("01".equals(org.getOrgtype())) {
				mOrg.put("iconSkin", "tree-depart-area");
			} else {
				mOrg.put("iconSkin", "tree-depart-labor");
			}
			if ((org.getOrglevel() != null) && (org.getOrglevel().longValue() < 3L) && ("1".equals(org.getIsleaf()))) {
				mOrg.put("open", "true");
			}
			if (isDeveloper) {
				mOrg.put("admin", Boolean.valueOf(true));
				lOrgs.add(mOrg);
			} else {
				for (Long orgid : opsitionids) {
					if (orgid.equals(org.getOrgid())) {
						mOrg.put("admin", Boolean.valueOf(true));
					}
				}
				lOrgs.add(mOrg);
			}
		}
		return lOrgs;
	}

	public String getAllPositions() throws Exception {
		List<Position> list = moduleMainService.getAllPositions(getDto());
		setList("positionGrid", list);
		return JSON;
	}
}
