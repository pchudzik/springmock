package com.pchudzik.springmock.infrastructure.spring.util;

import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

public class BeanDefinitionFinder {
	private final ConfigurableListableBeanFactory definitionRegistry;
	private final DoubleRegistry doubleRegistry;

	public BeanDefinitionFinder(ConfigurableListableBeanFactory definitionRegistry, DoubleRegistry doubleRegistry) {
		this.definitionRegistry = definitionRegistry;
		this.doubleRegistry = doubleRegistry;
	}

	public BeanDefinitionFinder(ApplicationContext applicationContext, DoubleRegistry doubleRegistry) {
		this(
				(ConfigurableListableBeanFactory) applicationContext.getAutowireCapableBeanFactory(),
				doubleRegistry);
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
			final String beanName = beanNamesForType[0];

			if(doubleRegistryContainsDouble(beanName, doubleClass)) {
				return Optional.empty();
			}

			return Optional.of(definitionRegistry.getBeanDefinition(beanName));
		}

		return Optional.empty();
	}

	private boolean doubleRegistryContainsDouble(String doubleName, Class<?> doubleClass) {
		return doubleRegistry.doublesSearch().containsExactlyOneDouble(doubleName, doubleClass);
	}
}
