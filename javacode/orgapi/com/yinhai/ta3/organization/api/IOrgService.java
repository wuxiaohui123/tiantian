package com.yinhai.ta3.organization.api;

import java.util.List;

import com.yinhai.sysframework.iorg.IOrg;
import com.yinhai.ta3.system.org.domain.Org;

public interface IOrgService {

	public abstract List<IOrg> queryAllOrg();

	public abstract List<Org> querySubOrgs(Long paramLong, boolean paramBoolean1, boolean paramBoolean2, String paramString);

	public abstract Org createOrg(Org paramOrg, Long paramLong);

	public abstract void updateOrg(Org paramOrg, Long paramLong);

	public abstract boolean ascSortOrg(List<Long> paramList, Long paramLong);
}
