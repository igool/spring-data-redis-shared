package com.stnts.cache.redis;

/**
 * Value type that contains specification of shard instance
 */
public final class RedisShardSpec {


    private String masterName;
    private String host;
    private String passWord;
    private int    port;
    private int    db;

    private RedisShardSpec( String masterName, int db ) {
        this.masterName = masterName;
        this.db = db;
    }

    private RedisShardSpec( String host, String passWord, int port, int db ) {
        this.host = host;
        this.passWord = passWord;
        this.port = port;
        this.db = db;
    }

    public static RedisShardSpec fromHostAndPort( String host, String passWord, int port, int db ) {
        return new RedisShardSpec(host, passWord, port, db);
    }

    public static RedisShardSpec fromMasterName( String masterName, int db ) {
        return new RedisShardSpec(masterName, db);
    }

    public String getMasterName() {
        return masterName;
    }

    public String getHost() {
        return host;
    }

    public String getPassWord()
	{
		return passWord;
	}

	public void setPassWord(String passWord)
	{
		this.passWord = passWord;
	}

	public int getPort() {
        return port;
    }

    public int getDb() {
        return db;
    }

	public void setPort(int port)
	{
		this.port = port;
	}

	public void setDb(int db)
	{
		this.db = db;
	}

}
