package com.yinhai.ta3.system.org.dao;

import com.yinhai.sysframework.iorg.IUser;

public interface UserDao {

	 IUser getUserByLoginId(String loginId);

	 IUser getUser(Long userid);

	 void lockUser(Long userid);

	 int updateUserFaultNum(Long userid, Integer num);
}
