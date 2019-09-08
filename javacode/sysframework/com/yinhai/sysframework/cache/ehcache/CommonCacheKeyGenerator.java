package com.yinhai.sysframework.cache.ehcache;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cache.interceptor.KeyGenerator;

public class CommonCacheKeyGenerator implements KeyGenerator {

	public String generateKey(MethodInvocation methodInvocation) {
		String targetClassName = methodInvocation.getThis().getClass().getName();
		String methodName = methodInvocation.getMethod().getName();
		Object[] arguments = methodInvocation.getArguments();
		return generateKey(targetClassName, methodName, arguments);
	}

	public String generateKey(Object... data) {
		return null;
	}

	private String generateKey(String targetClassName, String methodName, Object[] arguments) {
		StringBuffer key = new StringBuffer();
		key.append(targetClassName).append(".").append(methodName);
		if (arguments != null && arguments.length != 0) {
			Arrays.stream(arguments).forEach(argument -> key.append(".").append(argument));
		}
		return key.toString();
	}

	@Override
	public Object generate(Object object, Method method, Object... varArgs) {
		// TODO Auto-generated method stub
		return null;
	}
}
