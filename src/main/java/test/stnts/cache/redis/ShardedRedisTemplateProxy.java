package test.stnts.cache.redis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

/**
 * Provides abstraction on top of RedisTemplate that supports transparent sharding of operations
 */
public class ShardedRedisTemplateProxy<Object, V>  {

    private RedisShardingStrategy<String>         shardingStrategy;
    private ShardedConnectionFactoryProvider connectionFactoryProvider;

    private Map<Integer, RedisTemplate<Object, V>> templates;

    private RedisSerializer<?>               keySerializer = new JdkSerializationRedisSerializer();

    private RedisSerializer<?> valueSerializer = new JdkSerializationRedisSerializer();
    
    public Integer getShardIdForKey( String key ) {
        return shardingStrategy.getShardIdByKey(ObjectUtils.toString(key));
    }

    public RedisTemplate<Object, V> template( Object key ) {
    	String rKey = StringUtils.EMPTY;
    	if ( key instanceof byte[]){
    		rKey = new String((byte[])key);
    	}else if ( key instanceof String){
    		rKey = ObjectUtils.toString(key);
    	}
        return getTemplateByShareid(getShardIdForKey(rKey));
    }

    public RedisTemplate<Object, V> getTemplateByShareid( int shardId ) {
        Assert.notNull(templates, "ensure that method afterPropertiesSet was called before this " +
                "one");

        return templates.get(shardId);
    }

    public void initTemplateData() throws Exception {
        Assert.notNull(shardingStrategy, "Sharding strategy should be set prior to usage of the " +
                "instance");
        Assert.notNull(connectionFactoryProvider, "Connection factory provider should be set " +
                "prior to usage of the instance");

        templates = new ConcurrentHashMap<>(shardingStrategy.getShardsCount());

        for ( int i = 0; i < shardingStrategy.getShardsCount(); i++ ) {
            RedisTemplate<Object, V> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactoryProvider.getConnectionFactory(i));
            // Additional parameters should be added as pass-through
            if ( keySerializer != null ) {
                template.setKeySerializer(keySerializer);
            }
            if ( valueSerializer != null ) {
                template.setValueSerializer(valueSerializer);
            }
            template.afterPropertiesSet();
            templates.put(i, template);
        }

    }

    public RedisShardingStrategy<String> getShardingStrategy() {
        return shardingStrategy;
    }

    public void setShardingStrategy( RedisShardingStrategy<String> shardingStrategy ) {
        this.shardingStrategy = shardingStrategy;
    }

    public ShardedConnectionFactoryProvider getConnectionFactoryProvider()
	{
		return connectionFactoryProvider;
	}

	public void setConnectionFactoryProvider(ShardedConnectionFactoryProvider connectionFactoryProvider)
	{
		this.connectionFactoryProvider = connectionFactoryProvider;
	}
	
	public RedisSerializer<?> getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer( RedisSerializer<?> keySerializer ) {
        this.keySerializer = keySerializer;
    }

    public RedisSerializer<?> getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer( RedisSerializer<?> valueSerializer ) {
        this.valueSerializer = valueSerializer;
    }
    
    public Map<Integer, RedisTemplate<Object, V>>  getTemplates(){
    	return templates;
    }

}
