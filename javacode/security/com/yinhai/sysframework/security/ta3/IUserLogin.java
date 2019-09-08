package com.yinhai.sysframework.security.ta3;

import javax.servlet.http.HttpServletRequest;

import com.yinhai.sysframework.exception.AppException;

public interface IUserLogin {

	public static final String SERVICEKEY = "userLogin";

	public abstract void loginCheck(IUserAccountInfo paramIUserAccountInfo, String paramString) throws AppException;

	public abstract void regesitUserAccount(IUserAccountInfo paramIUserAccountInfo, HttpServletRequest paramHttpServletRequest);

	public abstract void doUserLoginWithoutCheck(String paramString, HttpServletRequest paramHttpServletRequest);

	public abstract IUserAccountInfo loadUserAccountInfo(String paramString, HttpServletRequest paramHttpServletRequest);
}
