package com.yinhai.ta3.redis.service.impl;

import org.apache.log4j.Logger;

import com.yinhai.ta3.redis.service.RedisDataSource;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
@SuppressWarnings("deprecation")
public class RedisDataSourceImpl implements RedisDataSource {

	private static final Logger log = Logger.getLogger(RedisDataSourceImpl.class);
	
	private ShardedJedisPool shardedJedisPool;
	
	@Override
	public ShardedJedis getRedisClient() {
		try {
            ShardedJedis shardJedis = shardedJedisPool.getResource();
            return shardJedis;
        } catch (Exception e) {
            log.error("getRedisClent error", e);
        }
        return null;
	}

	
	@Override
	public void returnResource(ShardedJedis shardedJedis) {
		shardedJedisPool.returnResource(shardedJedis);
	}

	@Override
	public void returnResource(ShardedJedis shardedJedis, boolean broken) {
		if (broken) {
            shardedJedisPool.returnBrokenResource(shardedJedis);
        } else {
            shardedJedisPool.returnResource(shardedJedis);
        }

	}

	public ShardedJedisPool getShardedJedisPool() {
		return shardedJedisPool;
	}

	public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
		this.shardedJedisPool = shardedJedisPool;
	}
	
	

}
