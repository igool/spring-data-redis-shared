package com.stnts.cache.service;

import org.springframework.cache.annotation.Cacheable;

import com.stnts.cache.po.Account;

public interface AccountService {

	public Account getAccountByName2(String userName);

	// 使用了一个缓存名叫 accountCache
	public Account getAccountByName(String userName);

	public Account getFromDB(String userName);
	

	// 清空 accountCache 缓存
	public void updateAccount(Account account);

	//@CacheEvict(value = "accountCache", allEntries = true)
	// 清空 accountCache 缓存
	public void reload();


	public void updateDB(Account account);
}