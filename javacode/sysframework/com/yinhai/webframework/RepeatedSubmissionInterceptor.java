package com.yinhai.webframework;

import java.util.concurrent.ConcurrentSkipListSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.yinhai.sysframework.exception.AppException;

@SuppressWarnings("unchecked")
public class RepeatedSubmissionInterceptor extends AbstractInterceptor {

	private static Logger logger = LogManager.getLogger(RepeatedSubmissionInterceptor.class.getName());
	private static final long serialVersionUID = -7089590393078726344L;

	public static final String REPEATED_SUBMISSION = "_REPEATED_SUBMISSION";

	public String intercept(ActionInvocation invocation) throws Exception {
		String reInvocate = null;
		try {
			registerSubmitUrl(ServletActionContext.getRequest());
			reInvocate = invocation.invoke();
		} catch (Exception e) {
			throw e;
		} finally {
			removeSubmitUrl(ServletActionContext.getRequest());
		}
		return reInvocate;
	}

	private void removeSubmitUrl(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			ConcurrentSkipListSet<String> set = (ConcurrentSkipListSet<String>) session.getAttribute("_REPEATED_SUBMISSION");
			if (set != null) {
				set.remove(request.getRequestURI());
			}
		}
	}

	private void registerSubmitUrl(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			ConcurrentSkipListSet<String> set = (ConcurrentSkipListSet<String>) session.getAttribute("_REPEATED_SUBMISSION");
			if (set == null) {
				ConcurrentSkipListSet<String> requestSet = new ConcurrentSkipListSet<String>();
				session.setAttribute("_REPEATED_SUBMISSION", requestSet);
			} else {
				if (set.contains(request.getRequestURI())) {
					logger.error("发生重复提交错误地址为: ["+ request.getRequestURI()+"]");
					throw new AppException("请求正在处理中，无需重复提交");
				}
				set.add(request.getRequestURI());
			}
		}
	}

}
