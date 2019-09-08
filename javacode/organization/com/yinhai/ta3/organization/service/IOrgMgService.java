package com.yinhai.ta3.organization.service;

import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.iorg.IOrg;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.UserInfoVO;

public interface IOrgMgService {

	public static final String SERVICEKEY = "orgMgService";

	public abstract Org createOrg(ParamDTO paramParamDTO);

	public abstract void updateOrg(ParamDTO paramParamDTO);

	public abstract Org queryOrgNode(ParamDTO paramParamDTO);

	public abstract String getMaxCostomNo(Long paramLong);

	public abstract Integer getMaxSortNo(ParamDTO paramParamDTO);

	public abstract List<Org> querySubOrgs(Long paramLong, boolean paramBoolean1, boolean paramBoolean2,
			String paramString);

	public abstract List<Long> queryPositionCouldManageOrgIds(Long paramLong);

	public abstract boolean sortOrg(List<Long> paramList, Long paramLong);

	public abstract void unUseOrg(ParamDTO paramParamDTO);

	public abstract void reUseOrg(ParamDTO paramParamDTO);

	public abstract void checkOrgInOrgMg(Long paramLong1, Long paramLong2);

	public abstract void deleteOrg(ParamDTO paramParamDTO);

	public abstract List<UserInfoVO> getUserInfo(ParamDTO paramParamDTO);

	public abstract Map getDeputyInfo(Long paramLong);

	public abstract List getManagers(String paramString1, Long paramLong, String paramString2);

	public abstract List<Org> queryAffiliatedOrgs(Long paramLong);

	public abstract IOrg getOrgByOrgName(String paramString1, String paramString2);

	public abstract List<IOrg> queryOrgsByOrgName(String paramString);
}
