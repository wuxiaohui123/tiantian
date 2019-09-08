package com.yinhai.ta3.organization.action;

import java.util.ArrayList;
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
import com.yinhai.ta3.organization.service.IOrgMgService;
import com.yinhai.ta3.organization.service.IPositionMgService;
import com.yinhai.ta3.organization.service.IUserMgService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.org.domain.UserPerrmissionVO;

public class UserMgAction extends OrgBaseAction {

	public IUserMgService userMgService = (IUserMgService) ServiceLocator.getService("userMgService");

	public IPositionMgService positionMgService = (IPositionMgService) ServiceLocator.getService("positionMgService");

	public IOrgMgService orgMgService = (IOrgMgService) ServiceLocator.getService("orgMgService");

	public String execute() throws Exception {
		request.setAttribute("orgTree", super.queryJsonOrgsTree(getDto()));
		return super.execute();
	}

	public String webToAddUser() throws Exception {
		request.setAttribute("w_orgTree", super.queryJsonOrgsTree(getDto()));
		request.setAttribute("w1_orgTree", super.queryJsonOrgsTree(getDto()));

		if (SysConfig.getSysconfigToBoolean("allowMultiOrg"))
			return "toBatchAddUser";
		return "toAddUser";
	}

	public String webResetPassword() throws Exception {
		List list = getSelected("userGd");
		if (list.size() <= 0) {
			setMsg("请先选择需要重置密码的人员");
			setSuccess(false);
		} else {
			userMgService.resetPassword(getDto().append("users", list));
			setMsg("重置成功");
		}
		return "tojson";
	}

	public String webBatchOrgsUserSave() throws Exception {
		ParamDTO reBuildDto = reBuildDto("w_", getDto());
		String orgs = reBuildDto.getAsString("orgs");
		if (ValidateUtil.isNotEmpty(orgs)) {
			List<Key> jsonStrToList = jsonStrToList(orgs);
			Long[] orgids = new Long[jsonStrToList.size()];
			for (int i = 0; i < jsonStrToList.size(); i++) {
				orgids[i] = ((Key) jsonStrToList.get(i)).getAsLong("orgid");
			}
			reBuildDto.put("orgs", orgids);
			User user = userMgService.createUser(reBuildDto);
			if (user.getUserid() == null) {
				setSuccess(false);
				setMsg("保存失败");
			}
		} else {
			setMsg("至少选择一个组织");
		}
		return "tojson";
	}

	public String webUserSave() throws Exception {
		ParamDTO reBuildDto = reBuildDto("w_", getDto());
		userMgService.createUser(reBuildDto);
		return "tojson";
	}

	public String webDeleteUsers() throws Exception {
		List<Key> users = getSelected("userGd");
		userMgService.deleteUsers(users, getDto());
		return "tojson";
	}

	public String webQueryUsers() throws Exception {
		ParamDTO dto = getDto();
		int start = dto.getStart("userGd").intValue();
		int limit = dto.getLimit("userGd").intValue();
		PageBean queryUsersInfo = userMgService.queryUsersInfo(dto, "userGd", start, limit);

		setList("userGd", queryUsersInfo);
		return "tojson";
	}

	public String webToUserPermiss() throws Exception {
		Long userid = getDto().getAsLong("userid");
		List<UserPerrmissionVO> list = userMgService.queryUserPerrmission(userid);
		setList("userPermission", list);
		setData("p_userid", userid);
		return "topermiss";
	}

	public String webToUserOpLog() throws Exception {
		setData("w_userid", getDto().getAsString("userid"));
		return "tooplog";
	}

	public String webQueryOpLogs() throws Exception {
		PageBean pb = userMgService.queryUserOpLogs("userOpLog", getDto().append("userid", getDto().getAsLong("w_userid")));
		setList("userOpLog", pb);
		return "tojson";
	}

	public String webUnEffectiveUsers() throws Exception {
		List<Key> selected = getSelected("userGd");
		Long[] userids = new Long[selected.size()];
		for (int i = 0; i < selected.size(); i++) {
			userids[i] = selected.get(i).getAsLong("userid");
		}
		userMgService.unBatchUseUser(userids, getDto());
		return "tojson";
	}

	public String webEffectiveUsers() throws Exception {
		List<Key> selected = getSelected("userGd");
		Long[] userids = new Long[selected.size()];
		for (int i = 0; i < selected.size(); i++) {
			userids[i] = ((Key) selected.get(i)).getAsLong("userid");
		}
		userMgService.batchReUser(userids, getDto());
		return "tojson";
	}

	public String webUnLockUser() throws Exception {
		userMgService.unLockUser(getDto().getAsLong("userid"), getDto().getUserInfo());
		setTopMsg("解锁成功");
		return "tojson";
	}

	public String webToEdit() throws Exception {
		User user = userMgService.getUser(getDto().getAsLong("userid"));
		setData(user.toMap(), true);
		return "toEdit";
	}

	public String webUpdateUser() throws Exception {
		userMgService.updateUser(reBuildDto("w_", getDto()));
		return "tojson";
	}

	public String webQueryUserPosition() throws Exception {
		List<Position> positons = userMgService.queryUserPostions(getDto().getAsLong("userid"));
		setList("positions", positons);
		return "toPositions";
	}

	public String webToRestPass() throws Exception {
		return "toResetPass";
	}

	public String webToBatchPosition() {
		List<Org> orgs = orgMgService.querySubOrgs(getDto().getUserInfo().getNowPosition().getOrgid(), true, true, "0");

		List<Map<String, Object>> orgstree = new ArrayList<>();
		List<Key> jsonParamAsList = getJsonParamAsList("users");
		if (jsonParamAsList.size() == 1) {
			List<Position> positions = userMgService.queryUserPersionalPostions(jsonParamAsList.get(0).getAsLong("userid"));
			for (Org o : orgs)
				if (o.getOrgid().equals(((Position) positions.get(0)).getOrgid())) {
					Map<String, Object> map = o.toMap();
					map.put("checked", Boolean.valueOf(true));
					orgstree.add(map);
				} else {
					orgstree.add(o.toMap());
				}
			String bean2json = JSonFactory.bean2json(orgstree);
			request.setAttribute("positionsTree", bean2json);
		} else {
			String bean2json = JSonFactory.bean2json(orgs);
			request.setAttribute("positionsTree", bean2json);
		}
		return "toBatchPosition";
	}

	public String webToUpdateOrg() throws Exception {
		User user = userMgService.getUser(getDto().getAsLong("userid"));

		Org directOrg = orgMgService.queryOrgNode(getDto().append("orgid", user.getDirectorgid()));

		List<Org> affiliatedOrgs = orgMgService.queryAffiliatedOrgs(getDto().getAsLong("userid"));
		StringBuilder w1_orgid = new StringBuilder();
		StringBuilder w1_orgname = new StringBuilder();
		for (int i = 0; i < affiliatedOrgs.size(); i++) {
			if (i != affiliatedOrgs.size() - 1) {
				w1_orgid.append(affiliatedOrgs.get(i).getOrgid()).append(",");
				w1_orgname.append(affiliatedOrgs.get(i).getOrgname()).append(",");
			} else {
				w1_orgid.append(affiliatedOrgs.get(i).getOrgid());
				w1_orgname.append(affiliatedOrgs.get(i).getOrgname());
			}
		}

		request.setAttribute("w_orgTree", super.queryJsonOrgsTree(getDto()));
		List<Org> orgs = orgMgService.querySubOrgs(Org.ORG_ROOT_ID, true, true, "-1");
		Long positionid = getDto().getUserInfo().getNowPosition().getPositionid();
		List<Long> orgids = orgMgService.queryPositionCouldManageOrgIds(positionid);
		List<Map<String, Object>> lOrgs = buildOrgTreeJSON(orgs, orgids, affiliatedOrgs, positionid.equals(Position.ADMIN_POSITIONID));
		request.setAttribute("w1_orgTree", JSonFactory.bean2json(lOrgs));

		setData("w_orgTree", user.getDirectorgid() + "," + directOrg.getOrgname());

		setData("w1_orgid", w1_orgid.toString());
		setData("w1_orgname", w1_orgname.toString());
		setData("userid", getDto().getAsLong("userid"));

		setData("w_orgid_1", user.getDirectorgid());
		return "toBatchPosition";
	}

	private List<Map<String, Object>> buildOrgTreeJSON(List<Org> orgs, List<Long> orgids, List<Org> affiliatedOrgs, boolean isDeveloper) {
		List<Map<String, Object>> lOrgs = new ArrayList<>();
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
					Long orgid = ((Org) affiliatedOrgs.get(i)).getOrgid();
					if (orgid.equals(org.getOrgid())) {
						mOrg.put("checked", Boolean.valueOf(true));
						break;
					}
				}
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
				for (int i = 0; i < affiliatedOrgs.size(); i++) {
					Long orgid = ((Org) affiliatedOrgs.get(i)).getOrgid();
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

	public String webSaveBatchPosition() {
		List<Key> users = getJsonParamAsList("users");
		Long[] userids = new Long[users.size()];
		getDto().put("orgid", getDto().getAsLong("wp_orgid"));
		for (int i = 0; i < users.size(); i++) {
			userids[i] = ((Key) users.get(i)).getAsLong("userid");
		}
		userMgService.batchChangeUserOrg(userids, getDto());
		return "tojson";
	}

	public String webSaveBatchOrg() throws Exception {
		List<Key> ids = getJsonParamAsList("ids");
		Long userid = getDto().getAsLong("userid");
		Long orgid = getDto().getAsLong("w_orgid");
		userMgService.updateDirectAndAffiliatedOrgs(orgid, ids, userid, getDto().getUserInfo());
		return "tojson";
	}

	public String queryDataField() throws Exception {
		Long userid = Long.valueOf(request.getParameter("userid"));
		Long menuid = Long.valueOf(request.getParameter("menuid"));
		List<AppCode> list = userMgService.queryDataField(userid, menuid);
		setList("yab139Grid", list);
		return "toDatafield";
	}

	public String toUserInfo() throws Exception {

		return "toUserInfo";
	}

	private List<Map<String, Object>> buildOrgTreeJSON(List<Org> orgs, List<Long> opsitionids, boolean isDeveloper, List<Org> affiliatedOrgs) {
		List<Map<String, Object>> lOrgs = new ArrayList<>();
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
}
