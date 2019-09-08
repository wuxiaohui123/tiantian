package com.yinhai.ta3.organization.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IOrg;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.organization.api.OrganizationEntityService;
import com.yinhai.ta3.organization.service.IAdminUserMgService;
import com.yinhai.ta3.organization.service.IOrgMgService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.org.domain.UserInfoVO;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class AdminUserMgAction extends OrgBaseAction {

	private IAdminUserMgService adminUserMgService = (IAdminUserMgService) super.getService("adminUserMgService");
	private OrganizationEntityService organizationEntityService = (OrganizationEntityService) super.getService("organizationEntityService");
	private IOrgMgService orgMgService = (IOrgMgService) super.getService("orgMgService");

	public String execute() throws Exception {
		List<UserInfoVO> users = adminUserMgService.getAdminMgUsersByPositionid(getDto().getUserInfo().getNowPosition().getPositionid());
		setList("adminMgGrid", users);
		return super.execute();
	}

	public String toAddAdminMgUser() throws Exception {
		return "toAddAdminMgUser";
	}

	public String queryNoAdminUsers() throws Exception {
		PageBean users = adminUserMgService.queryNoAdminUsers("userGrid", getDto());
		setList("userGrid", users);
		return "tojson";
	}

	public String queryForFastPermissionSettion() throws Exception {
		ParamDTO dto = getDto();

		Long curPositionid = dto.getUserInfo().getNowPosition().getPositionid();
		List<Map<String, String>> curlist = adminUserMgService.queryAdminYab003Scope(dto.getUserInfo().getUserid(), curPositionid);

		IOrg org = organizationEntityService.getDepartByPositionId(dto.getAsLong("positionid"));
		Map map = new HashMap();
		map.put("org", org);
		List datas = adminUserMgService.queryYab139sByPositionId(dto.getAsLong("positionid"));
		List list = new ArrayList();
		Map map1 = null;
		for (int i = 0; i < datas.size(); i++) {
			map1 = new HashMap();
			map1.put("codeValue", datas.get(i));
			String codeDesc = getCodeDesc("YAB139", String.valueOf(datas.get(i)), "9999");
			map1.put("codeDesc", codeDesc);
			list.add(map1);
		}
		map.put("datas", list);
		map.put("yab139CurList", curlist);
		writeJsonToClient(map);
		return null;
	}

	public String addAdminUser() throws Exception {
		ParamDTO dto = getDto();

		adminUserMgService.addAdminUser(dto);
		return "tojson";
	}

	public String queryAdminMgUsers() throws Exception {
		List<UserInfoVO> users = adminUserMgService.getAdminMgUsersByPositionid(getDto().getUserInfo().getNowPosition().getPositionid());
		setList("adminMgGrid", users);
		return "tojson";
	}

	public String toTransformAuthority() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		setData("positionid", positionid);

		List<UserInfoVO> users = adminUserMgService.getAdminMgUsersNoTransformPositionByPositionid(getDto().getUserInfo().getNowPosition()
				.getPositionid(), positionid);
		setList("userGrid", users);
		return "toTransformAuthority";
	}

	public String transformAuthority() throws Exception {
		Long positionid = getDto().getAsLong("positionid");

		List<Key> selected = getSelected("userGrid");
		adminUserMgService.transformAuthority(positionid, selected, getDto().getUserInfo());
		return "tojson";
	}

	public String removeAdminMgUser() throws Exception {
		adminUserMgService.removeAdminMgUser(getDto().getAsLong("positionid"), getDto().getUserInfo());
		queryAdminMgUsers();
		return "tojson";
	}

	public String toRecyclePermissions() throws Exception {
		String positionids = request.getParameter("positionids");
		setData("recyclePermissionsPositionids", positionids);
		setData("positionType", getDto().getAsString("positionType"));

		List<PermissionTreeVO> nodes = adminUserMgService.getRePermissionTreeByPositionId(getDto().getUserInfo().getNowPosition().getPositionid());

		request.setAttribute("opTree", buildRecyleTree(nodes));
		return "recyclePermissions";
	}

	public String toGrantUsePermissions() throws Exception {
		String positionids = request.getParameter("positionids");
		setData("grantPermissionsPositionids", positionids);

		List<PermissionTreeVO> nodes = adminUserMgService.getRePermissionTreeByPositionId(getDto().getUserInfo().getNowPosition().getPositionid());

		request.setAttribute("opTree", buildRecyleTree(nodes));
		return "grantUsePermissions";
	}

	public String toGrantAuthorityPermissions() throws Exception {
		String positionids = request.getParameter("positionids");
		setData("grantAuthorityPermissionsPositionids", positionids);

		Map<String, List<PermissionTreeVO>> nodes = adminUserMgService.getRePermissionAndAuthrityTreeByPositionid(getDto().getUserInfo()
				.getNowPosition().getPositionid());
		List<PermissionTreeVO> permissionNodes = (List) nodes.get("premissionnodes");
		List<PermissionTreeVO> authrityNodes = (List) nodes.get("authritynodes");

		request.setAttribute("grantingTree1", buildRecyleTree(permissionNodes));
		request.setAttribute("grantingTree2", buildRecyleTree(authrityNodes));
		return "grantAuthorityPermissions";
	}

	public String grantAuthorityPermissions() throws Exception {
		List<Key> permissionList = getJsonParamAsList("ids");

		List<Key> positionList = getJsonParamAsList("positionids");
		adminUserMgService.grantAuthorityPermissions(positionList, permissionList, getDto());
		return "tojson";
	}

	public String recycleAuthorityPermissions() throws Exception {
		List<Key> permissionList = getJsonParamAsList("ids");

		List<Key> positionList = getJsonParamAsList("positionids");
		adminUserMgService.recycleAuthorityPermissions(positionList, permissionList, getDto());
		return "tojson";
	}

	public String toRecycleAuthorityPermissions() throws Exception {
		String positionids = request.getParameter("positionids");
		setData("recycleAuthorityPermissionsPositionids", positionids);

		Map<String, List<PermissionTreeVO>> nodes = adminUserMgService.getRePermissionAndAuthrityTreeByPositionid(getDto().getUserInfo()
				.getNowPosition().getPositionid());

		List<PermissionTreeVO> permissionNodes = (List) nodes.get("premissionnodes");

		List<PermissionTreeVO> authrityNodes = (List) nodes.get("authritynodes");

		request.setAttribute("grantingTree1", buildRecyleTree(permissionNodes));
		request.setAttribute("grantingTree2", buildRecyleTree(authrityNodes));
		return "recycleAuthorityPermissions";
	}

	public String grantAdminUsePermissions() throws Exception {
		List<Key> list = getJsonParamAsList("ids");
		List<Key> positionsList = getJsonParamAsList("positionids");
		adminUserMgService.grantAdminUsePermissions(positionsList, list, getDto());
		return "tojson";
	}

	public String recycleAdminUsePermissions() throws Exception {
		List<Key> list = getJsonParamAsList("ids");
		List<Key> positionsList = getJsonParamAsList("positionids");
		adminUserMgService.recycleAdminUsePermissions(positionsList, list, getDto());
		return "tojson";
	}

	public String getAllPermissionBaseInfo() throws Exception {
		toFuncAdminUsePoermission();

		toFuncGrantingPurview();

		toOrgMgScope();

		getAdminYab003Scope();
		System.out.println("=========================================");
		return "tojson";
	}

	public String toFuncAdminUsePoermission() throws Exception {
		setData("adminPositionid", getDto().getAsLong("positionid"));
		List<PermissionTreeVO> nodes = new ArrayList();
		List<PermissionTreeVO> nodesTemp = new ArrayList();
		PermissionTreeVO node = null;
		List<PermissionTreeVO> curNodes = adminUserMgService.getAdminRePermissionTreeByPositionid(getDto().getUserInfo().getNowPosition()
				.getPositionid());
		List<PermissionTreeVO> targetNodes = adminUserMgService.getAdminUsePermissionTreeByPositionid(getDto().getAsLong("positionid"));
		int i = 0;
		for (PermissionTreeVO cPermissionTreeVO : curNodes) {
			i++;
			node = cPermissionTreeVO;
			for (PermissionTreeVO tPermissionTreeVO : targetNodes) {
				if (tPermissionTreeVO.getId().equals(cPermissionTreeVO.getId())) {
					node.setChecked(true);
					nodesTemp.add(cPermissionTreeVO);
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

		List<PermissionTreeVO> list = queryChildrenMenus(Long.valueOf(1L), nodesTemp, Long.valueOf(2L), new ArrayList());
		setList("grid1", list);
		return "tojson";
	}

	public String toOrgMgScope() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		setData("positionid", positionid);

		List<Org> orgs = orgMgService.querySubOrgs(Org.ORG_ROOT_ID, true, true, "0");

		List<Org> corgs = adminUserMgService.getCurPositionOrgMgScope(getDto().getUserInfo().getNowPosition().getPositionid());

		List<Org> torgs = adminUserMgService.getTargetPositionOrgMgScope(positionid);
		List<Org> newOrgs = new ArrayList();
		for (int i = 0; i < torgs.size(); i++) {
			Org org = (Org) torgs.get(i);

			for (int j = 0; j < corgs.size(); j++) {
				Org org2 = (Org) corgs.get(j);
				if (org.getOrgid() == org2.getOrgid()) {
					newOrgs.add(org);
					break;
				}
			}
		}

		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < orgs.size(); i++) {
			Org org = (Org) orgs.get(i);
			sb.append("{\"id\"  :'").append(org.getOrgid()).append("'").append(",\"name\":'").append(org.getOrgname()).append("'")
					.append(",\"pId\" :'").append(org.getPorgid()).append("'");

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

		orgs = queryChildrenMenusOrg(Long.valueOf(1L), orgs, Long.valueOf(0L), new ArrayList());

		setList("grid3", orgs);
		setData("grid3NewOrgs", newOrgs);
		return "tojson";
	}

	public String getAdminYab003Scope() throws Exception {
		ParamDTO dto = getDto();
		Long curPositionid = dto.getUserInfo().getNowPosition().getPositionid();
		List<Map<String, String>> curlist = adminUserMgService.queryAdminYab003Scope(dto.getUserInfo().getUserid(), curPositionid);

		Long duserid = dto.getAsLong("userid");
		Long dpositionid = dto.getAsLong("positionid");
		List<Map<String, String>> dlist = adminUserMgService.queryAdminYab003Scope(duserid, dpositionid);

		List<Map<String, String>> newList = new ArrayList();
		for (int i = 0; i < dlist.size(); i++) {
			Map map = (Map) dlist.get(i);
			for (int j = 0; j < curlist.size(); j++) {
				Map map2 = (Map) curlist.get(j);
				if (map.get("codeValue").equals(map2.get("codeValue"))) {
					newList.add(map);
					break;
				}
			}
		}

		setList("grid4", newList);
		setData("positionid", dpositionid);
		return "tojson";
	}

	public String toAdminYab003Scope() throws Exception {
		ParamDTO dto = getDto();
		Long curPositionid = dto.getUserInfo().getNowPosition().getPositionid();
		List<Map<String, String>> curlist = adminUserMgService.queryAdminYab003Scope(dto.getUserInfo().getUserid(), curPositionid);
		return "tojson";
	}

	public String queryTargetUserYab003Scope() throws Exception {
		ParamDTO dto = getDto();
		Long curPositionid = dto.getUserInfo().getNowPosition().getPositionid();
		List<Map<String, String>> curlist = adminUserMgService.queryAdminYab003Scope(dto.getUserInfo().getUserid(), curPositionid);

		Long duserid = dto.getAsLong("userid");
		Long dpositionid = dto.getAsLong("positionid");
		List<Map<String, String>> dlist = adminUserMgService.queryAdminYab003Scope(duserid, dpositionid);

		List<Map<String, String>> newList = new ArrayList();
		for (int i = 0; i < dlist.size(); i++) {
			Map map = (Map) dlist.get(i);
			for (int j = 0; j < curlist.size(); j++) {
				Map map2 = (Map) curlist.get(j);
				if (map.get("codeValue").equals(map2.get("codeValue"))) {
					newList.add(map);
					break;
				}
			}
		}

		setList("grid4", newList);
		setData("positionid", dpositionid);
		return "tojson";
	}

	public String toFuncGrantingPurview() throws Exception {
		Long positionid = getDto().getAsLong("positionid");

		Map<String, List<PermissionTreeVO>> nodes = adminUserMgService.getRePermissionAndAuthrityTreeByPositionid(positionid);

		List<PermissionTreeVO> permissionNodes = (List) nodes.get("premissionnodes");

		List<PermissionTreeVO> authrityNodes = (List) nodes.get("authritynodes");

		Long nowPositionid = getDto().getUserInfo().getNowPosition().getPositionid();
		if (ValidateUtil.isEmpty(nowPositionid)) {
			throw new AppException("当前用户岗位为空!");
		}
		Map<String, List<PermissionTreeVO>> curNodes = adminUserMgService.getRePermissionAndAuthrityTreeByPositionid(nowPositionid);

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

		List<PermissionTreeVO> newPerAndAuthrityNodes = new ArrayList();
		PermissionTreeVO newPerAndAuthrityNode = null;
		for (int i = 0; i < curAuthrityNodes.size(); i++) {
			newPerAndAuthrityNode = (PermissionTreeVO) curAuthrityNodes.get(i);
			for (PermissionTreeVO permissionNode : permissionNodes) {
				if (newPerAndAuthrityNode.getId().longValue() == permissionNode.getId().longValue()) {
					newPerAndAuthrityNode.setChecked1(true);
				}
			}

			for (PermissionTreeVO authrityNode : authrityNodes) {
				if (newPerAndAuthrityNode.getId().longValue() == authrityNode.getId().longValue()) {
					newPerAndAuthrityNode.setChecked2(true);
				}
			}

			if (i < 50) {
				newPerAndAuthrityNode.setOpen(true);
			}
			if (("4".equals(newPerAndAuthrityNode.getPolicy())) || ("3".equals(newPerAndAuthrityNode.getPolicy()))) {
				newPerAndAuthrityNode.setNocheck(true);
			}
			newPerAndAuthrityNodes.add(newPerAndAuthrityNode);

			if ((!newPerAndAuthrityNode.isChecked1()) && (!newPerAndAuthrityNode.isChecked2())) {
				newPerAndAuthrityNodes.remove(newPerAndAuthrityNode);
			}
		}

		List<PermissionTreeVO> list = queryChildrenMenus(Long.valueOf(0L), newPerAndAuthrityNodes, Long.valueOf(1L), new ArrayList());

		setList("grid2", list);
		return "tojson";
	}

	public String toFuncAdminUsePoermission2() throws Exception {
		setData("adminPositionid", getDto().getAsLong("positionid"));

		List<PermissionTreeVO> nodes = new ArrayList();
		PermissionTreeVO node = null;
		List<PermissionTreeVO> curNodes = adminUserMgService.getAdminRePermissionTreeByPositionid(getDto().getUserInfo().getNowPosition()
				.getPositionid());
		List<PermissionTreeVO> targetNodes = adminUserMgService.getAdminUsePermissionTreeByPositionid(getDto().getAsLong("positionid"));
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
		adminUserMgService.saveAdminUsePermission(menuids, positionid, getDto());
		return "tojson";
	}

	public String toFuncGrantingPurview2() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		setData("grantPositionid", positionid);
		setData("positionType", getDto().getAsString("positionType"));
		Map<String, List<PermissionTreeVO>> nodes = adminUserMgService.getRePermissionAndAuthrityTreeByPositionid(positionid);

		List<PermissionTreeVO> permissionNodes = (List) nodes.get("premissionnodes");

		List<PermissionTreeVO> authrityNodes = (List) nodes.get("authritynodes");

		Long nowPositionid = getDto().getUserInfo().getNowPosition().getPositionid();
		if (ValidateUtil.isEmpty(nowPositionid)) {
			throw new AppException("当前用户岗位为空!");
		}
		Map<String, List<PermissionTreeVO>> curNodes = adminUserMgService.getRePermissionAndAuthrityTreeByPositionid(nowPositionid);

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
		adminUserMgService.saveRoleScopeAclGranting(getDto());
		return "tojson";
	}

	public String toOrgMgScope2() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		setData("positionid", positionid);

		List<Org> orgs = orgMgService.querySubOrgs(Org.ORG_ROOT_ID, true, true, "0");

		List<Org> corgs = adminUserMgService.getCurPositionOrgMgScope(getDto().getUserInfo().getNowPosition().getPositionid());

		List<Org> torgs = adminUserMgService.getTargetPositionOrgMgScope(positionid);
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
		ParamDTO dto = getDto();

		Long dpositionid = dto.getAsLong("positionid");

		List<Key> orgids = getJsonParamAsList("ids");
		adminUserMgService.saveOrgMgScope(dpositionid, orgids, getDto());
		return "tojson";
	}

	public String toAdminYab003Scope2() throws Exception {
		ParamDTO dto = getDto();
		setData("positionid", dto.getAsLong("positionid"));
		setData("userid", dto.getAsLong("userid"));

		Long curPositionid = dto.getUserInfo().getNowPosition().getPositionid();
		List<Map<String, String>> curlist = adminUserMgService.queryAdminYab003Scope(dto.getUserInfo().getUserid(), curPositionid);
		setList("yab003Grid", curlist);
		return "toAdminYab003Scope";
	}

	public String queryTargetUserYab003Scope2() throws Exception {
		ParamDTO dto = getDto();
		Long duserid = dto.getAsLong("userid");
		Long dpositionid = dto.getAsLong("positionid");

		List<Map<String, String>> dlist = adminUserMgService.queryAdminYab003Scope(duserid, dpositionid);
		setData("dlist", dlist);
		setData("positionid", dpositionid);
		return "tojson";
	}

	public String saveAdminYab003Scope() throws Exception {
		List<Key> list = getSelected("yab003Grid");
		adminUserMgService.saveAdminYab003Scope(list, getDto());
		return "tojson";
	}

	public String removeMgPermission() throws Exception {
		ParamDTO dto = getDto();
		Long id = dto.getAsLong("id");
		Long tpositionid = dto.getAsLong("positionid");
		Long curPositionid = dto.getUserInfo().getNowPosition().getPositionid();

		List<Menu> list = adminUserMgService.queryChildrenMenus(id, tpositionid, curPositionid);

		String ids = "";
		for (int i = 0; i < list.size(); i++) {
			Menu menu = (Menu) list.get(i);
			ids = ids + "{\"id\":\"" + menu.getMenuid() + "\",\"checked\":false},";
		}

		ids = "[" + ids.substring(0, ids.length() - 1) + "]";

		List<Key> menuids = jsonStrToList(ids);
		adminUserMgService.saveAdminUsePermission(menuids, tpositionid, dto);
		return "tojson";
	}

	public String removeOrgPermission() throws Exception {
		ParamDTO dto = getDto();
		Long id = dto.getAsLong("id");
		Long tpositionid = dto.getAsLong("positionid");
		Long curPositionid = dto.getUserInfo().getNowPosition().getPositionid();

		String ids = "";

		ids = "[{\"id\":\"" + id + "\",\"checked\":false}]";

		List<Key> orgids = jsonStrToList(ids);
		adminUserMgService.saveOrgMgScope(tpositionid, orgids, dto);
		return "tojson";
	}

	public String removeAdminYab003Scope() throws Exception {
		ParamDTO dto = getDto();
		String id = dto.getAsString("tCodeValue");
		String gridData = dto.getAsString("gridData");
		List list = jsonStrToList(gridData);

		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			if (id.equals(map.get("codeValue"))) {
				list.remove(map);
			}
		}

		adminUserMgService.saveAdminYab003Scope(list, dto);
		return "tojson";
	}

	private List<PermissionTreeVO> queryChildrenMenus(Long pmenuid, List<PermissionTreeVO> ms, Long menulevel, List<PermissionTreeVO> menuLevelList) {
		Iterator<PermissionTreeVO> iterator = ms.iterator();

		while (iterator.hasNext()) {
			PermissionTreeVO m = (PermissionTreeVO) iterator.next();

			if ((menulevel.equals(m.getMenulevel())) && (pmenuid.equals(m.getPId()))) {
				menuLevelList.add(m);
				if ("1".equals(m.getIsleaf())) {
					menuLevelList.addAll(queryChildrenMenus(m.getId(), ms, Long.valueOf(menulevel.longValue() + 1L), new ArrayList()));
				}
			}
		}

		return menuLevelList;
	}

	private List<Org> queryChildrenMenusOrg(Long pmenuid, List<Org> ms, Long menulevel, List<Org> menuLevelList) {
		Iterator<Org> iterator = ms.iterator();

		while (iterator.hasNext()) {
			Org m = (Org) iterator.next();

			if ((menulevel.equals(m.getOrglevel())) && (pmenuid.equals(m.getpOrg().getOrgid()))) {
				menuLevelList.add(m);
				if ("1".equals(m.getIsleaf())) {
					menuLevelList.addAll(queryChildrenMenusOrg(m.getOrgid(), ms, Long.valueOf(menulevel.longValue() + 1L), new ArrayList()));
				}
			}
		}

		return menuLevelList;
	}
}
