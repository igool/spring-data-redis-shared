package test.stnts.cache.jedis;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConnection;

/**
 * Implementation of Jedis factory based on Sentinel Pool
 */
public class ResilientJedisConnectionFactory implements 
        RedisConnectionFactory {


    @Override
    public RedisConnection getConnection() {
        return null;
    }

    @Override
    public boolean getConvertPipelineAndTxResults() {
        return false;
    }

    @Override
    public DataAccessException translateExceptionIfPossible( RuntimeException e ) {
        return null;
    }

	@Override
	public RedisSentinelConnection getSentinelConnection()
	{
		// TODO Auto-generated method stub
		return null;
	}


}
