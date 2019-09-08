package com.yinhai.sysframework.cache;

public interface CacheService {

	public abstract void put(String key, Object value);

	public abstract void put(String key, String paramString2, Object paramObject);

	public abstract Object get(String paramString);

	public abstract <T> T get(String paramString, Class<T> paramClass);

	public abstract Object get(String paramString1, String paramString2);

	public abstract Object remove(String paramString);

	public abstract void removeByNamespace(String paramString);
}
