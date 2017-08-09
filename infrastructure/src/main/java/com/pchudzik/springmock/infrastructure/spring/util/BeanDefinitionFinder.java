package com.pchudzik.springmock.infrastructure.spring.util;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

public class BeanDefinitionFinder {
	private final ConfigurableListableBeanFactory definitionRegistry;

	public BeanDefinitionFinder(ConfigurableListableBeanFactory definitionRegistry) {
		this.definitionRegistry = definitionRegistry;
	}

	public BeanDefinitionFinder(ApplicationContext applicationContext) {
		this((ConfigurableListableBeanFactory) applicationContext.getAutowireCapableBeanFactory());
	}

	public Optional<BeanDefinition> tryToFindBeanDefinition(String beanName) {
		try {
			return Optional.of(definitionRegistry.getBeanDefinition(beanName));
		} catch (NoSuchBeanDefinitionException ex) {
			return Optional.empty();
		}
	}

	public Optional<BeanDefinition> tryToFindBeanDefinition(String beanName, Class<?> doubleClass) {
		final Optional<BeanDefinition> definition = tryToFindBeanDefinition(beanName);
		return definition.isPresent()
				? definition
				: tryToFindSingleBeanDefinition(doubleClass);
	}

	public Optional<BeanDefinition> tryToFindSingleBeanDefinition(Class<?> doubleClass) {
		final String[] beanNamesForType = definitionRegistry.getBeanNamesForType(doubleClass);
		if (beanNamesForType.length == 1) {
			return Optional.of(definitionRegistry.getBeanDefinition(beanNamesForType[0]));
		}
		return Optional.empty();
	}
}
