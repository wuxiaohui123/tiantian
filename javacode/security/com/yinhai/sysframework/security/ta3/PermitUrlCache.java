package com.yinhai.sysframework.security.ta3;

import java.util.HashMap;
import java.util.HashSet;

public class PermitUrlCache {

	private static HashMap<String, HashSet<String>> permitUrlCache = new HashMap<String, HashSet<String>>();

	public static void setPermitUrl(String sessionid, String url) {
		HashSet<String> h = permitUrlCache.computeIfAbsent(sessionid, k -> new HashSet<String>());
		h.add(url);
	}

	public static boolean hasPermitCache(String sessionid, String url) {
		HashSet<String> h = permitUrlCache.get(sessionid);
		if ((h != null) && (h.contains(url))) {
			return true;
		}
		return false;
	}

	public static void removeSession(String sessionid) {
		permitUrlCache.remove(sessionid);
	}
}
