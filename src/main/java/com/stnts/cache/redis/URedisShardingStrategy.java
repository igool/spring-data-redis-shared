package com.stnts.cache.redis;

public class URedisShardingStrategy implements RedisShardingStrategy<String> {

	private int NUM_SHARDS = 4; 
	@Override
    public int getShardsCount() {
        return NUM_SHARDS;
    }


	@Override
	public int getShardIdByKey(String key)
	{
		// TODO Auto-generated method stub
		 return key.hashCode() % NUM_SHARDS;
	}

}
