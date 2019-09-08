package com.yinhai.ta3.system.security.ta3;

import javax.servlet.http.HttpSession;

import com.yinhai.sysframework.security.ta3.TaSessionManager;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.WebUtil;
import com.yinhai.webframework.BaseAction;

public class FormLogoutAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9144204364981070182L;

	public String execute() throws Exception {
		TaSessionManager sm = (TaSessionManager) ServiceLocator.getService("taSessionManager");
		HttpSession session = request.getSession(false);
		if ((!ValidateUtil.isEmpty(sm.getUsers())) && (session != null)) {
			if (ValidateUtil.isEmpty(getDto().getUserInfo())) {
				return "success";
			}

			sm.invalidCurrentUsersession(getDto().getUserInfo().getLoginid(), session.getId());
		}

		if (WebUtil.isAjaxRequest(request)) {
			writeSuccess();
			return null;
		}
		return "success";
	}
}
