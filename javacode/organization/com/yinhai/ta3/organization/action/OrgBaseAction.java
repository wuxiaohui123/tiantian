package com.yinhai.ta3.organization.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.organization.service.IOrgMgService;
import com.yinhai.ta3.organization.service.IPositionMgService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.webframework.BaseAction;

public class OrgBaseAction extends BaseAction {

	private IOrgMgService orgMgService = (IOrgMgService) ServiceLocator.getService("orgMgService");
	private IPositionMgService positionMgService = (IPositionMgService) ServiceLocator.getService("positionMgService");

	public String queryJsonOrgsTree(ParamDTO dto) throws Exception {
		boolean lazy = SysConfig.getSysconfigToBoolean("lazyLoadOrgTree", true);
		String treeJson = "[]";
		if (!lazy) {
			treeJson = queryAllOrgTreeNodes(dto);
		}
		return treeJson;
	}

	public String webQueryAsyncOrgTree() throws Exception {
		String treeJson = "";
		if (ValidateUtil.isEmpty(request.getParameter("orgid"))) {
			List<Org> orgs = orgMgService.querySubOrgs(Org.ORG_ROOT_ID, false, true, "-1");
			Long positionid = getDto().getUserInfo().getNowPosition().getPositionid();
			List<Long> opsitionids = orgMgService.queryPositionCouldManageOrgIds(positionid);
			List<Map<String, Object>> lOrgs = buildOrgTreeJSON(orgs, opsitionids, positionid.equals(Position.ADMIN_POSITIONID));
			treeJson = JSonFactory.bean2json(lOrgs);
		} else {
			getDto().append("orgid", request.getParameter("orgid"));
			treeJson = queryAsyncOrgTreeNodes(getDto());
		}

		writeJsonToClient(treeJson);
		return null;
	}

	protected String queryAllOrgTreeNodes(ParamDTO dto) throws Exception {
		List<Org> orgs = orgMgService.querySubOrgs(Org.ORG_ROOT_ID, true, true, "-1");
		Long positionid = getDto().getUserInfo().getNowPosition().getPositionid();
		List<Long> orgids = orgMgService.queryPositionCouldManageOrgIds(positionid);
		List<Map<String, Object>> lOrgs = buildOrgTreeJSON(orgs, orgids, positionid.equals(Position.ADMIN_POSITIONID));
		return JSonFactory.bean2json(lOrgs);
	}

	protected String queryAsyncOrgTreeNodes(ParamDTO dto) throws Exception {
		Long porgid = dto.getAsLong("orgid");

		List<Org> orgs = orgMgService.querySubOrgs(porgid, false, false, "-1");
		Long positionid = getDto().getUserInfo().getNowPosition().getPositionid();
		List<Long> opsitionids = orgMgService.queryPositionCouldManageOrgIds(positionid);
		List<Map<String, Object>> lOrgs = buildOrgTreeJSON(orgs, opsitionids, positionid.equals(Position.ADMIN_POSITIONID));
		return JSonFactory.bean2json(lOrgs);
	}

	private List<Map<String, Object>> buildOrgTreeJSON(List<Org> orgs, List<Long> orgids, boolean isDeveloper) {
		List<Map<String, Object>> lOrgs = new ArrayList();
		for (Org org : orgs) {
			Map<String, Object> mOrg = org.toMap();
			mOrg.put("isParent", "true");
			if ("01".equals(org.getOrgtype())) {
				mOrg.put("iconSkin", "tree-depart-area");
			} else {
				mOrg.put("iconSkin", "tree-depart-labor");
			}
			if ("0".equals(org.getIsleaf())) {
				mOrg.put("isParent", "false");
			}

			if ((org.getOrglevel() != null) && (org.getOrglevel().longValue() < 1L) && ("1".equals(org.getIsleaf()))) {
				mOrg.put("open", "true");
			}
			if (isDeveloper) {
				mOrg.put("admin", Boolean.valueOf(true));
				lOrgs.add(mOrg);
			} else {
				for (int i = 0; i < orgids.size(); i++) {
					Long orgid = (Long) orgids.get(i);
					if (orgid.equals(org.getOrgid())) {
						mOrg.put("admin", Boolean.valueOf(true));
						break;
					}
					if (i == orgids.size() - 1) {
						mOrg.put("nocheck", Boolean.valueOf(true));
					}
				}
				lOrgs.add(mOrg);
			}
		}
		return lOrgs;
	}

	protected String buildRecyleTree(List<PermissionTreeVO> nodes) {
		List<PermissionTreeVO> newNodes = new ArrayList();
		PermissionTreeVO newNode = null;
		for (int i = 0; i < nodes.size(); i++) {
			newNode = (PermissionTreeVO) nodes.get(i);
			if (i < 50) {
				newNode.setOpen(true);
			}
			if (("4".equals(newNode.getPolicy())) || ("3".equals(newNode.getPolicy()))) {
				newNode.setNocheck(true);
			}
			newNodes.add(newNode);
		}
		return JSonFactory.bean2json(newNodes);
	}

	public String toSetEffectiveTime() throws Exception {
		setShowObj("btnSave");
		setHideObj("btnBatchSave,btnPositionsBatchSave,btnPositionsSave");
		String menuid = request.getParameter("menuid");
		String positionid = request.getParameter("positionid");
		if (ValidateUtil.isEmpty(menuid)) {
			throw new AppException("菜单id为空，不能设置权限有效时间");
		}
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("岗位id为空，不能设置权限有效时间");
		}
		Date effectiveTime = positionMgService.queryEffectiveTime(Long.valueOf(menuid), Long.valueOf(positionid));
		String positiontype = request.getParameter("positiontype");
		setData("p_menuid", menuid);
		setData("p_positionid", positionid);
		setData("p_positionType", positiontype);
		setData("effectiveTime", effectiveTime);
		return "setEffectiveTime";
	}

	public String saveEffectiveTimePanel() throws Exception {
		positionMgService.saveEffectiveTimePanel(reBuildDto("p_", getDto()));
		return "tojson";
	}

	public String toBatchSetEffectiveTime() throws Exception {
		setHideObj("btnSave,btnPositionsBatchSave,btnPositionsSave");
		setShowObj("btnBatchSave");
		String positionid = request.getParameter("positionid");
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("岗位id为空，不能设置权限有效时间");
		}
		String positiontype = request.getParameter("positiontype");
		setData("p_positionid", positionid);
		setData("p_positionType", positiontype);
		return "setEffectiveTime";
	}

	public String batchSaveEffectiveTimePanel() throws Exception {
		List<Key> list = getJsonParamAsList("menuids");
		ParamDTO dto = reBuildDto("p_", getDto());
		for (Key key : list) {
			dto.put("menuid", key.getAsLong("menuid"));
			positionMgService.saveEffectiveTimePanel(dto);
		}
		return "tojson";
	}

	public String toPositionsSetEffectiveTime() throws Exception {
		setHideObj("btnBatchSave,btnSave,btnPositionsBatchSave");
		setShowObj("btnPositionsSave");
		String menuid = request.getParameter("menuid");
		String positionids = request.getParameter("positionids");
		if (ValidateUtil.isEmpty(menuid)) {
			throw new AppException("菜单id为空，不能设置权限有效时间");
		}
		if (ValidateUtil.isEmpty(positionids)) {
			throw new AppException("岗位id为空，不能设置权限有效时间");
		}
		String positiontype = request.getParameter("positiontype");
		setData("p_menuid", menuid);
		setData("positionids", positionids);
		setData("p_positionType", positiontype);
		return "setEffectiveTime";
	}

	public String savePositionsEffectiveTimePanel() throws Exception {
		List<Key> list = getJsonParamAsList("positionids");
		ParamDTO dto = reBuildDto("p_", getDto());
		for (Key key : list) {
			dto.put("positionid", key.getAsLong("positionid"));
			positionMgService.saveEffectiveTimePanel(dto);
		}
		return "tojson";
	}

	public String toPositionsBatchSetEffectiveTime() throws Exception {
		setHideObj("btnBatchSave,btnSave,btnPositionsSave");
		setShowObj("btnPositionsBatchSave");
		String positionids = request.getParameter("positionids");
		if (ValidateUtil.isEmpty(positionids)) {
			throw new AppException("岗位id为空，不能设置权限有效时间");
		}
		String positiontype = request.getParameter("positiontype");
		setData("p_positionType", positiontype);
		setData("positionids", positionids);
		return "setEffectiveTime";
	}

	public String savePositionsBatchEffectiveTimePanel() throws Exception {
		List<Key> positionids = getJsonParamAsList("positionids");
		List<Key> menuids = getJsonParamAsList("menuids");
		ParamDTO dto = reBuildDto("p_", getDto());
		for (Key key : positionids) {
			dto.put("positionid", key.getAsLong("positionid"));
			for (Key key1 : menuids) {
				dto.put("menuid", key1.getAsLong("menuid"));
				positionMgService.saveEffectiveTimePanel(dto);
			}
		}
		return "tojson";
	}
}
