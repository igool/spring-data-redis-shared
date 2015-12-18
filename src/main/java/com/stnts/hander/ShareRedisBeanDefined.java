package com.stnts.hander;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.util.ReflectionUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import test.stnts.cache.jedis.JedisShardedConnectionFactoryProvider;

import test.stnts.cache.redis.DefaultRedisShardSpecRegistry;
import test.stnts.cache.redis.RedisShardSpec;
import test.stnts.cache.redis.RedisShardingStrategy;
import test.stnts.cache.redis.ShardedRedisTemplateProxy;

public class ShareRedisBeanDefined extends AbstractUcacheBeanDefinitionParser {

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext)
	{
		// TODO Auto-generated method stub
		final SpringXmlBuilder springXmlBuilder = new SpringXmlBuilder(parserContext);
		springXmlBuilder.handle(element);
		return springXmlBuilder.getBeanDefinition();
	}

	private class SpringXmlBuilder extends SpringXmlBuilderHelper {

		private final ParserContext parserContext;

		private BeanDefinitionBuilder builder;
		
		private BeanDefinitionBuilder providerBuilder;
		
		private ManagedMap mapSpecManagedMap  = new ManagedMap();
		
		RedisShardingStrategy sharedStrategy;
		
		BeanDefinitionBuilder shareConnectionBuilder = createBeanBuilder(JedisShardedConnectionFactoryProvider.class) ;

		public SpringXmlBuilder(ParserContext parserContext)
		{
			this.parserContext = parserContext;
			this.builder = BeanDefinitionBuilder.rootBeanDefinition(ShardedRedisTemplateProxy.class);
		}

		public AbstractBeanDefinition getBeanDefinition()
		{
			return builder.getBeanDefinition();
		}

		public void handle(Element element)
		{
			handleCommonBeanAttributes(element, builder, parserContext);
			for (Node node : new IterableNodeList(element, Node.ELEMENT_NODE))
			{
				final String nodeName = cleanNodeName(node.getNodeName());
				if ("sharding-strategy".equals(nodeName))
				{
					handleShareStrategy(node.getAttributes().getNamedItem("shared-strategy-impl"));
				}else if("connection-factory-provider".equals(nodeName)){
					handleConnectionFactoryProvider(node);
					shareConnectionBuilder.addConstructorArgValue(providerBuilder.getBeanDefinition());
					shareConnectionBuilder.setInitMethodName("initSharedConnection");
					this.builder.addPropertyValue("connectionFactoryProvider", shareConnectionBuilder.getBeanDefinition());
					this.builder.setInitMethodName("initTemplateData");
				}
			}

		}
		
		private void handleShareStrategy(Node node){
			String value = node.getNodeValue();	
			RedisShardingStrategy sharedStrategy = ReflectionUtils.createInstanceIfPresent(value, null);
			System.out.println(sharedStrategy);
			this.builder.addPropertyValue("shardingStrategy", sharedStrategy);

		}
		
		private void handleConnectionFactoryProvider(Node node){
			int specIndex = 0;
			for (Node _node : new IterableNodeList(node, Node.ELEMENT_NODE))
			{
				final String nodeName = cleanNodeName(_node.getNodeName());
				if("spec-provider".equals(nodeName)){
					handleConnectionFactoryProvider(_node);
					providerBuilder = createBeanBuilder(DefaultRedisShardSpecRegistry.class);
					providerBuilder.addPropertyValue("specs", mapSpecManagedMap);
				}else if("shared-spec".equals(nodeName)){
					//handleConnectionFactoryProvider(_node);
					
					BeanDefinitionBuilder shareSpecBuilder = createBeanBuilder(RedisShardSpec.class);
					
					shareSpecBuilder.addConstructorArgValue(_node.getAttributes().getNamedItem("host").getNodeValue());
					shareSpecBuilder.addConstructorArgValue(_node.getAttributes().getNamedItem("password").getNodeValue());
					shareSpecBuilder.addConstructorArgValue(_node.getAttributes().getNamedItem("port").getNodeValue());
					shareSpecBuilder.addConstructorArgValue(_node.getAttributes().getNamedItem("db").getNodeValue());
					mapSpecManagedMap.put(specIndex, shareSpecBuilder.getBeanDefinition());
					specIndex ++;
				}
			}
		}
	}
}
