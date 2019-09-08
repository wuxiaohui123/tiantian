package com.yinhai.sso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.validation.Assertion;

import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.security.ta3.IUserLogin;
import com.yinhai.sysframework.security.ta3.TaSecurityStrategy;
import com.yinhai.sysframework.security.ta3.TaSessionManager;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.WebUtil;

public class Ta3Cas20TicketValidationFilter extends DefaultTicketValidateFilter {

	protected void registLoalUserSession(HttpServletRequest request, HttpServletResponse response, Assertion assertion) {
		IUserLogin service = (IUserLogin) ServiceLocator.getService("userLogin");
		if (WebUtil.getUserInfo(request) == null) {
			service.doUserLoginWithoutCheck(transSSOLoginIdToLocalLoginId(assertion.getPrincipal()), request);
		}
	}

	protected boolean isNeedAuthUrl(HttpServletRequest request, String url) {
		if (PrincipalHelper.retrievePrincipalFromSessionOrRequest(request) != null)
			return false;
		return TaSecurityStrategy.isDynamicurls(url);
	}

	protected void doLogoutCurrentSystem(HttpServletRequest request, HttpServletResponse response) {
		TaSessionManager sm = (TaSessionManager) ServiceLocator.getService("taSessionManager");
		HttpSession session = request.getSession(false);
		if ((!ValidateUtil.isEmpty(sm.getUsers())) && (session != null)) {
			IUser userInfo = WebUtil.getUserInfo(request);
			if (userInfo != null) {
				sm.invalidCurrentUsersession(userInfo.getLoginid(), session.getId());
			}
		}
	}
}
