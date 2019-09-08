package com.yinhai.sysframework.security.ta3;

import java.util.HashSet;
import java.util.Set;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class TaRefererStrategy {

	public static String SECURITY_EXTEND = ".do";
	public static boolean allowRemoteLogin = false;
	public static Set<String> loginUrl = new HashSet<String>();
	public static Set<String> exceptUrl = new HashSet<String>();
	public static PathMatcher pathMatcher = new AntPathMatcher();

	public static boolean isExceptUrl(String url) {
		for (String fn : exceptUrl) {
			if (pathMatcher.match(fn, url)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSecurity(String url) {
		if ((url.endsWith(SECURITY_EXTEND)) || (url.indexOf(SECURITY_EXTEND) != -1)) {
			return true;
		}
		return false;
	}

	public static boolean isLoginUrl(String url) {
		for (String fn : loginUrl) {
			if (url.startsWith(fn)) {
				return true;
			}
		}
		return false;
	}

	public static Set<String> getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(Set<String> loginUrl) {
		TaRefererStrategy.loginUrl = loginUrl;
	}

	public static boolean isAllowRemoteLogin() {
		return allowRemoteLogin;
	}

	public static void setAllowRemoteLogin(boolean allowRemoteLogin) {
		TaRefererStrategy.allowRemoteLogin = allowRemoteLogin;
	}

	public static Set<String> getExceptUrl() {
		return exceptUrl;
	}

	public void setExceptUrl(Set<String> exceptUrl) {
		TaRefererStrategy.exceptUrl = exceptUrl;
	}
}
