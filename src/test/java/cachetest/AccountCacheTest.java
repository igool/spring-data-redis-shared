package cachetest;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.stnts.cache.po.Account;
import com.stnts.cache.service.AccountService;
@ContextConfiguration("classpath:spring-cache-anno.xml")
public class AccountCacheTest  extends AbstractJUnit4SpringContextTests{

	private static final Logger logger = Logger.getLogger(AccountCacheTest.class);
	@Autowired
	private AccountService  accountService;
	
	//@Test
	public void testCache(){
		 // 第一次查询，应该走数据库
		logger.debug("no first query..."); 
		logger.debug( accountService.getAccountByName("somebody")); 
	     // 第二次查询，应该不查数据库，直接返回缓存的值
	     logger.debug("no second query..."); 
	     logger.debug( accountService.getAccountByName("somebody")); 
	     logger.debug("-----");; 
	}
	
	@Test
	public void testCacheDb(){
		 // 第一次查询，应该走数据库
		logger.debug("db first query..."); 
		logger.debug(accountService.getAccountByName2("somebody")); 
	     // 第二次查询，应该不查数据库，直接返回缓存的值
	     logger.debug("db second query..."); 
	     logger.debug(accountService.getAccountByName2("somebody")); 
	     logger.debug("-----");; 
	}
	
//	@Test
	public void testClear(){
		logger.debug("start testing clear cache...");    // 更新某个记录的缓存，首先构造两个账号记录，然后记录到缓存中
	     Account account1 = accountService.getAccountByName("somebody1"); 
	     Account account2 = accountService.getAccountByName("somebody2"); 
	     logger.debug("account 1 "+account1);
	     // 开始更新其中一个    
	     account1.setId(1212);
	     accountService.updateAccount(account1); 
	     logger.debug("检查否更新了值:"+accountService.getAccountByName("somebody1"));// 因为被更新了，所以会查询数据库    
	     logger.debug("缓存读取somebody2:"+accountService.getAccountByName("somebody2"));// 没有更新过，应该走缓存    
	     logger.debug("缓存读取somebody1:"+accountService.getAccountByName("somebody1"));// 再次查询，应该走缓存    // 更新所有缓存
	    
	     accountService.reload(); 
	     logger.debug("重新加载somebody1: "+accountService.getAccountByName("somebody1"));// 应该会查询数据库   
	     logger.debug("重新加载somebody2"+accountService.getAccountByName("somebody2"));// 应该会查询数据库   
	     logger.debug("缓存somebody1:"+accountService.getAccountByName("somebody1"));// 应该走缓存    
	     logger.debug("缓存somebody2:"+accountService.getAccountByName("somebody2"));// 应该走缓存*/
	}
}
