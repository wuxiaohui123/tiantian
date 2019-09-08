package com.yinhai.ta3.system.security.ta3;

import javax.servlet.http.HttpServletRequest;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.security.ta3.IUserAccountInfo;
import com.yinhai.sysframework.security.ta3.IUserLogin;
import com.yinhai.sysframework.util.StringUtil;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.WebUtil;
import com.yinhai.webframework.BaseAction;

public class FormLoginCheckAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7838276892629023490L;
	private IUserLogin userLogin = (IUserLogin) getService("userLogin");

	public String execute() throws Exception {
		String loginId = null;
		String password=null;
		loginId = obtainLoginId();
		password = obtainPassword();
		boolean ajaxRequest = WebUtil.isAjaxRequest(request);
		String errmsg = "";
		if (StringUtil.isEmpty(loginId)) {
			errmsg = "用户名不能为空";
		} else if (StringUtil.isEmpty(password)) {
			errmsg = "密码不能为空";
		} else if (!checkCodeValid(request)) {
			errmsg = "验证码输入错误";
		}
		if (StringUtil.isNotEmpty(errmsg)) {
			if (ajaxRequest) {
				writeFailure(errmsg);
				return null;
			}
			setSuccess(false);
			setMsg(errmsg, "error");
			request.setAttribute("AUTHENTICATION_EXCEPTION", errmsg);
			request.setAttribute("ret_url", request.getParameter("ret_url"));
			return "success";
		}
        
		IUserAccountInfo user = WebUtil.getUserAccountInfo(request);

		if ((!ValidateUtil.isEmpty(request.getParameter("_src"))) && (!"null".equals(request.getParameter("_src")))) {
			putSessionResource("_src", request.getParameter("_src"));
		}

		if ((user != null) && (loginId.equals(user.getLoginId()))) {
			if (ajaxRequest) {
				writeSuccess();
				return null;
			}
			return "loginSuccess";
		}

		try {
			IUserAccountInfo userAccount = userLogin.loadUserAccountInfo(loginId, request);
			userLogin.loginCheck(userAccount, password);
            
			userLogin.regesitUserAccount(userAccount, request);
			String postId = String.valueOf(Math.random());
			request.getSession(false).setAttribute("_POSTID", postId);
			response.addHeader("Set-Cookie", "POSTID=" + postId + ";Path=" + request.getContextPath() + ";HttpOnly");
		} catch (AppException e) {
			if (ajaxRequest) {
				writeFailure(e.getMessage());
				return null;
			}
			setSuccess(false);
			setMsg(e.getMessage(), "error");
			request.setAttribute("AUTHENTICATION_EXCEPTION", e.getMessage());
			request.setAttribute("ret_url", request.getParameter("ret_url"));
			return "success";
		}

		if (ajaxRequest) {
			writeSuccess();
			return null;
		}
		return "loginSuccess";
	}

	protected boolean checkCodeValid(HttpServletRequest request) {
		String captcha = (String) request.getSession().getAttribute("simpleCaptcha");
		String checkCode = obtainCheckCode();
		if ("true".equals(SysConfig.getSysConfig("useCheckCode", true, new String[0])) && captcha != null && !captcha.equals(checkCode)) {
			return false;
		}
		return true;
	}

	protected String obtainLoginId() {
		return request.getParameter("j_username");
	}

	protected String obtainPassword() {
		return request.getParameter("j_password");
	}

	protected String obtainCheckCode() {
		return request.getParameter("checkCode");
	}
}
