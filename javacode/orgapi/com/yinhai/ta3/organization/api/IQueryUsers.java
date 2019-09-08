package com.yinhai.ta3.organization.api;

import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.ta3.system.org.domain.User;

public interface IQueryUsers {

	public abstract PageBean queryUsers(User paramUser, Long paramLong1, Long paramLong2, boolean paramBoolean, int paramInt1, int paramInt2);
}
