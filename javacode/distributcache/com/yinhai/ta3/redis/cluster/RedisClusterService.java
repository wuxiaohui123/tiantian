package com.yinhai.ta3.redis.cluster;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

import com.yinhai.sysframework.util.ReflectUtil;
import com.yinhai.ta3.redis.CacheService;

public class RedisClusterService implements CacheService {

	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public void set(String key, Object value) {
		// TODO Auto-generated method stub
		redisTemplate.opsForValue().set(key, value);
	}

	@Override
	public Object get(String key) {
		// TODO Auto-generated method stub
		return redisTemplate.opsForValue().get(key);
	}

	@Override
	public void del(String key) {
		// TODO Auto-generated method stub
		redisTemplate.delete(key);
	}

	@Override
	public void expire(String key, int timeout) {
		// TODO Auto-generated method stub
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
	}

	@Override
	public boolean exists(String key) {
		// TODO Auto-generated method stub
		return redisTemplate.hasKey(key);
	}

	@Override
	public void hset(String key, String field, String value) {
		redisTemplate.opsForHash().put(key, field, value);
	}

	@Override
	public String hget(String key, String field) {
		// TODO Auto-generated method stub
		return (String) redisTemplate.opsForHash().get(key, field);
	}

	@Override
	public Map<String, Object> hash(String key) {
		// TODO Auto-generated method stub
		return ReflectUtil.convertMapKeyToString(redisTemplate.opsForHash().entries(key));
	}

	@Override
	public void hdel(String key, String... fields) {
		// TODO Auto-generated method stub
		redisTemplate.opsForHash().delete(key, fields);
	}

	public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

}
