package com.yinhai.sysframework.security.ta3;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yinhai.sysframework.app.domain.jsonmodel.ResultBean;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.event.EventSource;
import com.yinhai.sysframework.event.TaEventPublisher;
import com.yinhai.sysframework.menu.IMenuService;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.StringUtil;
import com.yinhai.sysframework.util.WebUtil;
import com.yinhai.sysframework.util.json.JSonFactory;

@SuppressWarnings("unchecked")
public class Ta3SecurityFilter implements Filter {
	private static Logger log = LogManager.getLogger(Ta3SecurityFilter.class.getName());

	protected void checkPermit(String url, IUserAccountInfo user, HttpServletRequest request) throws NoPermissionException {
		if (TaSecurityStrategy.isDevelopers(user.getLoginId())) {
			return;
		}
		HttpSession session = request.getSession(false);

		if (TaSecurityStrategy.isLoginPermitUrl(url)) {
			return;
		}

		if (PermitUrlCache.hasPermitCache(session.getId(), url)) {
			return;
		}

		if (TaSecurityStrategy.hasRolePermit(user.getRoles(), url)) {
			return;
		}

		Set<String> perviewSet = (Set<String>) session.getAttribute("__USER_PERVIEW_FLAG__");
		String loginfo = " User:" + user.getLoginId() + ",无访问权限！ Url:" + url;
		if (perviewSet == null || perviewSet.size() == 0) {
			throw new NoPermissionException(loginfo);
		}

		if (perviewSet.contains(url)) {
			PermitUrlCache.setPermitUrl(session.getId(), url);
			return;
		}
		IMenuService ms = (IMenuService) ServiceLocator.getService("menuService");
		Set<String> allMenusUrl = ms.getAllMenusUrl();
		if (allMenusUrl.contains(url)) {
			throw new NoPermissionException(loginfo);
		}

		int b = url.indexOf("!");
		String urlTmp = url;
		if (b > 0) {
			urlTmp = urlTmp.substring(0, b) + ".do";
		} else {
			int c = url.indexOf(";jessionid");
			if (c > 0) {
				urlTmp = urlTmp.substring(0, c);
			}
		}
		if (perviewSet.contains(urlTmp)) {
			PermitUrlCache.setPermitUrl(session.getId(), url);
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug(loginfo);
		}
		throw new NoPermissionException(loginfo);
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filter) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String url = getAccessUrl(request);

		IUserAccountInfo user = WebUtil.getUserAccountInfo(request);

		if (TaSecurityStrategy.getFilterNone().size() > 0) {
			for (String expend : TaSecurityStrategy.getExpEnds()) {
				if (url.endsWith(expend)) {
					filter.doFilter(req, resp);
					return;
				}
			}

			if (TaSecurityStrategy.isFilterNone(url)) {
				filter.doFilter(req, resp);
				return;
			}

			if (!sessionAndPermitCheck(user, url, request, response, filter))
				return;
			filter.doFilter(req, resp);
			return;
		}

		if ((TaSecurityStrategy.getDynamicurls().size() >= 0) && (TaSecurityStrategy.getFilterNone().size() == 0)) {
			if ((TaSecurityStrategy.isDynamicurls(url)) && (!sessionAndPermitCheck(user, url, request, response, filter))) {
				return;
			}
		}
		filter.doFilter(req, resp);
	}

	public boolean sessionAndPermitCheck(IUserAccountInfo user, String url, HttpServletRequest request, HttpServletResponse response,
			FilterChain filter) throws IOException, ServletException {
		if (user == null) {
			TaSessionManager sm = (TaSessionManager) ServiceLocator.getService("taSessionManager");
			Map<String, String> sessionMap = sm.getExpiredSession();
			if (request.getSession() != null) {
				String sessionId = request.getSession().getId();
				if (sessionMap.get(sessionId) != null) {
					request.getSession().invalidate();
					sessionMap.remove(sessionId);
					sendtoLoginUrl(getLastRequestUrl(request), request, response);
				} else {
					gotoLoginUrl(getLastRequestUrl(request), request, response);
				}
			} else {
				gotoLoginUrl(getLastRequestUrl(request), request, response);
			}
			return false;
		}
		try {
			checkPermit(url, user, request);
		} catch (NoPermissionException e) {
			gotoAccessDenied(request, response);
			return false;
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	protected String getLastRequestUrl(HttpServletRequest request) {
		String qs = request.getQueryString();
		return URLEncoder.encode(request.getRequestURL()
				+ ((qs == null) || ("".equals(qs)) ? "" : new StringBuilder().append("?").append(qs).toString()));
	}

	protected void writeJson(Object obj, HttpServletResponse response) throws IOException {
		response.setContentType("text/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write(JSonFactory.bean2json(obj));
		writer.flush();
	}

	protected void gotoLoginUrl(String ret_url, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (WebUtil.isAjaxRequest(request)) {
			response.addHeader("__timeout", "true");
			writeJson(new ResultBean(false), response);
		} else {
			if (StringUtil.isEmpty(ret_url)) {
				ret_url = "";
			} else {
				ret_url = "?ret_url=" + ret_url;
			}
			response.sendRedirect(request.getContextPath() + TaSecurityStrategy.getLoginUrl() + ret_url);
		}
	}

	protected void sendtoLoginUrl(String ret_url, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (WebUtil.isAjaxRequest(request)) {
			response.addHeader("__samelogin", "true");
			writeJson(new ResultBean(false), response);
		} else {
			response.sendRedirect(request.getContextPath() + TaSecurityStrategy.getLoginUrl() + "?samelogin=true");
		}
	}

	protected void gotoAccessDenied(HttpServletRequest request, HttpServletResponse response) throws IOException {
		IUserAccountInfo user = WebUtil.getUserAccountInfo(request);
		String url = getAccessUrl(request);
		ParamDTO dto = new ParamDTO();
		dto.put("user", user.getUser());
		dto.put("url", url.substring(1));
		dto.put("ispermission", "0");

		TaEventPublisher.publishEvent(new EventSource(request, dto), "access_log");
		response.sendRedirect(request.getContextPath() + TaSecurityStrategy.getNoPermitRedrectUrl());
	}

	protected String getAccessUrl(HttpServletRequest request) {
		String url = request.getRequestURI();
		url = url.substring(request.getContextPath().length(), url.length());
		return url;
	}

	public void init(FilterConfig arg0) throws ServletException {
	}

	public void destroy() {
	}
}
