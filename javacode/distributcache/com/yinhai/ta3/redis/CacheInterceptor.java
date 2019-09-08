package com.yinhai.ta3.redis;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.yinhai.sysframework.util.StringUtil;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.redis.annotation.CacheMethod;
import com.yinhai.ta3.redis.annotation.LapseMethod;

@Aspect
public class CacheInterceptor {

	private CacheService client;

	@Pointcut("@annotation(com.yinhai.ta3.redis.annotation.CacheMethod)")
	public void methodCachePointcut() {
	}

	@Pointcut("@annotation(com.yinhai.ta3.redis.annotation.LapseMethod)")
	public void lapseCachePointcut() {
	}

	@Pointcut(value = "@annotation(cacheMethod)", argNames = "cacheMethod")
	public void pointcut2(CacheMethod cacheMethod) {
	}

	@Pointcut(value = "@annotation(lapseMethod)", argNames = "lapseMethod")
	public void pointcutLapse(LapseMethod lapseMethod) {
	}

	@Around("methodCachePointcut() && pointcut2(cacheMethod)")
	public Object methodCacheHold(ProceedingJoinPoint joinPoint, CacheMethod cacheMethod) throws Throwable {
		Object ret = null;
		StringBuilder key = new StringBuilder(64);
		key.append(cacheMethod.prefix());
		key.append(joinPoint.getTarget().getClass().getName()).append(".").append(joinPoint.getSignature().getName());
		if ((joinPoint.getArgs() != null) && (joinPoint.getArgs().length > 0)) {
			Object[] arrayOfObject;
			int j = (arrayOfObject = joinPoint.getArgs()).length;
			for (int i = 0; i < j; i++) {
				Object arg = arrayOfObject[i];
				key.append(":").append(arg);
			}
		}

		try {
			ret = client.get(key.toString());
			if (ret == null) {
				ret = joinPoint.proceed();
				client.set(key.toString(), ret);
				if (cacheMethod.expires() > 0) {
					client.expire(key.toString(), cacheMethod.expires());
				}
				return ret;
			}

			else {
				MethodSignature signature = (MethodSignature) joinPoint.getSignature();
				Method method = signature.getMethod();
				Class<?> type = getGenericReturnTypeByMethod(method);
				if (String.class.equals(method.getReturnType())) {
					ret = EntityStringToObject(ret, method.getReturnType(), type);
				}
				if (Map.class.equals(method.getReturnType())) {
					ret = EntityStringToObject(ret, method.getReturnType(), type);
				}
				if (List.class.equals(method.getReturnType())) {
					ret = EntityStringToObject(ret, method.getReturnType(), type);
				}
				if (Set.class.equals(method.getReturnType())) {
					ret = EntityStringToObject(ret, method.getReturnType(), type);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Around("lapseCachePointcut() && pointcutLapse(lapseMethod)")
	public Object lapseCacheMethod(ProceedingJoinPoint joinPoint, LapseMethod lapseMethod) throws Throwable {
		StringBuilder key = new StringBuilder(64);
		key.append(lapseMethod.prefix());
		key.append(joinPoint.getTarget().getClass().getName()).append(".").append(lapseMethod.name());
		if ((joinPoint.getArgs() != null) && (joinPoint.getArgs().length > 0)) {
			Object[] arrayOfObject;
			int j = (arrayOfObject = joinPoint.getArgs()).length;
			for (int i = 0; i < j; i++) {
				Object arg = arrayOfObject[i];
				key.append(":").append(arg);
			}
		}
		Object ret = joinPoint.proceed();
		try {
			client.del(key.toString());
		} catch (Exception localException1) {
		}

		return ret;
	}

	private Class<?> getGenericReturnTypeByMethod(Method method) throws ClassNotFoundException {
		Type genericReturnType = method.getGenericReturnType();
		if (genericReturnType instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
			Type[] aTypes = parameterizedType.getActualTypeArguments();
			if (aTypes.length == 0) {
				return null;
			}
			String[] classArr = StringUtil.split(aTypes[0].toString(), " ");
			if (classArr.length < 2) {
				return null;
			}
			Class<?> classs = Class.forName(classArr[1]);
			if (classs.isInterface()) {
				return null;
			}
			return classs;
		}
		return null;
	}

	private Object EntityStringToObject(Object ret, Class<?> returnType, Class<?> type) {
		if (type != null) {
			ret = JSonFactory.jsonDeserialization(ret.toString(), returnType, type);
		} else {
			ret = JSonFactory.jsonDeserialization(ret.toString(), returnType);
		}
		return ret;
	}

	public CacheService getClient() {
		return client;
	}

	public void setClient(CacheService client) {
		this.client = client;
	}
}
