package com.yinhai.sysframework.security.ta3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class TaSecurityStrategy {

	protected static String loginUrl = "/login.jsp";

	protected static String noPermitRedrectUrl = "/ta/accessdenied.jsp";
	protected static boolean allowRepeatedlyLogin = true;
	protected static HashSet<String> developers = new HashSet<String>();
	protected static HashSet<String> dynamicurls = new HashSet<String>();
	protected static HashSet<String> expEnds = new HashSet<String>();
	protected static HashSet<String> filterNone = new HashSet<String>();

	protected static HashMap<String, HashSet<String>> anyRolePermitUrl = new HashMap<String, HashSet<String>>();
	protected static HashSet<String> loginPermitUrl = new HashSet<String>();

	protected static HashSet<String> dynamicurlsCache = new HashSet<String>();

	protected static HashSet<String> anyRoleCache = new HashSet<String>();

	protected static HashSet<String> loginPermitCache = new HashSet<String>();

	public static String getLoginUrl() {
		return loginUrl;
	}

	public static boolean isDevelopers(String loginId) {
		return developers.contains(loginId);
	}

	public static boolean isDynamicurls(String url) {
		if (dynamicurlsCache.contains(url)) {
			return true;
		}
		if ((filterNone != null) && (filterNone.size() > 0)) {
			return !isFilterNone(url);
		}
		Iterator<String> iterator;
		if (getExpEnds() != null) {
			for (iterator = getExpEnds().iterator(); iterator.hasNext();) {
				String s1 = iterator.next();
				if (url.endsWith(s1)) {
					return false;
				}
			}
		}
		if (dynamicurls == null)
			return false;
		for (String dynamicurl : dynamicurls) {
			if (UrlPathMatcher.pathMatchesUrl(dynamicurl, url)) {
				dynamicurlsCache.add(url);
				return true;
			}
		}
		return false;
	}

	public static boolean isFilterNone(String url) {
		for (String fn : filterNone) {
			if (UrlPathMatcher.pathMatchesUrl(fn, url)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasRolePermit(java.util.Collection<IRoleAuthrity> roles, String url) {
		for (IRoleAuthrity r : roles) {
			HashSet<String> set = (HashSet<String>) anyRolePermitUrl.get(r.getRole());
			if (anyRoleCache.contains(r.getRole() + "_" + url)) {
				return true;
			}
			if ((set != null) && (set.size() != 0)) {
				Iterator<String> it = set.iterator();
				if (it.hasNext()) {
					if (UrlPathMatcher.pathMatchesUrl(it.next(), url))
						anyRoleCache.add(r.getRole() + "_" + url);
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isLoginPermitUrl(String url) {
		if (loginPermitCache.contains(url))
			return true;
		for (Iterator<String> it = loginPermitUrl.iterator(); it.hasNext();) {
			if (UrlPathMatcher.pathMatchesUrl(it.next(), url)) {
				loginPermitCache.add(url);
				return true;
			}
		}
		return false;
	}

	public void setLoginUrl(String loginUrl) {
		TaSecurityStrategy.loginUrl = loginUrl;
	}

	public void setDevelopers(HashSet<String> developers) {
		TaSecurityStrategy.developers = developers;
	}

	public void setDynamicurls(HashSet<String> dynamicurls) {
		TaSecurityStrategy.dynamicurls = dynamicurls;
	}

	public static final HashSet<String> getDynamicurls() {
		return dynamicurls;
	}

	public void setFilterNone(HashSet<String> filterNone) {
		TaSecurityStrategy.filterNone = filterNone;
	}

	public static HashSet<String> getFilterNone() {
		return filterNone;
	}

	public void setLoginPermitUrl(HashSet<String> loginPermitUrl) {
		TaSecurityStrategy.loginPermitUrl = loginPermitUrl;
	}

	public void setAnyRolePermitUrl(HashMap<String, HashSet<String>> anyRolePermitUrl) {
		TaSecurityStrategy.anyRolePermitUrl = anyRolePermitUrl;
	}

	public static String getNoPermitRedrectUrl() {
		return noPermitRedrectUrl;
	}

	public void setNoPermitRedrectUrl(String noPermitRedrectUrl) {
		TaSecurityStrategy.noPermitRedrectUrl = noPermitRedrectUrl;
	}

	public static boolean isAllowRepeatedlyLogin() {
		return allowRepeatedlyLogin;
	}

	public void setAllowRepeatedlyLogin(boolean allowRepeatedlyLogin) {
		TaSecurityStrategy.allowRepeatedlyLogin = allowRepeatedlyLogin;
	}

	public static final HashSet<String> getExpEnds() {
		return expEnds;
	}

	public static final void setExpEnds(HashSet<String> expEnds) {
		TaSecurityStrategy.expEnds = expEnds;
	}
}
