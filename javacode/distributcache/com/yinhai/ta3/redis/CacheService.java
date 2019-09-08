package com.yinhai.ta3.redis;

import java.util.Map;

public interface CacheService {

	 void set(String key, Object value);

	 void hset(String key, String field, String value);

	 Object get(String key);

	 String hget(String key, String field);

	 Map<String, Object> hash(String key);

	 void hdel(String key,String... field);

	 void del(String key);

	 void expire(String key, int paramInt);

	 boolean exists(String key);
}
