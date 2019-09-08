package com.yinhai.ta3.organization.action;

import java.util.List;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.security.IPermissionService;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.ta3.organization.service.IPositionMgService;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.PositionInfoVO;
import com.yinhai.ta3.system.org.domain.UserInfoVO;

public class PositionSettingMgAction extends OrgBaseAction {

	private IPositionMgService positionMgService = (IPositionMgService) ServiceLocator.getService("positionMgService");
	private IPermissionService permissionServcie = (IPermissionService) super.getService("permissionServcie");

	public String execute() throws Exception {
		return "success";
	}

	public String webQueryAsyncOrgTree_p() throws Exception {
		return super.webQueryAsyncOrgTree();
	}

	public String queryPubPositions() throws Exception {
		ParamDTO dto = reBuildDto("p_", getDto());
		String positionType = dto.getAsString("positionType");
		if ("1".equals(positionType)) {
			dto.append("gridId", "positionGrid");

			PageBean positions = positionMgService.queryPubAndSharePositions(dto);
			setList("positionGrid", positions);
		} else if ("2".equals(positionType)) {
			PageBean positions = positionMgService.queryUsersByDto(dto);
			setList("userGrid", positions);
		}
		return "tojson";
	}

	public String toAssignUser() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		setData("positionid", positionid);
		request.setAttribute("orgTree", super.queryJsonOrgsTree(getDto()));
		List<UserInfoVO> users = positionMgService.getHaveThePositionUsersByPositionId(positionid);
		setList("selectedUserGrid", users);
		return "assignUser";
	}

	public String queryUsersByOrgId() throws Exception {
		ParamDTO dto = getDto();

		List<Key> userids = super.getJsonParamAsList("userids");
		dto.append("userids", userids);
		List<UserInfoVO> queryUsers = positionMgService.queryUsers(dto);
		setList("userGrid", queryUsers);
		return "tojson";
	}

	public String saveAssignUsers() throws Exception {
		List<Key> selected = getSelected("userGrid");
		Long positionid = getDto().getAsLong("positionid");
		positionMgService.saveAssignUsers(selected, getDto());
		for (Key key : selected) {
			permissionServcie.clearUserEffectivePositionsCache(key.getAsLong("userid"));
		}
		List<UserInfoVO> users = positionMgService.getHaveThePositionUsersByPositionId(positionid);
		setList("selectedUserGrid", users);
		return "tojson";
	}

	public String removeAssignUsers() throws Exception {
		List<Key> selected = getSelected("selectedUserGrid");
		positionMgService.removeAssignUsers(selected, getDto().getAsLong("positionid"), getDto().getUserInfo().getUserid());
		for (Key key : selected) {
			permissionServcie.clearUserEffectivePositionsCache(key.getAsLong("userid"));
		}
		return "tojson";
	}

	public String toAssignPositionsToUser() throws Exception {
		Long userid = getDto().getAsLong("userid");
		setData("userid", userid);
		request.setAttribute("orgTreeUserPosition", super.queryJsonOrgsTree(getDto()));
		List<PositionInfoVO> positions = positionMgService.getPubPositionsCurUserid(getDto().getAsLong("userid"));
		setList("positionPerGrid", positions);
		return "assignPositionsToUser";
	}

	public String saveUserAddPositions() throws Exception {
		List<Key> selected = getSelected("noPositionPerGrid");
		positionMgService.saveUserAddPositions(selected, getDto());
		permissionServcie.clearUserEffectivePositionsCache(getDto().getAsLong("userid"));
		return "tojson";
	}

	public String getPubPositionsNoCurUseridByOrgId() throws Exception {
		ParamDTO dto = getDto();
		System.out.println(dto);
		List<Position> positions = positionMgService.getPubPositionsNoCurUseridByOrgId(getDto());
		setList("noPositionPerGrid", positions);
		return "tojson";
	}

	public String removeUserPosition() throws Exception {
		positionMgService.removeUserPosition(getDto());
		Long userid = getDto().getAsLong("userid");
		permissionServcie.clearUserEffectivePositionsCache(userid);
		List<PositionInfoVO> positions = positionMgService.getPubPositionsCurUserid(userid);
		setList("positionPerGrid", positions);
		return "tojson";
	}

	public String getPubPositionsCurUserid() throws Exception {
		List<PositionInfoVO> positions = positionMgService.getPubPositionsCurUserid(getDto().getAsLong("userid"));
		setList("positionPerGrid", positions);
		return "tojson";
	}

	public String setMainPosition() throws Exception {
		Long positionid = getDto().getAsLong("positionid");
		Long userid = getDto().getAsLong("userid");
		positionMgService.setMainPosition(positionid, userid, getDto());
		permissionServcie.clearUserEffectivePositionsCache(userid);
		List<PositionInfoVO> positions = positionMgService.getPubPositionsCurUserid(userid);
		setList("positionPerGrid", positions);
		return "tojson";
	}
}
