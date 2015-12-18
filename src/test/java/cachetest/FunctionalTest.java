package cachetest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.stnts.cache.jedis.JedisShardedConnectionFactoryProvider;
import com.stnts.cache.redis.DefaultRedisShardSpecRegistry;
import com.stnts.cache.redis.RedisShardSpec;
import com.stnts.cache.redis.RedisShardingStrategy;
import com.stnts.cache.redis.ShardedRedisTemplateProxy;

public class FunctionalTest {

    @Test
    public void showcaseBasicUsage() throws Exception {
        final int NUM_SHARDS = 4;
        RedisShardingStrategy<String> modStrategy = new RedisShardingStrategy<String>() {
            @Override
            public int getShardsCount() {
                return NUM_SHARDS;
            }

            @Override
            public int getShardIdByKey( String key ) {
                return key.hashCode() % NUM_SHARDS;
            }
        };

        DefaultRedisShardSpecRegistry specProvider = new DefaultRedisShardSpecRegistry();
        specProvider.addSpecForShard(0, RedisShardSpec.fromHostAndPort("192.168.4.57","eyoo_abc_kkmb-abc", 6379, 0));
        specProvider.addSpecForShard(1, RedisShardSpec.fromHostAndPort("192.168.2.97", "eyoo_abc_kkmb-abc", 6379, 1));
        specProvider.addSpecForShard(2, RedisShardSpec.fromHostAndPort("192.168.4.57", "eyoo_abc_kkmb-abc", 6379, 2));
        specProvider.addSpecForShard(3, RedisShardSpec.fromHostAndPort("192.168.2.97", "eyoo_abc_kkmb-abc",6379, 3));

        JedisShardedConnectionFactoryProvider connectionFactoryProvider = new
                JedisShardedConnectionFactoryProvider(specProvider, NUM_SHARDS);

        // Spring container would call this automatically after properties are set
        connectionFactoryProvider.afterPropertiesSet();

        RedisSerializer<String> stringSerializer = new StringRedisSerializer();

        ShardedRedisTemplateProxy<String, String> proxy = new ShardedRedisTemplateProxy<>();
        proxy.setShardingStrategy(modStrategy);
        proxy.setConnectionFactoryProvider(connectionFactoryProvider);
       // proxy.setKeySerializer(stringSerializer);
       // proxy.setValueSerializer(stringSerializer);

        // Spring container would call this automatically after properties are set
        proxy.afterPropertiesSet();

        // Could also get template by key. Index here for cases when we know shard upfront
        proxy.template(0).opsForValue().set("test", "0");
        proxy.template(1).opsForValue().set("test", "1");
        
        proxy.template("cai").opsForHash().put("cai", "name", "cxong");

        assertEquals("0", proxy.template(0).opsForValue().get("test"));
        assertEquals("1", proxy.template(1).opsForValue().get("test"));
        
        assertEquals("cxong",  proxy.template("cai").opsForHash().get("cai", "name"));
    }

}
