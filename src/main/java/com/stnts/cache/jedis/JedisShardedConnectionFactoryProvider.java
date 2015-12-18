package com.stnts.cache.jedis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import com.stnts.cache.redis.RedisShardSpec;
import com.stnts.cache.redis.RedisShardSpecRegistry;
import com.stnts.cache.redis.ShardedConnectionFactoryProvider;

public class JedisShardedConnectionFactoryProvider extends ShardedConnectionFactoryProvider {


    public JedisShardedConnectionFactoryProvider( RedisShardSpecRegistry specProvider,
                                                  int shardsCount ) {
        super(specProvider, shardsCount);
    }

    @Override
    protected RedisConnectionFactory createConnectionFactory( RedisShardSpec shardSpec ) {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(shardSpec.getHost());
        factory.setPassword(shardSpec.getPassWord());
        factory.setPort(shardSpec.getPort());
        factory.setDatabase(shardSpec.getDb());
        factory.afterPropertiesSet();
        return factory;
    }

    @Override
    protected RedisConnectionFactory createResilientConnectionFactory( RedisShardSpec shardSpec ) {
        ResilientJedisConnectionFactory factory = new ResilientJedisConnectionFactory();
        factory.afterPropertiesSet();
        return factory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if ( isResilient() ) {
            //TODO: Check that Sentinel parameters has been provided as part of initialization
        }

        super.afterPropertiesSet();
    }
}
