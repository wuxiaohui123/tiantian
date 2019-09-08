package com.yinhai.ta3.sysapp.syslogmg.action;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import com.yinhai.ta3.system.security.ta3.DefaultUserAccountInfo;
import com.yinhai.webframework.BaseAction;

public class RequestSessionLogAction extends BaseAction {

	public String request() throws IOException {
		HttpSession session = request.getSession(false);
		DefaultUserAccountInfo defaultUserAccountInfo = (DefaultUserAccountInfo) session.getAttribute("ta3.userinfo");
		if (defaultUserAccountInfo == null) {
			setData("noSession", true);
		} else {
			setData("noSession", false);
		}
		return JSON;
	}
}
 