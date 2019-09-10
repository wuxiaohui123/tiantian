package com.yinhai.sysframework.security.ta3;

import javax.servlet.http.HttpServletRequest;

import com.yinhai.sysframework.exception.AppException;

public interface IUserLogin {

	String SERVICEKEY = "userLogin";

	void loginCheck(IUserAccountInfo paramIUserAccountInfo, String paramString) throws AppException;

	void regesitUserAccount(IUserAccountInfo paramIUserAccountInfo, HttpServletRequest paramHttpServletRequest);

	void doUserLoginWithoutCheck(String paramString, HttpServletRequest paramHttpServletRequest);

	IUserAccountInfo loadUserAccountInfo(String paramString, HttpServletRequest paramHttpServletRequest);
}
