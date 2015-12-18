package com.stnts.cache.service;

import com.stnts.cache.po.User;

public interface UserService {

	public User getWarraperUser(int id);
	
	public User getUser(int id);
	
	public void updateUser(User user);
	
	public void reloadUser();
	
	
}
