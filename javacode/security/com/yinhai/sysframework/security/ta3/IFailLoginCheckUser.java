package com.yinhai.sysframework.security.ta3;

public interface IFailLoginCheckUser {

	void setUserLocked(Long userid);

	void updateUserFaultNum(Long userId, int num);
}
