package com.yinhai.webframework.session;

import javax.servlet.http.HttpServletRequest;

import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.util.WebUtil;


public class UserSession {

	private IUser user;
	private HttpServletRequest request;

	
	public static UserSession getUserSession(HttpServletRequest request) {
		IUser userInfo = WebUtil.getUserInfo(request);
		UserSession userSession = new UserSession();
		userSession.setRequest(request);
		userSession.setUser(userInfo);
		return userSession;
	}

	
	public IUser getUser() {
		return user;
	}


	public void setUser(IUser user) {
		this.user = user;
	}


	public UserSession getCurrentBusiness() {
		return this;
	}


	public Object getSessionResource(String key) {
		return request.getSession().getAttribute(key);
	}

	public void putSessionResource(String key, Object obj) {
		request.getSession().setAttribute(key, obj);
	}

	
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
}
