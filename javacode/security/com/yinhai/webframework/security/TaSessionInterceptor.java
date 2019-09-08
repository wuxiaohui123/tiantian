package com.yinhai.webframework.security;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.yinhai.ta3.system.security.ta3.DefaultUserAccountInfo;

public class TaSessionInterceptor extends AbstractInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7053216352257160881L;

	private static Log logger = LogFactory.getLog(TaSessionInterceptor.class);

	protected Set<String> excludeActions = Collections.emptySet();
	protected Set<String> includeActions = Collections.emptySet();

	@Override
	public String intercept(ActionInvocation actionInvocation) throws Exception {
		ActionContext actionContext = ServletActionContext.getContext();
		Map<String, Object> session = actionContext.getSession();
		Action action = (Action) actionInvocation.getAction();
		if (excludeActions.size() > 0 && excludeActions.contains(action.getClass().getSimpleName())) {
			logger.debug("略过此[" + action.getClass().getName() + "]Action，不检测session是否存在或失效！");
			return actionInvocation.invoke();
		}
		if (includeActions.size() > 0 && includeActions.contains(action.getClass().getSimpleName())) {
			logger.debug("检测此[" + action.getClass().getName() + "]Action，检测session是否存在或失效！");
			DefaultUserAccountInfo defaultUserAccountInfo = (DefaultUserAccountInfo) session.get("ta3.userinfo");
			if (defaultUserAccountInfo == null) {
				return Action.LOGIN;
			} else {
				return actionInvocation.invoke();
			}
		}
		DefaultUserAccountInfo defaultUserAccountInfo = (DefaultUserAccountInfo) session.get("ta3.userinfo");
		if (defaultUserAccountInfo == null) {
			return Action.LOGIN;
		} else {
			return actionInvocation.invoke();
		}
	}

	public Set<String> getExcludeActions() {
		return excludeActions;
	}

	public void setExcludeActions(String excludeActions) {
		this.excludeActions = TextParseUtil.commaDelimitedStringToSet(excludeActions);
	}

	public Set<String> getIncludeActions() {
		return includeActions;
	}

	public void setIncludeActions(String includeActions) {
		this.includeActions = TextParseUtil.commaDelimitedStringToSet(includeActions);
	}

}
