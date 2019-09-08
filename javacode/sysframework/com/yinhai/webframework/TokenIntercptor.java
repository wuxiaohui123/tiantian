package com.yinhai.webframework;

import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.util.WebUtil;

public class TokenIntercptor extends AbstractInterceptor {

	private static final long serialVersionUID = -7089590393078726344L;
	public static String TA_SUBMITKEYTOKEN = "_SUBMIT_KEY";
	public static String TA_SUBMIT_NONE = "NONE";

	public String intercept(ActionInvocation invocation) throws Exception {
		Map<String, Parameter> params = ActionContext.getContext().getParameters();
		String[] submitKeyParams = params.get(TA_SUBMITKEYTOKEN).getMultipleValues();
		HttpServletResponse response = ServletActionContext.getResponse();

		HttpSession session = ServletActionContext.getRequest().getSession(true);
		IMenu currentMenu = WebUtil.getCurrentMenu(ServletActionContext.getRequest());
		if (currentMenu == null) {
			return invocation.invoke();
		}

		String menuKey = String.valueOf(currentMenu.getMenuid());

		String sessionRealKey = (String) session.getAttribute(TA_SUBMITKEYTOKEN + menuKey);

		if ((submitKeyParams != null) && (submitKeyParams.length > 0)) {
			if ((sessionRealKey == null) || (sessionRealKey.equals(submitKeyParams[0]))
					|| (TA_SUBMIT_NONE.equals(submitKeyParams[0]))) {
				String uuid = UUID.randomUUID().toString();
				session.setAttribute(TA_SUBMITKEYTOKEN + menuKey, uuid);
				response.setHeader(TA_SUBMITKEYTOKEN, uuid);

				return invocation.invoke();
			}

			response.setHeader(TA_SUBMITKEYTOKEN, "error");
			return null;
		}

		return invocation.invoke();
	}
}
