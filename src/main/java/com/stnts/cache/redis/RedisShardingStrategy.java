package com.stnts.cache.redis;

/**
 * Calculates shard based on the key
 */
public interface RedisShardingStrategy<K> {

    public int getShardsCount();

    public int getShardIdByKey(K key);

}
