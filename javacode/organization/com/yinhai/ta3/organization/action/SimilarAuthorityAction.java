package com.yinhai.ta3.organization.action;

import java.util.ArrayList;
import java.util.List;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.organization.service.IPositionMgService;
import com.yinhai.ta3.organization.service.ISimilarAuthorityService;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.webframework.BaseAction;

public class SimilarAuthorityAction extends BaseAction {

	private ISimilarAuthorityService similarAuthorityService = (ISimilarAuthorityService) super.getService("similarAuthorityService");

	private IPositionMgService positionMgService = (IPositionMgService) super.getService("positionMgService");

	public String execute() throws Exception {
		List<PermissionTreeVO> curPermissionNodes = similarAuthorityService.getRePermissionTreeByPositionid(getDto().getUserInfo().getNowPosition()
				.getPositionid());

		List<PermissionTreeVO> newPermissionNodes = new ArrayList();
		PermissionTreeVO newPermissionNode = null;
		if (!ValidateUtil.isEmpty(curPermissionNodes)) {
			for (int i = 0; i < curPermissionNodes.size(); i++) {
				newPermissionNode = (PermissionTreeVO) curPermissionNodes.get(i);
				if (i < 50) {
					newPermissionNode.setOpen(true);
				}
				newPermissionNodes.add(newPermissionNode);
			}
		}
		request.setAttribute("authorityTree", JSonFactory.bean2json(newPermissionNodes));
		return "success";
	}

	public String querypositionsByAuthorities() throws Exception {
		List<Key> ids = getJsonParamAsList("ids");
		List<Position> list = similarAuthorityService.querypositionsByAuthorities(ids, getDto().getUserInfo()
				.getNowPosition().getPositionid());
		setList("positionGrid", list);
		return "tojson";
	}

	public String toSimilarAuthority() throws Exception {
		String positionids = request.getParameter("positionids");
		setData("positionids", positionids);
		List<PermissionTreeVO> nodes = positionMgService.getRePermissionTreeByPositionId(getDto().getUserInfo().getNowPosition().getPositionid());
		List<PermissionTreeVO> newNodes = new ArrayList();
		PermissionTreeVO newNode = null;
		for (int i = 0; i < nodes.size(); i++) {
			newNode = (PermissionTreeVO) nodes.get(i);
			if (i < 50) {
				newNode.setOpen(true);
			}
			newNodes.add(newNode);
		}
		request.setAttribute("opTree", JSonFactory.bean2json(newNodes));
		return "similarAuthority";
	}

	public String saveSimilarAuthority() throws Exception {
		ParamDTO dto = getDto();

		List<Key> permissionsList = getJsonParamAsList("ids");

		List<Key> positionsList = getJsonParamAsList("positionids");
		Long batchNo = similarAuthorityService.getLongSeq();
		for (Key key : positionsList) {
			dto.append("positionid", key.getAsLong("id"));
			dto.append("positionType", key.getAsString("type"));
			positionMgService.saveRoleScopeAclOperate(batchNo, permissionsList, dto);
		}
		return "tojson";
	}
}
