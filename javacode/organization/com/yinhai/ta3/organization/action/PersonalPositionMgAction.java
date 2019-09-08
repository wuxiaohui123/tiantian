package com.yinhai.ta3.organization.action;

import java.util.Iterator;
import java.util.List;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.organization.service.IPositionMgService;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;

public class PersonalPositionMgAction extends OrgBaseAction {

	private IPositionMgService positionMgService = (IPositionMgService) super.getService("positionMgService");

	public String execute() throws Exception {
		request.setAttribute("orgTree", super.queryJsonOrgsTree(getDto()));
		return "success";
	}

	public String queryPositions() throws Exception {
		ParamDTO dto = getDto();
		dto.put("positionType", "2");
		PageBean pb = positionMgService.getDescendantsPositionsByCount("positionPersonalGrid", getDto());
		setList("positionPersonalGrid", pb);
		return "tojson";
	}

	public String toClonePermissions() throws Exception {
		return "toClonePermissions";
	}

	public String clonePermissions() throws Exception {
		String positionids = getDto().getAsString("positionids");
		String[] poids = null;

		if (ValidateUtil.isNotEmpty(positionids)) {
			poids = positionids.split(",");
		}

		Long positionid = getDto().getAsLong("positionid");
		positionMgService.clonePermissions(positionid, poids, getDto().getUserInfo());
		return "tojson";
	}

	public String queryPositionsClone() throws Exception {
		ParamDTO dto = getDto();
		dto.put("positionType", "2");
		PageBean pb = positionMgService.getDescendantsPositionsByCount("positionPersonalGrid", getDto());
		setList("cloneGrid", pb);
		return "tojson";
	}

	public String toFuncOpPurview() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		setData("usePermissionPositionid", positionid);
		setData("positionType", getDto().getAsString("positionType"));
		setData("orgnamepath", getDto().getAsString("orgnamepath"));

		List<PermissionTreeVO> nodes = positionMgService.getUsePermissionTreeByPositionId(positionid);

		Long curPositionid = getDto().getUserInfo().getNowPosition().getPositionid();
		List<PermissionTreeVO> curNodes = positionMgService.getRePermissionTreeByPositionId(curPositionid);

		List<PermissionTreeVO> newNodes = new java.util.ArrayList();
		PermissionTreeVO newNode = null;
		for (int i = 0; i < curNodes.size(); i++) {
			newNode = (PermissionTreeVO) curNodes.get(i);
			Iterator localIterator = nodes.iterator();
			while (localIterator.hasNext()) {
				PermissionTreeVO node = (PermissionTreeVO) localIterator.next();
				if ((newNode.getId().longValue() == node.getId().longValue()) && (newNode.getPId().equals(node.getPId()))) {
					newNode.setChecked(true);
					if (node.isChkDisabled()) {
						newNode.setChkDisabled(true);
					}
					if (!node.isEffectivetimeover())
						break;
					newNode.setEffectivetimeover(true);
				}
			}

			if (i < 50) {
				newNode.setOpen(true);
			}
			if (("4".equals(newNode.getPolicy())) || ("3".equals(newNode.getPolicy()))) {
				newNode.setNocheck(true);
			}
			newNodes.add(newNode);
		}
		request.setAttribute("opTree", JSonFactory.bean2json(newNodes));
		return "toFuncOpPurview";
	}

	public String toRecyclePermissions() throws Exception {
		String positionids = request.getParameter("positionids");
		setData("recyclePermissionsPositionids", positionids);
		setData("positionType", getDto().getAsString("positionType"));

		List<PermissionTreeVO> nodes = positionMgService.getRePermissionTreeByPositionId(getDto().getUserInfo().getNowPosition().getPositionid());

		request.setAttribute("opTree", buildRecyleTree(nodes));
		setShowObj("personalrecyclePermissionsOpBtn");
		setHideObj("recyclePermissionsOpBtn");
		return "recyclePermissions";
	}

	public String toGrantUsePermissions() throws Exception {
		String positionids = request.getParameter("positionids");
		setData("grantPermissionsPositionids", positionids);
		setData("positionType", getDto().getAsString("positionType"));

		List<PermissionTreeVO> nodes = positionMgService.getRePermissionTreeByPositionId(getDto().getUserInfo().getNowPosition().getPositionid());

		request.setAttribute("opTree", buildRecyleTree(nodes));
		setShowObj("personalrecyclePermissionsOpBtn");
		setHideObj("recyclePermissionsOpBtn");
		return "grantUsePermissions";
	}

	public String saveRoleScopeAclOperate() throws Exception {
		List<Key> list = getJsonParamAsList("ids");

		positionMgService.saveRoleScopeAclOperate(null, list, getDto());
		return "tojson";
	}

	public String recyclePermissions() throws Exception {
		List<Key> permissionsList = getJsonParamAsList("ids");

		List<Key> positionsList = getJsonParamAsList("positionids");
		positionMgService.recyclePermissions(positionsList, permissionsList, getDto());
		return "tojson";
	}

	public String grantUsePermissions() throws Exception {
		List<Key> permissionsList = getJsonParamAsList("ids");

		List<Key> positionsList = getJsonParamAsList("positionids");
		positionMgService.grantUsePermissions(positionsList, permissionsList, getDto());
		return "tojson";
	}
}
