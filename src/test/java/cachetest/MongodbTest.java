package cachetest;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration("classpath:spring-cache-anno.xml")
public class MongodbTest extends AbstractJUnit4SpringContextTests{

	private static final Logger logger = Logger.getLogger(MongodbTest.class);
	@Autowired MongoTemplate mongoTemplate;
	
	@Test
	public void testMongodb(){
		logger.debug(mongoTemplate);
	}
}
