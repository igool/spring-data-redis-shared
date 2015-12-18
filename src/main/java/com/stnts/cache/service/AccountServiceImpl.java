package com.stnts.cache.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.stnts.cache.po.Account;
@Cacheable("accountCache")
@Service("accountService")
public class AccountServiceImpl implements AccountService {

	private static final Logger logger = Logger.getLogger(AccountService.class);

	Map<String, Account> accountData = new HashMap<String, Account>();

	public Account getAccountByName2(String userName)
	{
		return this.getAccountByName(userName);
	}

	@Cacheable( key = "#userName")
	// 使用了一个缓存名叫 accountCache
	public Account getAccountByName(String userName)
	{
		// 方法内部实现不考虑缓存逻辑，直接实现业务
		logger.debug("real query account." + userName);
		return getFromDB(userName);
	}

	public Account getFromDB(String userName)
	{
		logger.debug("real querying db..." + userName);
		Account account = accountData.get(userName);
		if ( account == null ){
			account = new Account();
			account.setId(0);
			account.setName(userName);
			accountData.put(userName, account);
		}
		return account;
	}

	@CacheEvict( key = "#account.getName()", beforeInvocation = true)
	// 清空 accountCache 缓存
	public void updateAccount(Account account)
	{
		updateDB(account);
	}

	@CacheEvict(allEntries = true)
	// 清空 accountCache 缓存
	public void reload()
	{
	}

	public void updateDB(Account account)
	{
		
		logger.debug("real update db..." + account);
		account = accountData.get(account.getName());
		if ( account != null ){
			accountData.put(account.getName(), account);
		}
	}
}
