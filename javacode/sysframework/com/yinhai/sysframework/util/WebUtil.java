package com.yinhai.sysframework.util;

import com.google.common.collect.Lists;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.security.IPermissionService;
import com.yinhai.sysframework.security.ta3.IUserAccountInfo;
import com.yinhai.sysframework.service.ServiceLocator;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@SuppressWarnings("unchecked")
public class WebUtil {

	public static IUser getUserInfo(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null)
			return null;
		Object u = getUserAccountInfo(request);
		if (u == null)
			return null;
		return ((IUserAccountInfo) u).getUser();
	}

	public static IUserAccountInfo getUserAccountInfo(HttpServletRequest request) {
		return (IUserAccountInfo) request.getSession().getAttribute(IConstants.USERINFO);
	}

	public static IMenu getCurrentMenu(HttpServletRequest request) {
		return (IMenu) request.getSession().getAttribute(IConstants.CURRENT_MENU);
	}

	public static List<IMenu> getCurrentUserPermissionMenus(HttpSession session) {
		return (List<IMenu>) session.getAttribute(IConstants.USER_PERVIEW_MENUS_FLAG);
	}

	public static Set<String> getCurrentUserPermissionUrls(HttpSession session) {
		return (Set<String>) session.getAttribute(IConstants.USER_PERVIEW_FLAG);
	}

	public static boolean isAdministrator(HttpServletRequest request) {
		IPermissionService ps = (IPermissionService) ServiceLocator.getService("permissionServcie");
		IUser userInfo = getUserInfo(request);
		if (userInfo == null)
			return false;
		return ps.isAdministrator(getUserInfo(request).getNowPosition());
	}

	public static List<IPosition> getUserPositions(HttpServletRequest request) {
		List<IPosition> positions = Lists.newArrayList();
		List<Object> list = (List<Object>) request.getAttribute(IConstants.USER_EFFECTIVE_POSITIONS);
		list.forEach(object -> {
			IPosition iposition = ConvertUtil.ObjectToPosition(object);
			if (iposition != null) {
				positions.add(iposition);
			}
		});
		return positions;
	}

	public static List<IPosition> getCurrentUserPositions(HttpSession session) {
		return (List<IPosition>) session.getAttribute(IConstants.USER_EFFECTIVE_POSITIONS);
	}

	public static boolean isAjaxRequest(HttpServletRequest request) {
		String isAjax = request.getHeader("x-requested-with");
		if ("XMLHttpRequest".equals(isAjax)) {
			return true;
		}
		return false;
	}

	public static final String getClientIp(HttpServletRequest request) {
		String ipString = request.getHeader("x-forwarded-for");
		if (ValidateUtil.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {
			ipString = request.getHeader("Proxy-Client-IP");
		}
		if (ValidateUtil.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {
			ipString = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ValidateUtil.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {
			ipString = request.getRemoteAddr();
		}

		String[] arr = ipString.split(",");
		for (String str : arr) {
			if (!"unknown".equalsIgnoreCase(str)) {
				ipString = str;
				break;
			}
		}
		return ipString;
	}

	public static String getUserAgent(HttpServletRequest request) {
		return request.getHeader("User-Agent");
	}

	public static String getRequestUrl(HttpServletRequest request) {
		return request.getRequestURI();
	}
}
