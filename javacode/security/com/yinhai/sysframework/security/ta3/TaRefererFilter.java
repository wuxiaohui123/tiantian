package com.yinhai.sysframework.security.ta3;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.yinhai.sysframework.util.WebUtil;

public class TaRefererFilter implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filter) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String url = getAccessUrl(request);

		if (!TaRefererStrategy.isSecurity(url)) {
			filter.doFilter(req, resp);
			return;
		}

		if (TaRefererStrategy.isExceptUrl(url)) {
			filter.doFilter(req, resp);
			return;
		}
		HttpSession session = request.getSession(false);
		if (session == null) {
			if ((TaRefererStrategy.isLoginUrl(url)) && (!isRemoteLogin(request))) {
				accessDenied(url, request, response);
				return;
			}

			filter.doFilter(req, resp);
			return;
		}

		if (!refererAndCookieCheck(url, request, response)) {
			accessDenied(url, request, response);
			return;
		}
		filter.doFilter(req, resp);
	}

	protected void accessDenied(String url, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (WebUtil.isAjaxRequest(request)) {
			response.addHeader("_errorReferer", "true");
		} else {
			response.sendError(401, "无权限访问：" + url + " 无效访问来源！");
		}
	}

	protected boolean isRemoteLogin(HttpServletRequest request) {
		String jsonpCallback = request.getParameter("jsonpCallback");
		if (StringUtils.isNotBlank(jsonpCallback)) {
			return true;
		}
		return false;
	}

	protected boolean refererAndCookieCheck(String url, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		if (url.equals("/"))
			return true;
		HttpSession session = request.getSession(false);
		if ((TaRefererStrategy.isAllowRemoteLogin()) && (TaRefererStrategy.isLoginUrl(url)) && (isRemoteLogin(request))) {
			return true;
		}
		String origin = request.getHeader("Origin");
		String referer = request.getHeader("Referer");
		String host = request.getScheme() + "://" + request.getServerName()
				+ (80 == request.getServerPort() ? "" : new StringBuilder().append(":").append(request.getServerPort()).toString());
		String context = host + request.getContextPath();

		if ((StringUtils.isNotBlank(referer)) && (!"null".equals(referer)) && (!referer.trim().startsWith(context))) {
			return false;
		}

		if ((StringUtils.isNotBlank(origin)) && (!origin.trim().startsWith(host))) {
			return false;
		}

		if (TaRefererStrategy.isLoginUrl(url)) {
			return true;
		}

		String sessionPostId = (String) session.getAttribute("_POSTID");

		if (sessionPostId == null) {
			return true;
		}
		Cookie[] cookies = request.getCookies();
		String cookiePostId = "";

		if (cookies != null) {
			Cookie cookie = null;
			int i = 0;
			for (int len = cookies.length; i < len; i++) {
				cookie = cookies[i];
				if (cookie.getName().equals("POSTID")) {
					cookiePostId = cookie.getValue();
					if ((!StringUtils.isNotBlank(cookiePostId)) || (!cookiePostId.equals(sessionPostId)))
						break;
					return true;
				}
			}
		}

		return false;
	}

	protected String getAccessUrl(HttpServletRequest request) {
		String url = request.getRequestURI();
		url = url.substring(request.getContextPath().length(), url.length());
		return url;
	}

	public void init(FilterConfig config) throws ServletException {
	}
}
