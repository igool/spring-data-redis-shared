package test.stnts.cache.redis;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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
	
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
