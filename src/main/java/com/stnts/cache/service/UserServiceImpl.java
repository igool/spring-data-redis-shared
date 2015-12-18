package com.stnts.cache.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.stnts.cache.po.User;

@Cacheable("mapCache1")
@Service("userService")
public class UserServiceImpl implements UserService {

	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

	Map<Integer, User> userData = new HashMap<Integer, User>();

	public User getWarraperUser(int id)
	{
		// TODO Auto-generated method stub
		return getUser(id);
	}

	@Cacheable(key = "#id"/* , cacheManager="hcCacheManager" */)
	public User getUser(int id)
	{
		// TODO Auto-generated method stub
		logger.debug("real querying db... id " + id);
		User account = userData.get(id);
		if (account == null)
		{
			account = new User();
			account.setId(id);
			account.setName("i'm"+id+" null");
			userData.put(id, account);
		}
		return account;
	}

	@CacheEvict(key = "#user.getId()", beforeInvocation = true)
	public void updateUser(User user)
	{
		// TODO Auto-generated method stub
		User account = userData.get(user.getId());
		if (account != null)
		{
			userData.put(user.getId(), user);
		}
	}

	@CacheEvict(allEntries = true, value = "mapCache1")
	public void reloadUser()
	{
		// TODO Auto-generated method stub

	}

}
