package com.yinhai.sysframework.cache.ehcache;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

public class ParamCacheKeyGenerator implements KeyGenerator {

	private String generateKey(String targetClassName, String methodName, Object[] arguments) {
		StringBuffer key = new StringBuffer();

		if (arguments != null && arguments.length != 0) {
			for (int i = 0; i < arguments.length; i++) {
				if (i > 0) {
					key.append(".");
				}
				key.append(arguments[i]);
			}
		}
		return key.toString();
	}

	@Override
	public Object generate(Object paramObject, Method method, Object... paramVarArgs) {
		String targetClassName = method.getClass().getName();
		String methodName = method.getName();
		Object[] arguments = paramVarArgs;
		return generateKey(targetClassName, methodName, arguments);
	}
}
