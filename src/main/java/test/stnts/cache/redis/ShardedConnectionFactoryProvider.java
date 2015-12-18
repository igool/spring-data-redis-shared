package test.stnts.cache.redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.Assert;

// TODO: Refactor to use two different providers (and pass respective parameters)
public abstract class ShardedConnectionFactoryProvider  {

    private RedisShardSpecRegistry specProvider;

    private boolean resilient = false;

    private RedisConnectionFactory[] factories;


    // TODO: Use size of registry
    public ShardedConnectionFactoryProvider( RedisShardSpecRegistry specProvider ) {
        this.specProvider = specProvider;
    }

    public boolean isResilient() {
        return resilient;
    }

    public void setResilient( boolean resilient ) {
        this.resilient = resilient;
    }

    public RedisConnectionFactory getConnectionFactory( int shardId ) {
        Assert.isTrue(shardId >= 0 && shardId < specProvider.getSharedSize(),
                "shardId should be between 0 and " + specProvider.getSharedSize());
        Assert.notNull(factories, "ensure that afterPropertiesSet method was called prior to " +
                "using this method");

        return factories[shardId];
    }

    public void initSharedConnection() throws Exception {
        Assert.notNull(specProvider, "specProvider should be set prior to bean usage");
        Assert.isTrue(specProvider.getSharedSize() > 0, "shards count should be at least 1");

        factories = new RedisConnectionFactory[specProvider.getSharedSize()];

        for ( int i = 0; i < specProvider.getSharedSize(); i++ ) {
            RedisShardSpec shardSpec = specProvider.getShardSpecById(i);
            factories[i] = resilient ? createResilientConnectionFactory(shardSpec) :
                    createConnectionFactory(shardSpec);
        }
    }

    protected abstract RedisConnectionFactory createConnectionFactory( RedisShardSpec shardSpec );

    protected abstract RedisConnectionFactory createResilientConnectionFactory(
            RedisShardSpec shardSpec );

}
