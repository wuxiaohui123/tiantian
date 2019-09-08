package com.yinhai.ta3.coherence;

import java.util.Map;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.yinhai.ta3.redis.CacheService;

public class CoherenceService implements CacheService {
	private NamedCache cache = CacheFactory.getCache("$system$");

	@Override
	public void del(String key) {
		this.cache.remove(key);
	}

	@Override
	public boolean exists(String key) {
		return this.cache.containsKey(key);
	}

	@Override
	public void expire(String key, int paramInt) {
		this.cache.put(key, this.cache.get(key), paramInt);
	}

	@Override
	public Object get(String key) {
		return this.cache.get(key);
	}

	@Override
	public void set(String key, Object value) {
		this.cache.put(key, value);
	}

	@Override
	public void hset(String key, String field, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public String hget(String key, String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> hash(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hdel(String key, String... field) {
		// TODO Auto-generated method stub

	}
}
