package com.yinhai.ta3.redis.service;

import java.util.Map;

import org.apache.log4j.Logger;

import com.yinhai.sysframework.util.ReflectUtil;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.redis.CacheService;

import redis.clients.jedis.ShardedJedis;

public class ShardedJedisClient implements CacheService {

	private static final Logger log = Logger.getLogger(ShardedJedisClient.class);

	private RedisDataSource redisDataSource;

	@Override
	public void set(String key, Object value) {
		ShardedJedis shardedJedis = redisDataSource.getRedisClient();
		if (shardedJedis != null) {
			boolean broken = false;
			try {
				shardedJedis.set(key, JSonFactory.bean2json(value));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				broken = true;
			} finally {
				redisDataSource.returnResource(shardedJedis, broken);
			}
		}

	}

	@Override
	public Object get(String key) {
		String result = null;
		ShardedJedis shardedJedis = redisDataSource.getRedisClient();
		if (shardedJedis == null) {
			return result;
		}

		boolean broken = false;
		try {
			result = shardedJedis.get(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			redisDataSource.returnResource(shardedJedis, broken);
		}
		return result;
	}

	@Override
	public void del(String key) {
		ShardedJedis shardedJedis = redisDataSource.getRedisClient();
		if (shardedJedis != null) {
			boolean broken = false;
			try {
				shardedJedis.del(key);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				broken = true;
			} finally {
				redisDataSource.returnResource(shardedJedis, broken);
			}
		}

	}

	@Override
	public void expire(String key, int paramInt) {
		ShardedJedis shardedJedis = redisDataSource.getRedisClient();
		if (shardedJedis != null) {
			boolean broken = false;
			try {
				shardedJedis.expire(key, paramInt);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				broken = true;
			} finally {
				redisDataSource.returnResource(shardedJedis, broken);
			}
		}

	}

	@Override
	public boolean exists(String key) {
		boolean result = false;
		ShardedJedis shardedJedis = redisDataSource.getRedisClient();
		if (shardedJedis != null) {
			boolean broken = false;
			try {
				result = shardedJedis.exists(key);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				broken = true;
			} finally {
				redisDataSource.returnResource(shardedJedis, broken);
			}
		}
		return result;
	}

	@Override
	public void hset(String key, String field, String value) {
		ShardedJedis shardedJedis = redisDataSource.getRedisClient();
		if (shardedJedis != null) {
			boolean broken = false;
			try {
				shardedJedis.hset(key, field, JSonFactory.bean2json(value));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				broken = true;
			} finally {
				redisDataSource.returnResource(shardedJedis, broken);
			}
		}

	}

	@Override
	public String hget(String key, String field) {
		String result = null;
		ShardedJedis shardedJedis = redisDataSource.getRedisClient();
		if (shardedJedis == null) {
			return result;
		}

		boolean broken = false;
		try {
			result = shardedJedis.hget(key, field);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			redisDataSource.returnResource(shardedJedis, broken);
		}
		return result;
	}

	@Override
	public Map<String, Object> hash(String key) {
		Map<String, Object> result = null;
		ShardedJedis shardedJedis = redisDataSource.getRedisClient();
		if (shardedJedis == null) {
			return result;
		}

		boolean broken = false;
		try {
			result = ReflectUtil.convertMapValueToObject(shardedJedis.hgetAll(key));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			redisDataSource.returnResource(shardedJedis, broken);
		}
		return result;
	}

	@Override
	public void hdel(String key, String... fields) {
		ShardedJedis shardedJedis = redisDataSource.getRedisClient();
		if (shardedJedis != null) {
			boolean broken = false;
			try {
				shardedJedis.hdel(key, fields);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				broken = true;
			} finally {
				redisDataSource.returnResource(shardedJedis, broken);
			}
		}
	}

	public RedisDataSource getRedisDataSource() {
		return redisDataSource;
	}

	public void setRedisDataSource(RedisDataSource redisDataSource) {
		this.redisDataSource = redisDataSource;
	}

}
