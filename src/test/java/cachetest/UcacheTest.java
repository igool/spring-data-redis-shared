package cachetest;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import test.stnts.cache.redis.ShardedRedisTemplateProxy;

import com.stnts.cache.po.People;

@ContextConfiguration("classpath:spring-cache-anno.xml")
public class UcacheTest extends AbstractJUnit4SpringContextTests {
	private static final Logger logger = Logger.getLogger(UcacheTest.class);
	@Autowired
	private People people;
	
	@Autowired
	private ShardedRedisTemplateProxy redisshared;

	@Test
	public void testGet()
	{
		logger.debug(people);
		
		redisshared.template("cai").opsForHash().put("cai", "name", "cxong");
		redisshared.template("我是来测试的").opsForHash().put("我是来测试的", "name", "你的值是？");
		assertEquals("cxong",  redisshared.template("cai").opsForHash().get("cai", "name"));
		logger.debug(redisshared);
		
		logger.debug(redisshared.template("我是来测试的").opsForHash().get("我是来测试的", "name"));
	}
}
