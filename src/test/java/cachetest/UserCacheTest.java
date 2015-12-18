package cachetest;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import com.stnts.cache.service.UserService;

@ContextConfiguration("classpath:spring-cache-anno.xml")
public class UserCacheTest  extends AbstractJUnit4SpringContextTests{

	private static final Logger logger = Logger.getLogger(UserCacheTest.class);
	@Autowired
	private UserService  userService;

	@Autowired
	private HazelcastCacheManager hcCacheManager;
	
	@Autowired
	private HazelcastInstance instanceCache;
	@Test
	public void testCache(){
		logger.debug("---reload cache --");
	     userService.reloadUser();
		 // 第一次查询，应该走数据库
		logger.debug("no first query..."); 
		logger.debug( userService.getWarraperUser(2)); 
	     // 第二次查询，应该不查数据库，直接返回缓存的值
	     logger.debug("no second query..."); 
	     logger.debug( userService.getWarraperUser(1)); 
	     logger.debug("-----");
	     logger.debug("---read again --");
	     logger.debug( userService.getWarraperUser(2));
	     logger.debug("---read new 3--");
	     logger.debug( userService.getWarraperUser(3));
	     logger.debug("---read new 3 again--");
         logger.debug( userService.getWarraperUser(3));
	}
}
