package com.stnts.hander;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class AbstractUcacheBeanDefinitionParser extends AbstractBeanDefinitionParser {

	 /**
     * Base Helper class for Spring Xml Builder
     */
    public abstract class SpringXmlBuilderHelper extends AbstractXmlConfigHelper {

        protected BeanDefinitionBuilder configBuilder;

        protected void handleCommonBeanAttributes(Node node, BeanDefinitionBuilder builder, ParserContext parserContext) {
            final NamedNodeMap attributes = node.getAttributes();
            if (attributes != null) {
                Node lazyInitAttr = attributes.getNamedItem("lazy-init");
                if (lazyInitAttr != null) {
                    builder.setLazyInit(Boolean.valueOf(getTextContent(lazyInitAttr)));
                } else {
                    builder.setLazyInit(parserContext.isDefaultLazyInit());
                }

                if (parserContext.isNested()) {
                    builder.setScope(parserContext.getContainingBeanDefinition().getScope());
                } else {
                    Node scopeNode = attributes.getNamedItem("scope");
                    if (scopeNode != null) {
                        builder.setScope(getTextContent(scopeNode));
                    }
                }

                Node dependsOnNode = attributes.getNamedItem("depends-on");
                if (dependsOnNode != null) {
                    String[] dependsOn = getTextContent(dependsOnNode).split("[,;]");
                    for (String dep : dependsOn) {
                        builder.addDependsOn(dep.trim());
                    }
                }
            }
        }

        protected BeanDefinitionBuilder createBeanBuilder(final Class clazz) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
//            builder.setScope(configBuilder.getBeanDefinition().getScope());
//            builder.setLazyInit(configBuilder.getBeanDefinition().isLazyInit());
            return builder;
        }

        protected BeanDefinitionBuilder createAndFillBeanBuilder(Node node, final Class clazz,
                                                                 final String propertyName,
                                                                 final BeanDefinitionBuilder parent,
                                                                 final String... exceptPropertyNames) {
            BeanDefinitionBuilder builder = createBeanBuilder(clazz);
            final AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
            fillValues(node, builder, exceptPropertyNames);
            parent.addPropertyValue(propertyName, beanDefinition);
            return builder;
        }

        protected void createAndFillListedBean(Node node,
                                               final Class clazz,
                                               final String propertyName,
                                               final ManagedMap managedMap,
                                               String... excludeNames) {
            BeanDefinitionBuilder builder = createBeanBuilder(clazz);
            final AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
            //"name"
            final Node attName = node.getAttributes().getNamedItem(propertyName);
            final String name = getTextContent(attName);
            builder.addPropertyValue("name", name);
            fillValues(node, builder, excludeNames);
            managedMap.put(name, beanDefinition);
        }

        protected void fillValues(Node node, BeanDefinitionBuilder builder, String... excludeNames) {
            Collection<String> epn = excludeNames != null && excludeNames.length > 0
                    ? new HashSet<String>(Arrays.asList(excludeNames)) : null;
            fillAttributeValues(node, builder, epn);
            for (Node n : new IterableNodeList(node, Node.ELEMENT_NODE)) {
                String name = xmlToJavaName(cleanNodeName(n));
                if (epn != null && epn.contains(name)) {
                    continue;
                }
                String value = getTextContent(n);
                builder.addPropertyValue(name, value);
            }
        }

        protected void fillAttributeValues(Node node, BeanDefinitionBuilder builder, String... excludeNames) {
            Collection<String> epn = excludeNames != null && excludeNames.length > 0
                    ? new HashSet<String>(Arrays.asList(excludeNames)) : null;
            fillAttributeValues(node, builder, epn);
        }

        protected void fillAttributeValues(Node node, BeanDefinitionBuilder builder, Collection<String> epn) {
            final NamedNodeMap atts = node.getAttributes();
            if (atts != null) {
                for (int a = 0; a < atts.getLength(); a++) {
                    final Node att = atts.item(a);
                    final String name = xmlToJavaName(att.getNodeName());
                    if (epn != null && epn.contains(name)) {
                        continue;
                    }
                    final String value = att.getNodeValue();
                    builder.addPropertyValue(name, value);
                }
            }
        }

        protected ManagedList parseListeners(Node node, Class listenerConfigClass) {
            ManagedList listeners = new ManagedList();
            final String implementationAttr = "implementation";
            for (Node listenerNode : new IterableNodeList(node.getChildNodes(), Node.ELEMENT_NODE)) {
                final BeanDefinitionBuilder listenerConfBuilder = createBeanBuilder(listenerConfigClass);
                fillAttributeValues(listenerNode, listenerConfBuilder, implementationAttr);
                Node implementationNode = listenerNode.getAttributes().getNamedItem(implementationAttr);
                if (implementationNode != null) {
                    listenerConfBuilder.addPropertyReference(implementationAttr, getTextContent(implementationNode));
                }
                listeners.add(listenerConfBuilder.getBeanDefinition());
            }
            return listeners;
        }

        protected ManagedList parseProxyFactories(Node node, Class proxyFactoryConfigClass) {
            ManagedList list = new ManagedList();
            for (Node instanceNode : new IterableNodeList(node.getChildNodes(), Node.ELEMENT_NODE)) {
                final BeanDefinitionBuilder confBuilder = createBeanBuilder(proxyFactoryConfigClass);
                fillAttributeValues(instanceNode, confBuilder);
                list.add(confBuilder.getBeanDefinition());
            }
            return list;
        }


        protected void handleDataSerializableFactories(final Node node, final BeanDefinitionBuilder serializationConfigBuilder) {
            ManagedMap factories = new ManagedMap();
            ManagedMap<Integer, String> classNames = new ManagedMap<Integer, String>();
            for (Node child : new IterableNodeList(node, Node.ELEMENT_NODE)) {
                final String name = cleanNodeName(child);
                if ("data-serializable-factory".equals(name)) {
                    final NamedNodeMap attrs = child.getAttributes();
                    final Node implRef = attrs.getNamedItem("implementation");
                    final Node classNode = attrs.getNamedItem("class-name");
                    final Node fidNode = attrs.getNamedItem("factory-id");
                    if (implRef != null) {
                        factories.put(Integer.parseInt(getTextContent(fidNode))
                                , new RuntimeBeanReference(getTextContent(implRef)));
                    }
                    if (classNode != null) {
                        classNames.put(Integer.parseInt(getTextContent(fidNode)), getTextContent(classNode));
                    }
                }
            }
            serializationConfigBuilder.addPropertyValue("dataSerializableFactoryClasses", classNames);
            serializationConfigBuilder.addPropertyValue("dataSerializableFactories", factories);
        }

       

       
    }

}
