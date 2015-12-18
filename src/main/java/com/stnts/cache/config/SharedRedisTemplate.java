package com.stnts.cache.config;

import com.stnts.cache.redis.DefaultRedisShardSpecRegistry;
import com.stnts.cache.redis.RedisShardingStrategy;

public class SharedRedisTemplate<K, V> {

	private DefaultRedisShardSpecRegistry redisSharedSpectRegistry;
	private RedisShardingStrategy<K>         shardingStrategy;

	public DefaultRedisShardSpecRegistry getRedisSharedSpectRegistry()
	{
		return redisSharedSpectRegistry;
	}

	public void setRedisSharedSpectRegistry(DefaultRedisShardSpecRegistry redisSharedSpectRegistry)
	{
		this.redisSharedSpectRegistry = redisSharedSpectRegistry;
	}

	public RedisShardingStrategy<K> getShardingStrategy()
	{
		return shardingStrategy;
	}

	public void setShardingStrategy(RedisShardingStrategy<K> shardingStrategy)
	{
		this.shardingStrategy = shardingStrategy;
	}
}
