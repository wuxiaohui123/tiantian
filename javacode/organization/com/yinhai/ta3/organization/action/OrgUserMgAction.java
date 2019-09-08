package com.yinhai.ta3.organization.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.organization.service.IOrgUserMgService;
import com.yinhai.ta3.organization.service.IPositionMgService;
import com.yinhai.ta3.organization.service.IYab003MgService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.org.domain.UserPerrmissionVO;

public class OrgUserMgAction extends OrgBaseAction {

	private IOrgUserMgService orgUserMgService = (IOrgUserMgService) super.getService("orgUserMgService");
	private IPositionMgService positionMgService = (IPositionMgService) ServiceLocator.getService("positionMgService");
	private IYab003MgService yab003MgService = (IYab003MgService) ServiceLocator.getService("yab003MgService");

	public String execute() throws Exception {
		String allOrgTreeNodes = super.queryAllOrgTreeNodes(getDto());
		request.setAttribute("orgTree", allOrgTreeNodes);
		return super.execute();
	}

	public String toAddOrg() throws Exception {
		ParamDTO dto = getDto();
		setData("porgname", dto.getAsString("porgname"));
		setData("orglevel", dto.getAsString("orglevel"));
		setData("isleaf", dto.getAsString("isleaf"));
		setData("orgnamepath", dto.getAsString("orgnamepath"));
		setData("effective", dto.getAsString("effective"));
		setData("porgid", dto.getAsString("porgid"));
		setData("yab003", dto.getAsString("yab003"));
		setData("yab139", dto.getAsString("yab139"));
		setSelectInputList("yab139", super.getCodeList("yab139", "9999"));

		String maxCostomNo = orgUserMgService.getMaxCostomNo(getDto().getAsLong("porgid"));
		setData("maxUdi", maxCostomNo);
		return "addOrg";
	}

	public String addOrg() throws Exception {
		Org org = orgUserMgService.addOrg(getDto());

		setData("orgid", org.getOrgid());
		Map<String, Object> map = org.toMap();
		map.put("admin", Boolean.valueOf(true));
		if ("01".equals(org.getOrgtype())) {
			map.put("iconSkin", "tree-depart-area");
		} else {
			map.put("iconSkin", "tree-depart-labor");
		}
		setData("childOrg", map);
		return JSON;
	}

	public String toEditOrg() throws Exception {
		setData("t_orgid", getDto().getAsLong("orgid"));
		setData("t_porgname", getDto().getAsString("porgname"));
		return "editOrg";
	}

	public String queryEditOrg() throws Exception {
		ParamDTO dto = reBuildDto("t_", getDto());
		Org org = orgUserMgService.queryOrgNode(dto);
		if (org == null) {
			setSuccess(false);
			setMsg("无编辑该部门的权限或部门不存在");
			return JSON;
		}
		Map<String, Object> map = org.toMap();
		map.put("porgname", dto.getAsString("porgname"));

		Long positionid = org.getOrgmanager();
		if (!ValidateUtil.isEmpty(positionid)) {
			Position position = positionMgService.getPosition(positionid);
			map.put("orgmanager_name", position.getPositionname());
		}
		Map<String, Object> deputyInfo = orgUserMgService.getDeputyInfo(org.getOrgid());
		setData(deputyInfo, false);

		setData("maxUdi", "0");
		String isOpenAgencies = SysConfig.getSysConfig("isOpenAgencies", "false");
		if ("true".equals(isOpenAgencies)) {
			String yab003 = (String) map.get("yab003");
			setSelectInputList("yab139", yab003MgService.queryCurYab139(dto.getUserInfo().getNowPosition().getPositionid(), yab003));
		}
		setData(map, false);
		return JSON;
	}

	public String editOrg() throws Exception {
		ParamDTO dto = getDto();
		if ((dto.getAsString("orgid") == "1") && (dto.getAsString("effective") == "1")) {
			setTopMsg("顶层组织不能禁用");
			setSuccess(false);
			return JSON;
		}
		orgUserMgService.editOrg(dto);
		String allOrgTreeNodes = super.queryAllOrgTreeNodes(getDto());
		setData("allOrgTreeNodes", allOrgTreeNodes);
		return JSON;
	}

	public String toAddUser() throws Exception {
		String isMutileOrg = SysConfig.getSysConfig("isMutileOrg", "false");
		if ("false".equals(isMutileOrg)) {
			setHideObj("w1_orgname");
		}
		request.setAttribute("w_orgTree", super.queryJsonOrgsTree(getDto()));
		request.setAttribute("w1_orgTree", super.queryJsonOrgsTree(getDto()));
		return "toAddUser";
	}

	public String addUser() throws Exception {
		ParamDTO reBuildDto = reBuildDto("w_", getDto());
		User user = orgUserMgService.addUser(reBuildDto);
		if (user.getUserid() == null) {
			setSuccess(false);
			setMsg("保存失败");
		}
		return JSON;
	}

	public String deleteUsers() throws Exception {
		List<Key> users = getSelected("userGd");
		orgUserMgService.deleteUsers(users, getDto());
		return JSON;
	}

	public String toEditUser() throws Exception {
		User user = orgUserMgService.getUser(getDto().getAsLong("userid"));
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> iterator = user.toMap().keySet().iterator();
		while (iterator.hasNext()) {
			String string = iterator.next();
			map.put("p_" + string, user.toMap().get(string));
		}
		setData(map, true);
		return JSON;
	}

	public String editUser() throws Exception {
		orgUserMgService.editUser(reBuildDto("p_", getDto()));
		return JSON;
	}

	public String deleteOrg() throws Exception {
		orgUserMgService.deleteOrg(getDto());
		String allOrgTreeNodes = super.queryAllOrgTreeNodes(getDto());
		setData("allOrgTreeNodes", allOrgTreeNodes);
		return JSON;
	}

	public String sortOrg() throws Exception {
		List<Key> sortids = getJsonParamAsList("sortorgids");
		List<Long> sortidslong = new ArrayList<Long>();
		for (Key key : sortids) {
			sortidslong.add(key.getAsLong("orgid"));
		}
		orgUserMgService.sortOrg(sortidslong, getDto().getUserInfo().getUserid());
		return JSON;
	}

	public String queryUsers() throws Exception {
		ParamDTO dto = getDto();
		String isShowSubOrg = dto.getAsString("isShowSubOrg");
		if ("1".equals(isShowSubOrg)) {
			dto.put("isShowSubOrg", Boolean.valueOf(true));
		} else {
			dto.put("isShowSubOrg", Boolean.valueOf(false));
		}
		if ("false".equals(dto.getAsString("isShow"))) {
			dto.put("sex", "-1");
			dto.put("islock", "-1");
		}
		dto.put("orgid", dto.get("deptId"));
		int start = dto.getStart("userGd").intValue();
		int limit = dto.getLimit("userGd").intValue();
		PageBean queryUsersInfo = orgUserMgService.queryUsersInfo(dto, "userGd", start, limit);

		setList("userGd", queryUsersInfo);
		return JSON;
	}

	public String unEffectiveUsers() throws Exception {
		List<Key> selected = getSelected("userGd");
		Long[] userids = new Long[selected.size()];
		for (int i = 0; i < selected.size(); i++) {
			userids[i] = selected.get(i).getAsLong("userid");
		}
		orgUserMgService.unBatchUseUser(userids, getDto());
		return JSON;
	}

	public String effectiveUsers() throws Exception {
		List<Key> selected = getSelected("userGd");
		Long[] userids = new Long[selected.size()];
		for (int i = 0; i < selected.size(); i++) {
			userids[i] = selected.get(i).getAsLong("userid");
		}
		orgUserMgService.batchReUser(userids, getDto());
		return JSON;
	}

	public String toUpdateOrg() throws Exception {
		User user = orgUserMgService.getUser(getDto().getAsLong("userid"));

		Org directOrg = orgUserMgService.queryOrgNode(getDto().append("orgid", user.getDirectorgid()));

		List<Org> affiliatedOrgs = orgUserMgService.queryAffiliatedOrgs(getDto().getAsLong("userid"));
		String w1_orgid = "";
		String w1_orgname = "";
		for (int i = 0; i < affiliatedOrgs.size(); i++) {
			if (i != affiliatedOrgs.size() - 1) {
				w1_orgid = w1_orgid + affiliatedOrgs.get(i).getOrgid() + ",";
				w1_orgname = w1_orgname + affiliatedOrgs.get(i).getOrgname() + ",";
			} else {
				w1_orgid = w1_orgid + affiliatedOrgs.get(i).getOrgid();
				w1_orgname = w1_orgname + affiliatedOrgs.get(i).getOrgname();
			}
		}

		request.setAttribute("w_orgTree", super.queryJsonOrgsTree(getDto()));
		List<Org> orgs = orgUserMgService.querySubOrgs(Org.ORG_ROOT_ID, true, true, "-1");
		Long positionid = getDto().getUserInfo().getNowPosition().getPositionid();
		List<Long> orgids = orgUserMgService.queryPositionCouldManageOrgIds(positionid);
		List<Map<String, Object>> lOrgs = buildOrgTreeJSON(orgs, orgids, affiliatedOrgs, positionid.equals(Position.ADMIN_POSITIONID));
		request.setAttribute("w1_orgTree", JSonFactory.bean2json(lOrgs));

		setData("w_orgTree", user.getDirectorgid() + "," + directOrg.getOrgname());

		setData("w1_orgid", w1_orgid);
		setData("w1_orgname", w1_orgname);
		setData("userid", getDto().getAsLong("userid"));

		setData("w_orgid_1", user.getDirectorgid());

		String isMutileOrg = SysConfig.getSysConfig("isMutileOrg", "false");
		if ("false".equals(isMutileOrg)) {
			setHideObj("w1_orgname");
		}
		return "toBatchPosition";
	}

	private List<Map<String, Object>> buildOrgTreeJSON(List<Org> orgs, List<Long> orgids, List<Org> affiliatedOrgs, boolean isDeveloper) {
		List<Map<String, Object>> lOrgs = new ArrayList<Map<String, Object>>();
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
				for (int i = 0; i < affiliatedOrgs.size(); i++) {
					Long orgid = affiliatedOrgs.get(i).getOrgid();
					if (orgid.equals(org.getOrgid())) {
						mOrg.put("checked", Boolean.valueOf(true));
						break;
					}
				}
				lOrgs.add(mOrg);
			} else {
				for (int i = 0; i < orgids.size(); i++) {
					Long orgid = orgids.get(i);
					if (orgid.equals(org.getOrgid())) {
						mOrg.put("admin", Boolean.valueOf(true));
						break;
					}
					if (i == orgids.size() - 1) {
						mOrg.put("nocheck", Boolean.valueOf(true));
					}
				}
				for (int i = 0; i < affiliatedOrgs.size(); i++) {
					Long orgid = affiliatedOrgs.get(i).getOrgid();
					if (orgid.equals(org.getOrgid())) {
						mOrg.put("checked", Boolean.valueOf(true));
						break;
					}
				}
				lOrgs.add(mOrg);
			}
		}
		return lOrgs;
	}

	private List<Map<String, Object>> buildOrgTreeJSON(List<Org> orgs, List<Long> opsitionids, boolean isDeveloper, List<Org> affiliatedOrgs) {
		List<Map<String, Object>> lOrgs = new ArrayList<Map<String, Object>>();
		for (Org org : orgs) {
			Map<String, Object> mOrg = org.toMap();
			if (affiliatedOrgs != null) {
				for (Org affiliatedOrg : affiliatedOrgs) {
					if (affiliatedOrg.getOrgid().equals(org.getOrgid())) {
						mOrg.put("checked", Boolean.valueOf(true));
						break;
					}
				}
			}
			if ("01".equals(org.getOrgtype())) {
				mOrg.put("iconSkin", "tree-depart-area");
			} else {
				mOrg.put("iconSkin", "tree-depart-labor");
			}
			if ((org.getOrglevel() != null) && (org.getOrglevel() != null) && (org.getOrglevel().longValue() < 3L) && ("1".equals(org.getIsleaf()))) {
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

	public String unLockUser() throws Exception {
		orgUserMgService.unLockUser(getDto().getAsLong("userid"), getDto().getUserInfo());
		setTopMsg("解锁成功");
		return JSON;
	}

	public String saveBatchOrg() throws Exception {
		List<Key> ids = getJsonParamAsList("ids");
		Long userid = getDto().getAsLong("userid");
		Long orgid = getDto().getAsLong("w_orgid");
		orgUserMgService.updateDirectAndAffiliatedOrgs(orgid, ids, userid, getDto().getUserInfo());
		return JSON;
	}

	public String toRestPass() throws Exception {
		return "toResetPass";
	}

	public String resetPassword() throws Exception {
		List list = getSelected("userGd");
		if (list.size() <= 0) {
			setMsg("请先选择需要重置密码的人员");
			setSuccess(false);
		} else {
			orgUserMgService.resetPassword(getDto().append("users", list));
			setMsg("重置成功");
		}
		return JSON;
	}

	public String queryAsyncOrgTree() throws Exception {
		return super.webQueryAsyncOrgTree();
	}

	public String queryUserPermission() throws Exception {
		Long userid = getDto().getAsLong("userid");
		List<UserPerrmissionVO> list = orgUserMgService.queryUserPerrmission(userid);
		setList("userPermission", list);
		setData("p_userid", userid);

		String isAudite = SysConfig.getSysConfig("isAudite", "false");
		if ("false".equals(isAudite)) {
			setData("isAudite", "false");
		} else {
			setData("isAudite", "true");
		}
		return "topermiss";
	}

	public String queryDataField() throws Exception {
		Long userid = Long.valueOf(request.getParameter("userid"));
		Long menuid = Long.valueOf(request.getParameter("menuid"));
		List<AppCode> list = orgUserMgService.queryDataField(userid, menuid);
		setList("yab139Grid", list);
		return "toDatafield";
	}

	public String queryUserPosition() throws Exception {
		Long userid = getDto().getAsLong("userid");
		List<Position> positions = orgUserMgService.queryUserPostions(userid);
		setList("positions", positions);
		return JSON;
	}

	public String queryLikeZhengzhi() throws Exception {
		List list = orgUserMgService.queryLikeZhengzhi(getDto());
		writeJsonToClient(list);
		return null;
	}
}
