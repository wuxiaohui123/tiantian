package com.yinhai.ta3.organization.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.organization.service.IAdminMgService;
import com.yinhai.ta3.organization.service.IOrgMgService;
import com.yinhai.ta3.organization.service.IPositionMgService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.org.domain.Position;

public class PositionMgAction extends OrgBaseAction {

	private IPositionMgService positionMgService = (IPositionMgService) super.getService("positionMgService");

	private IOrgMgService orgMgService = (IOrgMgService) super.getService("orgMgService");

	private IAdminMgService adminMgService = (IAdminMgService) super.getService("adminMgService");

	public String execute() throws Exception {
		request.setAttribute("orgTree", super.queryJsonOrgsTree(getDto()));

		return "success";
	}

	public String queryPositions() throws Exception {
		ParamDTO dto = getDto();
		dto.put("positionType", "1");
		PageBean pb = positionMgService.getDescendantsPositionsByCount("positionPerGrid", getDto());
		setList("positionPerGrid", pb);
		return "tojson";
	}

	public String addPosition() throws Exception {
		request.setAttribute("orgTree", super.queryJsonOrgsTree(getDto()));
		setData("positioncategory", "01");
		return "toAdd";
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
		return "toAdd";
	}

	public String createPosition() throws Exception {
		getDto().put("positiontype", "1");
		Position p = positionMgService.createPosition(getDto());
		setData("newPositionId", p.getPositionid());
		return "tojson";
	}

	public String updatePosition() throws Exception {
		Long positionid = getDto().getAsLong("editpositionid");
		getDto().put("positionid", positionid);
		positionMgService.updatePosition(getDto());
		return "tojson";
	}

	public String deletePositions() throws Exception {
		List<Key> list = getSelected("positionPerGrid");
		positionMgService.removePosition(list, getDto());
		return "tojson";
	}

	public String unUsePosition() throws Exception {
		List<Key> list = getSelected("positionPerGrid");

		positionMgService.unUsePosition(list, getDto());
		return "tojson";
	}

	public String usePosition() throws Exception {
		List<Key> list = getSelected("positionPerGrid");

		positionMgService.usePosition(list, getDto());
		return "tojson";
	}

	public String toOperWin() throws Exception {
		if (ValidateUtil.isEmpty(getDto().getAsString("positionid"))) {
			throw new AppException("新增岗位id为空!");
		}
		setData("positionid", getDto().getAsString("positionid"));

		setData("positionname", getDto().getAsString("positionname"));
		return "operWin";
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

	public String toFuncOpPurview() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		setData("usePermissionPositionid", positionid);
		setData("positionType", getDto().getAsString("positionType"));

		List<PermissionTreeVO> nodes = positionMgService.getUsePermissionTreeByPositionId(positionid);

		Long curPositionid = getDto().getUserInfo().getNowPosition().getPositionid();
		List<PermissionTreeVO> curNodes = positionMgService.getRePermissionTreeByPositionId(curPositionid);

		List<PermissionTreeVO> newNodes = new ArrayList();
		PermissionTreeVO newNode = null;
		for (int i = 0; i < curNodes.size(); i++) {
			newNode = (PermissionTreeVO) curNodes.get(i);
			Iterator localIterator = nodes.iterator();
			while (localIterator.hasNext()) {
				PermissionTreeVO node = (PermissionTreeVO) localIterator.next();
				if ((newNode.getId().longValue() == node.getId().longValue()) && (newNode.getPId().equals(node.getPId()))) {
					if (node.getNocheck()) {
						newNode.setNocheck(true);
					} else {
						newNode.setChecked(true);
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
		setShowObj("saveScopeOpBtn");
		setHideObj("personalsaveScopeOpBtn");
		return "toFuncOpPurview";
	}

	public String toRecyclePermissions() throws Exception {
		String positionids = request.getParameter("positionids");
		setData("recyclePermissionsPositionids", positionids);
		setData("positionType", getDto().getAsString("positionType"));

		List<PermissionTreeVO> nodes = positionMgService.getRePermissionTreeByPositionId(getDto().getUserInfo().getNowPosition().getPositionid());

		request.setAttribute("opTree", buildRecyleTree(nodes));
		setHideObj("personalrecyclePermissionsOpBtn");
		setShowObj("recyclePermissionsOpBtn");
		return "recyclePermissions";
	}

	public String toGrantUsePermissions() throws Exception {
		String positionids = request.getParameter("positionids");
		setData("grantPermissionsPositionids", positionids);
		setData("positionType", getDto().getAsString("positionType"));

		List<PermissionTreeVO> nodes = positionMgService.getRePermissionTreeByPositionId(getDto().getUserInfo().getNowPosition().getPositionid());

		request.setAttribute("opTree", buildRecyleTree(nodes));
		setHideObj("personalrecyclePermissionsOpBtn");
		setShowObj("recyclePermissionsOpBtn");
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
}
