package com.stnts.cache.config;

import org.springframework.cache.support.CompositeCacheManager;

public class Ucache {

	private CompositeCacheManager  compositeCacheManager;

	public CompositeCacheManager getCompositeCacheManager()
	{
		return compositeCacheManager;
	}

	public void setCompositeCacheManager(CompositeCacheManager compositeCacheManager)
	{
		this.compositeCacheManager = compositeCacheManager;
	}
	
}
