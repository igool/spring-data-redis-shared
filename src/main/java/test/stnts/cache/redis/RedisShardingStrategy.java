package test.stnts.cache.redis;

/**
 * Calculates shard based on the key
 */
public interface RedisShardingStrategy<String> {

    public int getShardsCount();

    public int getShardIdByKey(String key);

}
