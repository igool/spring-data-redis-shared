package com.stnts.hander;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class CacheNamespaceHander extends NamespaceHandlerSupport {

	public void init()
	{
		// TODO Auto-generated method stub
		registerBeanDefinitionParser("cache", new CacheBeanDefined());
		registerBeanDefinitionParser("redisshared", new ShareRedisBeanDefined());
		registerBeanDefinitionParser("people", new PeopleBeanDefinitionParser());
	}

}
