package com.yinhai.ta3.organization.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.codetable.service.CodeTableLocator;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IDataAccessApi;
import com.yinhai.sysframework.iorg.IOrg;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.security.IPermissionService;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.api.OrganizationEntityService;
import com.yinhai.ta3.organization.service.IAdminMgService;
import com.yinhai.ta3.organization.service.IOrgMgService;
import com.yinhai.ta3.organization.service.IPositionMgService;
import com.yinhai.ta3.organization.service.IPositionUserMgService;
import com.yinhai.ta3.system.org.domain.MenuPositionVO;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.PositionAuthrity;
import com.yinhai.ta3.system.org.domain.PositionInfoVO;
import com.yinhai.ta3.system.org.domain.UserInfoVO;
import com.yinhai.ta3.system.org.domain.Yab139Mg;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class PositionUserMgAction extends OrgBaseAction {

	private IPositionUserMgService positionUserMgService = (IPositionUserMgService) super.getService("positionUserMgService");
	private IPositionMgService positionMgService = (IPositionMgService) ServiceLocator.getService("positionMgService");
	private IPermissionService permissionService = (IPermissionService) super.getService("permissionService");
	private OrganizationEntityService organizationEntityService = (OrganizationEntityService) super.getService("organizationEntityService");
	private IOrgMgService orgMgService = (IOrgMgService) super.getService("orgMgService");

	private IAdminMgService adminMgService = (IAdminMgService) super.getService("adminMgService");

	private IDataAccessApi api = (IDataAccessApi) super.getService("dataAccessApi");

	public String execute() throws Exception {
		String orgString = super.queryJsonOrgsTree(getDto());
		request.setAttribute("orgTree", orgString);
		request.setAttribute("pos_orgTree", orgString);
		request.setAttribute("orgTreeUserPosition", orgString);
		return super.execute();
	}

	public String getOpenPubPosition() throws Exception {
		String flag = SysConfig.getSysConfig("isOpenPubPosition", "false");
		StringBuffer sb = new StringBuffer();
		sb.append("{\"isopen\":").append(flag).append("}");
		writeJsonToClient(sb);
		return null;
	}

	public String webQueryPosTree() throws Exception {
		super.webQueryAsyncOrgTree();
		return null;
	}

	public String queryUsers() throws Exception {
		ParamDTO dto = getDto();
		dto.put("positionType", "2");
		PageBean pb = positionUserMgService.queryUsersByParamDto("userGrid", dto);
		setList("userGrid", pb);
		return "tojson";
	}

	public String queryPosition() throws Exception {
		ParamDTO dto = reBuildDto("pos_", getDto());
		dto.put("positionType", "1");
		dto.append("gridId", "positionGrid");

		PageBean pb = positionUserMgService.queryPositionByParamDto("positionGrid", dto);
		setList("positionGrid", pb);
		return "tojson";
	}

	public String queryPerMission() throws Exception {
		ParamDTO dto = getDto();
		Long userid = dto.getAsLong("userid");
		List<PositionInfoVO> list = positionUserMgService.queryPositionByUserid(userid);
		IOrg org = organizationEntityService.getDepartByUserId(userid);
		setData("orgPath", org.getOrgnamepath());
		setList("perPosition", list);
		List<MenuPositionVO> newList = new ArrayList<MenuPositionVO>();

		List<MenuPositionVO> MenuPositionVOList = positionUserMgService.queryPositionPermissionsByUserId(userid);
		for (PositionInfoVO pos : list) {
			newList.addAll(queryChildrenMenus(Long.valueOf(0L), pos.getPositionid(), MenuPositionVOList, Long.valueOf(1L), new ArrayList<MenuPositionVO>()));
		}
		List<MenuPositionVO> result = new ArrayList<MenuPositionVO>();
		for (MenuPositionVO mvo : newList) {
			if (!result.contains(mvo)) {
				result.add(mvo);
			}
		}
		setList("perMission", result);
		return "tojson";
	}

	public String queryPositionInfo() throws Exception {
		return "tojson";
	}

	public String setMainPosition() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		Long userid = getDto().getAsLong("per_userid");
		positionMgService.setMainPosition(positionid, userid, getDto());
		permissionService.clearUserEffectivePositionsCache(userid);
		List<PositionInfoVO> positions = positionMgService.getPubPositionsCurUserid(userid);
		setList("perPosition", positions);
		return "tojson";
	}

	public String removeUserPosition() throws Exception {
		Long userid = getDto().getAsLong("per_userid");
		getDto().put("userid", userid);

		positionMgService.removeUserPosition(getDto());

		permissionService.clearUserEffectivePositionsCache(userid);
		List<PositionInfoVO> positions = positionMgService.getPubPositionsCurUserid(userid);
		setList("perPosition", positions);

		ArrayList<MenuPositionVO> newList = new ArrayList<MenuPositionVO>();
		List<MenuPositionVO> resultMenu = positionUserMgService.queryPositionPermissionsByUserId(userid);
		for (PositionInfoVO pos : positions) {
			newList.addAll(queryChildrenMenus(Long.valueOf(0L), pos.getPositionid(), resultMenu, Long.valueOf(1L), new ArrayList<MenuPositionVO>()));
		}

		setList("perMission", resultMenu);
		return "tojson";
	}

	public String queryPosMission() throws Exception {
		Long positionid = getDto().getAsLong("pos_positionid");
		List<MenuPositionVO> list = positionUserMgService.queryPositionPermissionsByPositionId(positionid);
		List<MenuPositionVO> newList = queryChildrenMenus(Long.valueOf(0L), positionid, list, Long.valueOf(1L), new ArrayList<MenuPositionVO>());

		List<UserInfoVO> userInfoList = positionUserMgService.queryUserInPosition(positionid);
		List<Org> shareOrgList = positionUserMgService.querySharePosition(positionid);
		setList("posMission", newList);
		setList("posUser", userInfoList);
		setList("posShare", shareOrgList);
		return "tojson";
	}

	public String toPosMissionWindow() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		List<MenuPositionVO> newList = new ArrayList<MenuPositionVO>();
		List<MenuPositionVO> list = positionUserMgService.queryPositionPermissionsByPositionId(positionid);
		newList.addAll(queryChildrenMenus(Long.valueOf(0L), positionid, list, Long.valueOf(1L), new ArrayList<MenuPositionVO>()));

		setList("posMissionwin_posMission", newList);

		return "toPosMissionWindow";
	}

	public String toAssignPositionsToUser() throws Exception {
		Long userid = getDto().getAsLong("userid");
		setData("open_userid", userid);
		String positionids = getDto().getAsString("positionids");
		setData("positionids", positionids);
		return "assignPositionsToUser";
	}

	public String getPubPositionsNoCurUseridByOrgId() throws Exception {
		ParamDTO dto = getDto();
		dto.put("userid", dto.getAsLong("open_userid"));
		dto.put("orgid", dto.getAsLong("open_orgid"));
		dto.put("positionname", dto.getAsString("open_positionname"));
		String positionids = getDto().getAsString("positionids");
		String gridId = "noPositionPerGrid";
		Integer startI = getDto().getStart(gridId);
		Integer limitI = getDto().getLimit(gridId);
		int start = 0;
		int limit = 20;
		if (!ValidateUtil.isEmpty(startI)) {
			start = startI.intValue();
		}
		if (!ValidateUtil.isEmpty(limitI)) {
			limit = limitI.intValue();
		}
		PageBean positions = positionUserMgService.getPubPositionsNoCurUseridByOrgId(getDto(), gridId, start, limit);
		setList("noPositionPerGrid", positions);
		return "tojson";
	}

	public String getPubPositionsNoCurUseridByOrgIdWithPage() {
		return "tojson";
	}

	public String toFuncOpPurview() throws Exception {
		setData("positionid", getDto().getAsLong("positionid"));
		setData("positionType", getDto().getAsLong("positionType"));
		setData("userid", getDto().getAsLong("userid"));
		return "toFuncOpPurview";
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

	public String toAddPosition() throws Exception {
		request.setAttribute("orgTree", super.queryJsonOrgsTree(getDto()));
		//setData("positioncategory", "01");
		return "addPosition";
	}

	public String editPosition() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		setData("editpositionid", positionid);
		Position p = positionMgService.getPosition(positionid);
		if (p != null) {
			setData("positionname", p.getPositionname());

			setData("positioncategory", p.getPositioncategory());

			request.setAttribute("orgTree", super.queryJsonOrgsTree(getDto()));

			String orgnamepath = p.getOrgnamepath();

			String orgname = "";
			if (orgnamepath.lastIndexOf("/") > -1) {
				orgname = orgnamepath.substring(orgnamepath.lastIndexOf("/") + 1, orgnamepath.length());
			} else {
				orgname = orgnamepath;
			}

			setData("orgTree", p.getOrgid() + "," + orgname);
			setShowObj("saveEdit");
			setHideObj("save");
		}
		return "addPosition";
	}

	public String createPosition() throws Exception {
		ParamDTO dto = getDto();
		dto.put("positiontype", "1");
		dto.append("gridId", "positionGrid");
		dto.append("effective", "0");

		positionMgService.createPosition(dto);

		PageBean pb = positionUserMgService.queryPositionByParamDto("positionGrid", dto);
		setList("positionGrid", pb);

		return "tojson";
	}

	public String updatePosition() throws Exception {
		Long positionid = getDto().getAsLong("editpositionid");
		getDto().put("positionid", positionid);
		positionMgService.updatePosition(getDto());
		return "tojson";
	}

	public String deletePositions() throws Exception {
		List<Key> list = getSelected("positionGrid");
		positionMgService.removePosition(list, getDto());
		return "tojson";
	}

	public String toAssignUser() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		String userids = getDto().getAsString("userids");
		setData("positionid", positionid);
		setData("userids", userids);
		setData("orgid", IOrg.ORG_ROOT_ID);

		ParamDTO dto = getDto();
		List<Key> useridList = new ArrayList();

		if (!ValidateUtil.isEmpty(userids)) {
			String[] users = userids.split(",");
			for (String userid : users) {
				Key key = new Key();
				key.put("userid", userid);
				useridList.add(key);
			}
		}

		request.setAttribute("orgTree", super.queryJsonOrgsTree(getDto()));
		return "assignUsersToPosition";
	}

	public String queryUsersByOrgId() throws Exception {
		ParamDTO dto = getDto();
		List<Key> useridList = new ArrayList();

		String userids = dto.getAsString("userids");
		if (!ValidateUtil.isEmpty(userids)) {
			String[] users = userids.split(",");
			for (String userid : users) {
				Key key = new Key();
				key.put("userid", userid);
				useridList.add(key);
			}
		}

		dto.append("userids", useridList);
		String gridId = "userGrid";
		Integer startI = getDto().getStart(gridId);
		Integer limitI = getDto().getLimit(gridId);
		int start = 0;
		int limit = 20;
		if (!ValidateUtil.isEmpty(startI)) {
			start = startI.intValue();
		}
		if (!ValidateUtil.isEmpty(limitI)) {
			limit = limitI.intValue();
		}

		PageBean pb = positionUserMgService.queryUsers(dto, gridId, start, limit);
		setList("userGrid", pb);
		return "tojson";
	}

	public String queryUsersByOrgIdWithPage() {
		return "tojson";
	}

	public String saveAssignUsers() throws Exception {
		List<Key> selected = getSelected("userGrid");
		Long positionid = getDto().getAsLong("positionid");
		positionMgService.saveAssignUsers(selected, getDto());
		for (Key key : selected) {
			permissionService.clearUserEffectivePositionsCache(key.getAsLong("userid"));
		}

		return "tojson";
	}

	public String removeAssignUsers() throws Exception {
		List<Key> selected = getSelected("posUser");
		positionMgService.removeAssignUsers(selected, getDto().getAsLong("positionid"), getDto().getUserInfo().getUserid());
		for (Key key : selected) {
			permissionService.clearUserEffectivePositionsCache(key.getAsLong("userid"));
		}
		return "tojson";
	}

	public String removeAssignUsersBySingle() throws Exception {
		Long userid = getDto().getAsLong("userid");

		List<Key> selected = new ArrayList<Key>();
		Key key = new Key();
		key.put("userid", userid);
		selected.add(key);

		positionMgService.removeAssignUsers(selected, getDto().getAsLong("positionid"), getDto().getUserInfo().getUserid());
		permissionService.clearUserEffectivePositionsCache(key.getAsLong("userid"));
		return "tojson";
	}

	public String toSharePosition() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		setData("positionid", positionid);
		request.setAttribute("shareTree", queryScopOrgsAndSharePositions(getDto()));
		return "sharePosition";
	}

	public String saveSharePositions() throws Exception {
		List<Key> orgids = getJsonParamAsList("ids");
		positionMgService.saveSharePositions(getDto(), orgids);

		return "tojson";
	}

	public String deleteSharePositions() throws Exception {
		ParamDTO dto = getDto();
		Long orgid = dto.getAsLong("orgid");
		Long positionid = dto.getAsLong("pos_positionid");

		List<IPosition> positions = organizationEntityService.getAllPositionsByDepartId(orgid);
		List<IPosition> sharePositions = organizationEntityService.getSharePositionBySPositionId(positionid);
		Long delPositionId = null;
		for (IPosition p : sharePositions) {
			if (positions.contains(p)) {
				delPositionId = p.getPositionid();
			}
		}
		List<Key> list = new ArrayList<Key>();
		Key key = new Key();

		key.put("positionid", delPositionId);
		list.add(key);
		positionMgService.removePosition(list, getDto());

		List<Org> shareOrgList = positionUserMgService.querySharePosition(positionid);
		setList("posShare", shareOrgList);

		return "tojson";
	}

	public String getChildData() throws Exception {
		String idpath = getDto().getAsString("idpath");
		Long userid = getDto().getAsLong("per_userid");
		List<MenuPositionVO> MenuPositionVOList = positionUserMgService.queryPositionPermissionsByUserId(userid);
		List<MenuPositionVO> resultList = getChildMenu(MenuPositionVOList, idpath);

		List<Key> positionsList = new ArrayList<Key>();
		Key positionKey = new Key();
		positionKey.put("positionid", getDto().getAsLong("positionid"));
		positionsList.add(positionKey);

		List<Key> permissionsList = new ArrayList<Key>();
		for (int i = 0; i < resultList.size(); i++) {
			MenuPositionVO mvo = (MenuPositionVO) resultList.get(i);
			Key permission = new Key();
			permission.put("permissionid", mvo.getMenuid());
			permissionsList.add(permission);
		}
		try {
			positionUserMgService.recyclePermissions(positionsList, permissionsList, getDto());
		} catch (AppException e) {
			setMsg("没有权限删除该权限", "error");
		} finally {
			List<MenuPositionVO> resultMenu = positionUserMgService.queryPositionPermissionsByUserId(userid);
			setList("perMission", resultMenu);
		}
		return "tojson";
	}

	public String delPosMissionChildData() throws Exception {
		String idpath = getDto().getAsString("idpath");
		Long userid = getDto().getAsLong("per_userid");
		Long positionid = getDto().getAsLong("pos_positionid");
		List<MenuPositionVO> MenuPositionVOList = positionUserMgService.queryPositionPermissionsByPositionId(positionid);
		List<MenuPositionVO> resultList = getChildMenu(MenuPositionVOList, idpath);
		List<IPosition> sharePositions = organizationEntityService.getSharePositionBySPositionId(positionid);

		List<Key> positionsList = new ArrayList<Key>();
		Key positionKey = new Key();
		positionKey.put("positionid", positionid);
		positionsList.add(positionKey);
		for (IPosition p : sharePositions) {
			Key key = new Key();
			key.put("positionid", p.getPositionid());
			positionsList.add(key);
		}

		List<Key> permissionsList = new ArrayList<Key>();
		for (int i = 0; i < resultList.size(); i++) {
			MenuPositionVO mvo = (MenuPositionVO) resultList.get(i);
			Key permission = new Key();
			permission.put("permissionid", mvo.getMenuid());
			permissionsList.add(permission);
		}
		try {
			positionUserMgService.recyclePermissions(positionsList, permissionsList, getDto());
		} catch (AppException e) {
			setMsg("没有权限删除该权限", "error");
		} finally {
			List<MenuPositionVO> list = positionUserMgService.queryPositionPermissionsByPositionId(positionid);
			setList("posMission", list);
		}
		return "tojson";
	}

	public String queryPositionHaveMenuByUserid() throws Exception {
		Long userid = getDto().getAsLong("userid");
		Long menuid = getDto().getAsLong("menuid");
		List<Position> list = positionUserMgService.queryPositionsHaveMenuUsePermission(menuid, userid);
		setList("positions", list);
		return "tojson";
	}

	public List<MenuPositionVO> getChildMenu(List<MenuPositionVO> list, String idpath) throws Exception {
		String newIdpath = idpath + "/";
		List<MenuPositionVO> result = new ArrayList<MenuPositionVO>();
		for (int i = 0; i < list.size(); i++) {
			if ((((MenuPositionVO) list.get(i)).getMenuidpath().startsWith(newIdpath))
					|| (((MenuPositionVO) list.get(i)).getMenuidpath().equals(idpath))) {
				result.add(list.get(i));
			}
		}
		return result;
	}

	public String toRecyclePermissions() throws Exception {
		String positionids = request.getParameter("positionids");

		setData("recyclePermissionsPositionids", positionids);
		String type = getDto().getAsString("positionType");
		setData("positionType", type);

		List<PermissionTreeVO> nodes = positionMgService.getRePermissionTreeByPositionId(getDto().getUserInfo().getNowPosition().getPositionid());

		request.setAttribute("opTree", buildRecyleTree(nodes));

		if ("1".equals(type)) {
			setHideObj("personalrecyclePermissionsOpBtn");
			setShowObj("recyclePermissionsOpBtn");
		} else if ("2".equals(type)) {
			setShowObj("personalrecyclePermissionsOpBtn");
			setHideObj("recyclePermissionsOpBtn");
		}

		return "recyclePermissions";
	}

	public String toGrantUsePermissions() throws Exception {
		String positionids = request.getParameter("positionids");

		setData("grantPermissionsPositionids", positionids);
		String type = getDto().getAsString("positionType");
		setData("positionType", type);

		List<PermissionTreeVO> nodes = positionMgService.getRePermissionTreeByPositionId(getDto().getUserInfo().getNowPosition().getPositionid());

		request.setAttribute("opTree", buildRecyleTree(nodes));

		if ("1".equals(type)) {
			setHideObj("personalrecyclePermissionsOpBtn");
			setShowObj("recyclePermissionsOpBtn");
		} else if ("2".equals(type)) {
			setShowObj("personalrecyclePermissionsOpBtn");
			setHideObj("recyclePermissionsOpBtn");
		}

		return "grantUsePermissions";
	}

	public String saveUserAddPositions() throws Exception {
		ParamDTO dto = getDto();
		Long userid = dto.getAsLong("open_userid");
		dto.put("userid", userid);
		List<Key> selected = getSelected("noPositionPerGrid");
		positionMgService.saveUserAddPositions(selected, dto);
		permissionService.clearUserEffectivePositionsCache(dto.getAsLong("userid"));

		return "tojson";
	}

	public String unUsePosition() throws Exception {
		List<Key> list = getSelected("positionGrid");

		positionMgService.unUsePosition(list, getDto());
		return "tojson";
	}

	public String usePosition() throws Exception {
		List<Key> list = getSelected("positionGrid");
		positionMgService.usePosition(list, getDto());
		return "tojson";
	}

	public String getIsAudite() throws Exception {
		String flag = SysConfig.getSysConfig("isAudite", "false");
		StringBuffer sb = new StringBuffer();
		sb.append("{\"isAudite\":").append(flag).append("}");
		writeJsonToClient(sb);
		return null;
	}

	private String queryScopOrgsAndSharePositions(ParamDTO dto) {
		Long orgid = Org.ORG_ROOT_ID;
		List<Org> orgs = orgMgService.querySubOrgs(orgid, true, true, "0");
		List<Org> torgs = adminMgService.getTargetPositionOrgMgScope(dto.getUserInfo().getNowPosition().getPositionid());

		List<Org> os = positionMgService.queryCopyPositionInOrgBySharePositionId(dto.getAsLong("positionid"));
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < orgs.size(); i++) {
			Org org = (Org) orgs.get(i);
			sb.append("{id:'").append(org.getOrgid()).append("'").append(",name:'").append(org.getOrgname()).append("'").append(",pId:'")
					.append(org.getPorgid()).append("'");

			if (IPosition.ADMIN_POSITIONID.equals(dto.getUserInfo().getNowPosition().getPositionid())) {
				for (Org o : os) {
					if (org.getOrgid().equals(o.getOrgid())) {
						sb.append(",checked:true");
						break;
					}
				}
				sb.append(",isadmin:true");
			} else {
				for (Org o : os) {
					if (org.getOrgid().equals(o.getOrgid())) {
						sb.append(",checked:true");
						break;
					}
				}
				for (int j = 0; j < torgs.size(); j++) {
					Org torg = (Org) torgs.get(j);
					if (torg.getOrgid().equals(org.getOrgid())) {
						sb.append(",isadmin:true");
						break;
					}
					if (j == torgs.size() - 1) {
						sb.append(",nocheck:true");
					}
				}
			}
			if (i < 50) {
				sb.append(",open:true");
			}
			if (i < orgs.size() - 1) {
				sb.append("},");
			} else {
				sb.append("}");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public String queryPermissionsYab139s() throws Exception {
		List<Menu> reList = positionUserMgService.queryReUsePermissions(getDto().getUserInfo().getNowPosition().getPositionid());
		List<PositionAuthrity> useList = positionUserMgService.queryUsePermissions(getDto().getAsLong("positionid"));
		List<MenuPositionVO> volist = new ArrayList<MenuPositionVO>();
		if (ValidateUtil.isEmpty(reList)) {
			setData("list", "[]");
		} else {
			for (Menu menu : reList) {
				MenuPositionVO vo = new MenuPositionVO();
				vo.setMenuid(menu.getMenuid());
				vo.setNbsp(createNbsp(String.valueOf(menu.getMenulevel())));
				vo.setMenuname(menu.getMenuname());
				vo.setPmenuid(menu.getPmenuid());
				if ("0".equals(menu.getUseyab003())) {
					vo.setHasdp("true");
				} else {
					vo.setHasdp("false");
				}
				vo.setMenuidpath(menu.getMenuidpath());
				if (!ValidateUtil.isEmpty(useList)) {
					for (PositionAuthrity pa : useList) {
						if (menu.getMenuid().equals(pa.getId().getTamenu().getMenuid())) {
							vo.setPerview("checked");
							vo.setEffecttime(pa.getEffecttime());
							if (!"0".equals(menu.getUseyab003()))
								break;
							List<AppCode> yab139List = api.query(menu.getMenuid(), getDto().getAsLong("positionid"), "YAB139");
							if (!ValidateUtil.isEmpty(yab139List)) {
								String yab139Names = "";
								for (int i = 0; i < yab139List.size(); i++) {
									yab139Names = yab139Names + ((AppCode) yab139List.get(i)).getCodeDESC();
									if (i < yab139List.size() - 1) {
										yab139Names = yab139Names + ",";
									}
								}
								vo.setDp(yab139Names);
							}
							break;
						}
					}
				}

				volist.add(vo);
			}
		}
		setData("list", volist);
		String yab139s = positionUserMgService.queryDefaultYab139s(getDto().getAsLong("positionid"));
		setData("yab139s", yab139s);
		return "tojson";
	}

	public String queryDefaultAndAdminYab139s() throws Exception {
		Map<String, List<AppCode>> map = new HashMap<String, List<AppCode>>();
		Long curPositionid = getDto().getUserInfo().getNowPosition().getPositionid();
		if (IPosition.ADMIN_POSITIONID.equals(curPositionid)) {
			List<AppCode> codeList = CodeTableLocator.getCodeList("YAB139");
			map.put("adminList", codeList);
		} else {
			List<String> adminList = positionUserMgService.queryAdminMgYab139List(curPositionid);
			if (!ValidateUtil.isEmpty(adminList)) {
				List<AppCode> list = new ArrayList<AppCode>();
				for (int i = 0; i < adminList.size(); i++) {
					AppCode ac = new AppCode();
					ac.setCodeType("YAB139");
					ac.setCodeTypeDESC("数据区");
					ac.setCodeValue((String) adminList.get(i));
					ac.setCodeDESC(getCodeDesc("YAB139", (String) adminList.get(i), "9999"));
					list.add(ac);
				}
				map.put("adminList", list);
			}
		}
		List<Yab139Mg> defaultYab139List = positionUserMgService.queryDefaultYab139List(getDto().getAsLong("positionid"));
		List<AppCode> yab139List = positionUserMgService.queryYab139List(getDto().getAsLong("positionid"), getDto().getAsLong("menuid"));
		if (!ValidateUtil.isEmpty(defaultYab139List)) {
			List<AppCode> list = new ArrayList<AppCode>();
			for (int i = 0; i < defaultYab139List.size(); i++) {
				AppCode ac = new AppCode();
				ac.setCodeType("YAB139");
				ac.setCodeTypeDESC("数据区");
				ac.setCodeValue(((Yab139Mg) defaultYab139List.get(i)).getId().getYab139());
				ac.setCodeDESC(getCodeDesc("YAB139", ((Yab139Mg) defaultYab139List.get(i)).getId().getYab139(), "9999"));
				list.add(ac);
			}
			map.put("defaultYab139List", list);
		}
		if (!ValidateUtil.isEmpty(yab139List)) {
			map.put("yab139List", yab139List);
		}
		setData("yab139AllList", map);
		return "tojson";
	}

	public String queryAdminDefault() {
		Long curPositionid = getDto().getUserInfo().getNowPosition().getPositionid();
		List<AppCode> list = null;
		if (IPosition.ADMIN_POSITIONID.equals(curPositionid)) {
			list = super.getCodeList("YAB139", "9999");
		} else {
			List<String> adminList = positionUserMgService.queryAdminMgYab139List(curPositionid);
			if (!ValidateUtil.isEmpty(adminList)) {
				list = new ArrayList<AppCode>();
				for (int i = 0; i < adminList.size(); i++) {
					AppCode ac = new AppCode();
					ac.setCodeType("YAB139");
					ac.setCodeTypeDESC("数据区");
					ac.setCodeValue((String) adminList.get(i));
					ac.setCodeDESC(getCodeDesc("YAB139", (String) adminList.get(i), "9999"));
					list.add(ac);
				}
			}
		}
		List<Yab139Mg> defaultYab139List = positionUserMgService.queryDefaultYab139List(getDto().getAsLong("positionid"));
		List<AppCode> yabList = new ArrayList<AppCode>();
		if (!ValidateUtil.isEmpty(defaultYab139List)) {
			for (int i = 0; i < defaultYab139List.size(); i++) {
				AppCode ac = new AppCode();
				ac.setCodeType("YAB139");
				ac.setCodeTypeDESC("数据区");
				ac.setCodeValue(((Yab139Mg) defaultYab139List.get(i)).getId().getYab139());
				ac.setCodeDESC(getCodeDesc("YAB139", ((Yab139Mg) defaultYab139List.get(i)).getId().getYab139(), "9999"));
				yabList.add(ac);
			}
		}
		List<AppCode> result = new ArrayList<AppCode>();
		for (Iterator i$ = list.iterator(); i$.hasNext();) {
			AppCode code = (AppCode) i$.next();
			for (AppCode yabCode : yabList) {
				if ((code.getCodeType().equals(yabCode.getCodeType())) && (code.getCodeValue().equals(yabCode.getCodeValue()))) {
					result.add(yabCode);
				}
			}
		}
		setData("list", result);
		return "tojson";
	}

	public String queryYab139List() throws Exception {
		List<AppCode> yab139List = positionUserMgService.queryYab139List(getDto().getAsLong("positionid"), getDto().getAsLong("menuid"));
		if (ValidateUtil.isEmpty(yab139List)) {
			setData("yab139List", new ArrayList<AppCode>());
		}
		setData("yab139List", yab139List);
		return "tojson";
	}

	public String saveRoleScopeAclOperate() throws Exception {
		positionUserMgService.saveRoleScopeAclOperate(getDto());
		return "tojson";
	}

	private String createNbsp(String menulevel) {
		String nbsp = "";
		switch (Integer.valueOf(menulevel).intValue()) {
		case 1:
			nbsp = "&nbsp;&nbsp;&nbsp;";
			break;
		case 2:
			nbsp = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			break;
		case 3:
			nbsp = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			break;
		case 4:
			nbsp = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			break;
		case 5:
			nbsp = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			break;
		case 6:
			nbsp = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			break;
		case 7:
			nbsp = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			break;
		case 8:
			nbsp = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			break;
		case 9:
			nbsp = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			break;
		default:
			nbsp = "&nbsp;&nbsp;&nbsp;";
		}

		return nbsp;
	}

	private List<MenuPositionVO> queryChildrenMenus(Long pmenuid, Long positionid, List<MenuPositionVO> ms, Long menulevel,
			List<MenuPositionVO> menuLevelList) {
		Iterator<MenuPositionVO> iterator = ms.iterator();
		while (iterator.hasNext()) {
			MenuPositionVO m = (MenuPositionVO) iterator.next();

			if ((menulevel.equals(m.getMenulevel())) && (pmenuid.equals(m.getPmenuid()))) {
				menuLevelList.add(m);
				if ("1".equals(m.getIsleaf())) {
					menuLevelList.addAll(queryChildrenMenus(m.getMenuid(), m.getPositionid(), ms, Long.valueOf(menulevel.longValue() + 1L),
							new ArrayList<MenuPositionVO>()));
				}
			}
		}

		return menuLevelList;
	}
}
