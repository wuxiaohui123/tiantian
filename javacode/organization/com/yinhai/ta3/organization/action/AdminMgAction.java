package com.yinhai.ta3.organization.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.organization.service.IAdminMgService;
import com.yinhai.ta3.organization.service.IOrgMgService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.org.domain.UserInfoVO;

public class AdminMgAction extends OrgBaseAction {

	private IAdminMgService adminMgService = (IAdminMgService) super.getService("adminMgService");
	private IOrgMgService orgMgService = (IOrgMgService) super.getService("orgMgService");

	public String execute() throws Exception {
		List<UserInfoVO> users = adminMgService.getAdminMgUsersByPositionid(getDto().getUserInfo().getNowPosition().getPositionid());
		setList("adminMgGrid", users);
		return "success";
	}

	public String queryAdminMgUsers() throws Exception {
		List<UserInfoVO> users = adminMgService.getAdminMgUsersByPositionid(getDto().getUserInfo().getNowPosition().getPositionid());
		setList("adminMgGrid", users);
		return "tojson";
	}

	public String toAddAdminMgUser() throws Exception {
		request.setAttribute("orgTree", super.queryJsonOrgsTree(getDto()));
		return "toAddAdminMgUser";
	}

	public String queryNoAdminUsers() throws Exception {
		PageBean users = adminMgService.queryNoAdminUsers("userGrid", getDto());
		setList("userGrid", users);
		return "tojson";
	}

	public String addAdminMgUser() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		adminMgService.addAdminMgUser(adminMgService.getLongSeq(), positionid, getDto());
		return "tojson";
	}

	public String addDetachAdminMgUser() throws Exception {
		List<Key> selected = getSelected("userGrid");
		Long batchNo = adminMgService.getLongSeq();
		for (Key key : selected) {
			adminMgService.addAdminMgUser(batchNo, key.getAsLong("positionid"), getDto());
		}
		return "tojson";
	}

	public String toFuncAdminUsePoermission() throws Exception {
		setData("adminPositionid", getDto().getAsLong("positionid"));
		List<PermissionTreeVO> nodes = new ArrayList();
		PermissionTreeVO node = null;
		List<PermissionTreeVO> curNodes = adminMgService
				.getAdminRePermissionTreeByPositionid(getDto().getUserInfo().getNowPosition().getPositionid());
		List<PermissionTreeVO> targetNodes = adminMgService.getAdminUsePermissionTreeByPositionid(getDto().getAsLong("positionid"));
		int i = 0;
		for (PermissionTreeVO cPermissionTreeVO : curNodes) {
			i++;
			node = cPermissionTreeVO;
			for (PermissionTreeVO tPermissionTreeVO : targetNodes) {
				if (tPermissionTreeVO.getId().equals(cPermissionTreeVO.getId())) {
					node.setChecked(true);
					break;
				}
			}
			if ("1".equals(cPermissionTreeVO.getIsleaf())) {
				node.setParent(true);
			}
			if (!ValidateUtil.isEmpty(cPermissionTreeVO.getMenulevel())) {
				if ((cPermissionTreeVO.getMenulevel().intValue() < 4) && (i <= 50)) {
					node.setOpen(true);
				}
			} else if (i <= 50) {
				node.setOpen(true);
			}

			if (("4".equals(cPermissionTreeVO.getPolicy())) || ("3".equals(cPermissionTreeVO.getPolicy()))) {
				node.setNocheck(true);
			}
			nodes.add(node);
		}
		request.setAttribute("adminTree", JSonFactory.bean2json(nodes));
		return "toFuncAdminUsePoermission";
	}

	public String saveAdminUsePermission() throws Exception {
		List<Key> menuids = getJsonParamAsList("ids");
		Long positionid = getDto().getAsLong("positionid");
		adminMgService.saveAdminUsePermission(menuids, positionid, getDto());
		return "tojson";
	}

	public String toOrgMgScope() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		setData("positionid", positionid);

		List<Org> orgs = orgMgService.querySubOrgs(Org.ORG_ROOT_ID, true, true, "0");

		List<Org> corgs = adminMgService.getCurPositionOrgMgScope(getDto().getUserInfo().getNowPosition().getPositionid());

		List<Org> torgs = adminMgService.getTargetPositionOrgMgScope(positionid);
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < orgs.size(); i++) {
			Org org = (Org) orgs.get(i);
			sb.append("{\"id\":'").append(org.getOrgid()).append("'").append(",\"name\":'").append(org.getOrgname()).append("'").append(",\"pId\":'")
					.append(org.getPorgid()).append("'");

			for (Org torg : torgs) {
				if (torg.getOrgid().equals(org.getOrgid())) {
					sb.append(",\"checked\":true");
					break;
				}
			}
			if (IPosition.ADMIN_POSITIONID.equals(getDto().getUserInfo().getNowPosition().getPositionid())) {
				sb.append(",admin:true");
			} else {
				for (int j = 0; j < corgs.size(); j++) {
					Org corg = (Org) corgs.get(j);
					if (corg.getOrgid().equals(org.getOrgid())) {
						sb.append(",admin:true");
						break;
					}
					if (j == corgs.size() - 1) {
						sb.append(",admin:false");
						sb.append(",nocheck:true");
					}
				}
			}
			if (i < 50) {
				sb.append(",\"open\":true");
			}
			if (i < orgs.size() - 1) {
				sb.append("},");
			} else {
				sb.append("}");
			}
		}
		sb.append("]");
		request.setAttribute("orgMgTree", sb.toString());
		return "toOrgMgScope";
	}

	public String saveOrgMgScope() throws Exception {
		Long positionid = getDto().getAsLong("positionid");

		List<Key> orgids = getJsonParamAsList("ids");
		adminMgService.saveOrgMgScope(positionid, orgids, getDto());
		return "tojson";
	}

	public String toAdminYab003Scope() throws Exception {
		ParamDTO dto = getDto();
		Long curPositionid = dto.getUserInfo().getNowPosition().getPositionid();
		List<Map<String, String>> curlist = adminMgService.queryAdminYab003Scope(dto.getUserInfo().getUserid(), curPositionid);
		setList("yab003Grid", curlist);
		return "toAdminYab003Scope";
	}

	public String queryTargetUserYab003Scope() throws Exception {
		ParamDTO dto = getDto();
		Long duserid = dto.getAsLong("userid");
		Long dpositionid = dto.getAsLong("positionid");
		List<Map<String, String>> dlist = adminMgService.queryAdminYab003Scope(duserid, dpositionid);
		setData("dlist", dlist);
		setData("positionid", dpositionid);
		return "tojson";
	}

	public String saveAdminYab003Scope() throws Exception {
		List<Key> list = getSelected("yab003Grid");
		adminMgService.saveAdminYab003Scope(list, getDto());
		return "tojson";
	}

	public String removeAdminMgUser() throws Exception {
		adminMgService.removeAdminMgUser(getDto().getAsLong("positionid"), getDto().getUserInfo());
		queryAdminMgUsers();
		return "tojson";
	}

	public String toTransformAuthority() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		setData("positionid", positionid);

		List<UserInfoVO> users = adminMgService.getAdminMgUsersNoTransformPositionByPositionid(getDto().getUserInfo().getNowPosition()
				.getPositionid(), positionid);
		setList("userGrid", users);
		return "toTransformAuthority";
	}

	public String transformAuthority() throws Exception {
		Long positionid = getDto().getAsLong("positionid");

		List<Key> selected = getSelected("userGrid");
		adminMgService.transformAuthority(positionid, selected, getDto().getUserInfo());
		return "tojson";
	}

	public String toFuncGrantingPurview() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		setData("grantPositionid", positionid);
		setData("positionType", getDto().getAsString("positionType"));
		Map<String, List<PermissionTreeVO>> nodes = adminMgService.getRePermissionAndAuthrityTreeByPositionid(positionid);

		List<PermissionTreeVO> permissionNodes = (List) nodes.get("premissionnodes");

		List<PermissionTreeVO> authrityNodes = (List) nodes.get("authritynodes");

		Long nowPositionid = getDto().getUserInfo().getNowPosition().getPositionid();
		if (ValidateUtil.isEmpty(nowPositionid)) {
			throw new AppException("当前用户岗位为空!");
		}
		Map<String, List<PermissionTreeVO>> curNodes = adminMgService.getRePermissionAndAuthrityTreeByPositionid(nowPositionid);

		List<PermissionTreeVO> curAuthrityNodes = (List) curNodes.get("authritynodes");

		List<PermissionTreeVO> newPermissionNodes = new ArrayList();
		PermissionTreeVO newPermissionNode = null;
		for (int i = 0; i < curAuthrityNodes.size(); i++) {
			newPermissionNode = (PermissionTreeVO) curAuthrityNodes.get(i);
			for (PermissionTreeVO permissionNode : permissionNodes) {
				if (newPermissionNode.getId().longValue() == permissionNode.getId().longValue()) {
					newPermissionNode.setChecked1(true);
				}
			}
			if (i < 50) {
				newPermissionNode.setOpen(true);
			}
			if (("4".equals(newPermissionNode.getPolicy())) || ("3".equals(newPermissionNode.getPolicy()))) {
				newPermissionNode.setNocheck(true);
			}
			newPermissionNodes.add(newPermissionNode);
		}
		List<PermissionTreeVO> newAuthrityNodes = new ArrayList();
		PermissionTreeVO newAuthrityNode = null;
		for (int i = 0; i < curAuthrityNodes.size(); i++) {
			newAuthrityNode = (PermissionTreeVO) curAuthrityNodes.get(i);
			for (PermissionTreeVO authrityNode : authrityNodes) {
				if (newAuthrityNode.getId().longValue() == authrityNode.getId().longValue()) {
					newAuthrityNode.setChecked2(true);
				}
			}
			if (i < 50) {
				newAuthrityNode.setOpen(true);
			}
			if (("4".equals(newAuthrityNode.getPolicy())) || ("3".equals(newAuthrityNode.getPolicy()))) {
				newAuthrityNode.setNocheck(true);
			}
			newAuthrityNodes.add(newAuthrityNode);
		}
		request.setAttribute("grantingTree1", JSonFactory.bean2json(newPermissionNodes));
		request.setAttribute("grantingTree2", JSonFactory.bean2json(newAuthrityNodes));
		return "toFuncGrantingPurview";
	}

	public String toGrantAuthorityPermissions() throws Exception {
		String positionids = request.getParameter("positionids");
		setData("grantAuthorityPermissionsPositionids", positionids);

		Map<String, List<PermissionTreeVO>> nodes = adminMgService.getRePermissionAndAuthrityTreeByPositionid(getDto().getUserInfo().getNowPosition()
				.getPositionid());
		List<PermissionTreeVO> permissionNodes = (List) nodes.get("premissionnodes");
		List<PermissionTreeVO> authrityNodes = (List) nodes.get("authritynodes");

		request.setAttribute("grantingTree1", buildRecyleTree(permissionNodes));
		request.setAttribute("grantingTree2", buildRecyleTree(authrityNodes));
		return "grantAuthorityPermissions";
	}

	public String toRecycleAuthorityPermissions() throws Exception {
		String positionids = request.getParameter("positionids");
		setData("recycleAuthorityPermissionsPositionids", positionids);

		Map<String, List<PermissionTreeVO>> nodes = adminMgService.getRePermissionAndAuthrityTreeByPositionid(getDto().getUserInfo().getNowPosition()
				.getPositionid());

		List<PermissionTreeVO> permissionNodes = (List) nodes.get("premissionnodes");

		List<PermissionTreeVO> authrityNodes = (List) nodes.get("authritynodes");

		request.setAttribute("grantingTree1", buildRecyleTree(permissionNodes));
		request.setAttribute("grantingTree2", buildRecyleTree(authrityNodes));
		return "recycleAuthorityPermissions";
	}

	public String recycleAuthorityPermissions() throws Exception {
		List<Key> permissionList = getJsonParamAsList("ids");

		List<Key> positionList = getJsonParamAsList("positionids");
		adminMgService.recycleAuthorityPermissions(positionList, permissionList, getDto());
		return "tojson";
	}

	public String grantAuthorityPermissions() throws Exception {
		List<Key> permissionList = getJsonParamAsList("ids");

		List<Key> positionList = getJsonParamAsList("positionids");
		adminMgService.grantAuthorityPermissions(positionList, permissionList, getDto());
		return "tojson";
	}

	public String saveRoleScopeAclGranting() throws Exception {
		List<Key> list = getJsonParamAsList("ids");

		List<Key> addList = new ArrayList();

		List<Key> delList = new ArrayList();

		List<Key> addList2 = new ArrayList();

		List<Key> delList2 = new ArrayList();

		int size = list.size();
		Key key = null;
		for (int i = 0; i < size; i++) {
			key = (Key) list.get(i);
			if (key.getAsString("checked").equals("true")) {
				if (key.get("re") == null) {
					addList.add(key);
				} else {
					addList2.add(key);
				}
			} else if (key.getAsString("checked").equals("false")) {
				if (key.get("re") == null) {
					delList.add(key);
				} else {
					delList2.add(key);
				}
			}
		}
		getDto().put("addList", addList);
		getDto().put("delList", delList);
		getDto().put("addList2", addList2);
		getDto().put("delList2", delList2);
		adminMgService.saveRoleScopeAclGranting(getDto());
		return "tojson";
	}

	public String toRecyclePermissions() throws Exception {
		String positionids = request.getParameter("positionids");
		setData("recyclePermissionsPositionids", positionids);
		setData("positionType", getDto().getAsString("positionType"));

		List<PermissionTreeVO> nodes = adminMgService.getRePermissionTreeByPositionId(getDto().getUserInfo().getNowPosition().getPositionid());

		request.setAttribute("opTree", buildRecyleTree(nodes));
		return "recyclePermissions";
	}

	public String toGrantUsePermissions() throws Exception {
		String positionids = request.getParameter("positionids");
		setData("grantPermissionsPositionids", positionids);

		List<PermissionTreeVO> nodes = adminMgService.getRePermissionTreeByPositionId(getDto().getUserInfo().getNowPosition().getPositionid());

		request.setAttribute("opTree", buildRecyleTree(nodes));
		return "grantUsePermissions";
	}

	public String grantAdminUsePermissions() throws Exception {
		List<Key> list = getJsonParamAsList("ids");
		List<Key> positionsList = getJsonParamAsList("positionids");
		adminMgService.grantAdminUsePermissions(positionsList, list, getDto());
		return "tojson";
	}

	public String recycleAdminUsePermissions() throws Exception {
		List<Key> list = getJsonParamAsList("ids");
		List<Key> positionsList = getJsonParamAsList("positionids");
		adminMgService.recycleAdminUsePermissions(positionsList, list, getDto());
		return "tojson";
	}
}
