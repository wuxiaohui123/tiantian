package com.yinhai.sysframework.cache.ehcache;

import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.constructs.web.filter.SimplePageCachingFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PageEhCacheFilter extends SimplePageCachingFilter {

	private static final Logger log = LogManager.getLogger(PageEhCacheFilter.class);

	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {
		chain.doFilter(request, response);
	}

	private boolean headerContains(HttpServletRequest request, String header, String value) {
		logRequestHeaders(request);
		Enumeration<String> accepted = request.getHeaders(header);
		while (accepted.hasMoreElements()) {
			String headerValue = accepted.nextElement();
			if (headerValue.contains(value)) {
				return true;
			}
		}
		return false;
	}

	protected boolean acceptsGzipEncoding(HttpServletRequest request) {
		boolean ie6 = headerContains(request, "User-Agent", "MSIE 6.0");
		boolean ie7 = headerContains(request, "User-Agent", "MSIE 7.0");
		return (acceptsEncoding(request, "gzip")) || (ie6) || (ie7);
	}
}
