package com.yinhai.sysframework.security.ta3;

import org.springframework.util.PathMatcher;

public class UrlPathMatcher {

	static PathMatcher pathMatcher = new org.springframework.util.AntPathMatcher();

	public static boolean pathMatchesUrl(Object path, String url) {
		return pathMatcher.match((String) path, url);
	}
}
