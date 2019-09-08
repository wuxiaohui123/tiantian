package com.yinhai.ta3.organization.service;

import javax.jws.WebService;

import com.yinhai.sysframework.service.WsService;
import com.yinhai.ta3.system.org.domain.User;

@WebService
public interface IUserPassChangeService extends WsService {

	public static final String SERVICEKEY = "userPassChangeService";

	public abstract User getUser(String paramString);

	public abstract void resetPassword(Long paramLong, String paramString);

	public abstract String encodePassword(String paramString1, String paramString2);
}
