package com.yinhai.ta3.organization.service;

import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.service.Service;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.org.domain.UserPerrmissionVO;

public interface IOrgUserMgService extends Service {

	String SERVICEKEY = "orgUserMgService";

	 Org addOrg(ParamDTO dto);

	 void editOrg(ParamDTO dto);

	 User addUser(ParamDTO dto);

	 void editUser(ParamDTO dto);

	 void deleteOrg(ParamDTO dto);

	 Org queryOrgNode(ParamDTO dto);

	 String getMaxCostomNo(Long porgid);

	 void sortOrg(List<Long> sortidslong, Long operator);

	 PageBean queryUsersInfo(ParamDTO dto, String gdid, int start, int limit);

	 void batchReUser(Long[] userids, ParamDTO dto);

	 void unBatchUseUser(Long[] userids, ParamDTO dto);

	 void unLockUser(Long userid, IUser opUser);

	 User getUser(Long userid);

	 List<Org> queryAffiliatedOrgs(Long userid);

	 List<Org> querySubOrgs(Long porgid, boolean isGrandChildren, boolean isSelf, String effective);

	 List<Long> queryPositionCouldManageOrgIds(Long positionid);

	 void updateDirectAndAffiliatedOrgs(Long orgid, List<Key> ids, Long userid, IUser userInfo);

	 void resetPassword(ParamDTO dto);

	 void deleteUsers(List<Key> users, ParamDTO dto);

	 List<Position> queryUserPostions(Long asLong);

	 List<UserPerrmissionVO> queryUserPerrmission(Long userid);

	 List<AppCode> queryDataField(Long userid, Long menuid);

	 List queryLikeZhengzhi(ParamDTO dto);

	 Map getDeputyInfo(Long orgId);
}
