package com.yinhai.sysframework.cache.spring;

import java.util.regex.Pattern;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yinhai.sysframework.cache.CacheService;

public class CacheInterceptor implements MethodInterceptor {

	private static Logger logger = LogManager.getLogger(CacheInterceptor.class.getName());

	//private List<CacheService> services = Collections.synchronizedList(new ArrayList<CacheService>());

	protected CacheService cacheService;

	private String cachePrefix;

	private String[] lapsePrefix;

	private Pattern lapsePattern;

	private String filter;

	public Object invoke(MethodInvocation mi) throws Throwable {
		if (cacheService == null) {
			logger.debug("缓存服务不可用");
			return mi.proceed();
		}

		String key = getKey(mi);
		String namespace = getNamespace(mi);

		if ((lapsePattern != null) && (lapsePattern.matcher(mi.getMethod().getName()).matches())) {
			cacheService.removeByNamespace(namespace);
		}

		if (!mi.getMethod().getName().startsWith(cachePrefix)) {
			return mi.proceed();
		}

		if (cacheService.get(namespace, key) != null) {
			logger.debug("缓存命中");
			return cacheService.get(namespace, key);
		}

		Object result = mi.proceed();
		if (result != null) {
			cacheService.put(namespace, key, result);
		}
		return result;
	}

	private String getKey(MethodInvocation mi) {
		StringBuilder key = new StringBuilder(100);
		key.append(mi.getMethod().getName());
		if (mi.getArguments() != null) {
			key.append("(");
			for (Object o : mi.getArguments()) {
				key.append(o.toString());
			}
			key.append(")");
		}
		return key.toString().replaceAll("\\s+", "");
	}

	public String getNamespace(MethodInvocation mi) {
		return mi.getMethod().getDeclaringClass().getName();
	}

	public String getCachePrefix() {
		return cachePrefix;
	}

	public void setCachePrefix(String cachePrefix) {
		this.cachePrefix = cachePrefix;
	}

	public String[] getLapsePrefix() {
		return lapsePrefix;
	}

	public void setLapsePrefix(String[] lapsePrefix) {
		this.lapsePrefix = lapsePrefix;
		if ((lapsePrefix != null) && (lapsePrefix.length > 0)) {
			StringBuilder regex = new StringBuilder(100);
			for (String s : lapsePrefix) {
				regex.append(s).append(".*").append("|");
			}
			lapsePattern = Pattern.compile(regex.toString());
		}
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

}
