package com.yinhai.ta3.organization.service.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.service.IOrgMgService;
import com.yinhai.ta3.organization.service.IOrgUserMgService;
import com.yinhai.ta3.organization.service.IUserMgService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.org.domain.UserPerrmissionVO;

public class OrgUserMgServiceImpl extends OrgBaseService implements IOrgUserMgService {

    private IOrgMgService orgMgService;
    private IUserMgService userMgService;

    public IUserMgService getUserMgService() {
        return userMgService;
    }

    public void setUserMgService(IUserMgService userMgService) {
        this.userMgService = userMgService;
    }

    public void setOrgMgService(IOrgMgService orgMgService) {
        this.orgMgService = orgMgService;
    }

    public IOrgMgService getOrgMgService() {
        return orgMgService;
    }

    public Org addOrg(ParamDTO dto) {
        return orgMgService.createOrg(dto);
    }

    public void editOrg(ParamDTO dto) {
        orgMgService.updateOrg(dto);
    }

    public User addUser(ParamDTO dto) {
        return userMgService.createUser(dto);
    }

    public void editUser(ParamDTO dto) {
        userMgService.updateUser(dto);
    }

    public void deleteOrg(ParamDTO dto) {
        orgMgService.deleteOrg(dto);
    }

    public Org queryOrgNode(ParamDTO dto) {
        return orgMgService.queryOrgNode(dto);
    }

    public String getMaxCostomNo(Long porgid) {
        return orgMgService.getMaxCostomNo(porgid);
    }

    public void sortOrg(List<Long> sortidslong, Long operator) {
        orgMgService.sortOrg(sortidslong, operator);
    }

    public PageBean queryUsersInfo(ParamDTO dto, String gdid, int start, int limit) {
        return userMgService.queryUsersInfo(dto, gdid, start, limit);
    }

    public void batchReUser(Long[] userids, ParamDTO dto) {
        userMgService.batchReUser(userids, dto);
    }

    public void unBatchUseUser(Long[] userids, ParamDTO dto) {
        userMgService.unBatchUseUser(userids, dto);
    }

    public void unLockUser(Long userid, IUser opUser) {
        userMgService.unLockUser(userid, opUser);
    }

    public User getUser(Long userid) {
        return userMgService.getUser(userid);
    }

    public List<Org> queryAffiliatedOrgs(Long userid) {
        return orgMgService.queryAffiliatedOrgs(userid);
    }

    public List<Org> querySubOrgs(Long porgid, boolean isGrandChildren, boolean isSelf, String effective) {
        return orgMgService.querySubOrgs(porgid, isGrandChildren, isSelf, effective);
    }

    public List<Long> queryPositionCouldManageOrgIds(Long positionid) {
        return orgMgService.queryPositionCouldManageOrgIds(positionid);
    }

    public void updateDirectAndAffiliatedOrgs(Long orgid, List<Key> ids, Long userid, IUser userInfo) {
        userMgService.updateDirectAndAffiliatedOrgs(orgid, ids, userid, userInfo);
    }

    public void resetPassword(ParamDTO append) {
        userMgService.resetPassword(append);
    }

    public void deleteUsers(List<Key> users, ParamDTO dto) {
        userMgService.deleteUsers(users, dto);
    }

    public List<Position> queryUserPostions(Long asLong) {
        return userMgService.queryUserPostions(asLong);
    }

    public List<UserPerrmissionVO> queryUserPerrmission(Long userid) {
        return userMgService.queryUserPerrmission(userid);
    }

    public List<AppCode> queryDataField(Long userid, Long menuid) {
        return userMgService.queryDataField(userid, menuid);
    }

    public List queryLikeZhengzhi(ParamDTO dto) {
        String positionnamelike = (dto.getAsString("positionnamelike") == null) || ("".equals(dto.getAsString("positionnamelike"))) ? dto.getAsString("positionnamelike1") : dto.getAsString("positionnamelike");
        if (ValidateUtil.isEmpty(positionnamelike)) {
            return null;
        }
        Query query = hibernateDao.createQuery("from " + super.getEntityClassName(Position.class.getName()) + " p where 1=1 and p.effective=:effective and p.positiontype=:positiontype and p.positionname like :positionname order by positionname");
        query.setString("effective", "0");
        query.setString("positiontype", "2");
        query.setString("positionname", "%" + positionnamelike + "%");
        return query.list();
    }

    public Map getDeputyInfo(Long orgId) {
        return orgMgService.getDeputyInfo(orgId);
    }

}
