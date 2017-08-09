package com.pchudzik.springmock.infrastructure.spring.util;

import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import static com.pchudzik.springmock.infrastructure.DoubleFactory.*;

public class BeanDefinitionRegistrationHelper {
	private final BeanDefinitionRegistry beanDefinitionRegistry;

	public BeanDefinitionRegistrationHelper(BeanDefinitionRegistry beanDefinitionRegistry) {
		this.beanDefinitionRegistry = beanDefinitionRegistry;
	}

	public void registerMock(DoubleDefinition mockDefinition) {
		registerBeanDefinition(
				mockDefinition,
				BeanDefinitionBuilder
						.rootBeanDefinition(mockDefinition.getDoubleClass())
						.setFactoryMethodOnBean(CREATE_MOCK_FACTORY_METHOD, DOUBLE_FACTORY_BEAN_NAME)
						.addConstructorArgValue(mockDefinition)
						.getBeanDefinition());
	}

	public void registerSpy(DoubleDefinition spyDefinition) {
		registerBeanDefinition(
				spyDefinition,
				BeanDefinitionBuilder
						.rootBeanDefinition(spyDefinition.getDoubleClass())
						.setFactoryMethodOnBean(CREATE_SPY_FACTORY_METHOD, DOUBLE_FACTORY_BEAN_NAME)
						.addConstructorArgValue(null)
						.addConstructorArgValue(spyDefinition)
						.getBeanDefinition());
	}

	private void registerBeanDefinition(DoubleDefinition doubleDefinition, BeanDefinition beanDefinition) {
		beanDefinitionRegistry.registerBeanDefinition(doubleDefinition.getName(), beanDefinition);
		doubleDefinition.getAliases().forEach(alias -> beanDefinitionRegistry.registerAlias(doubleDefinition.getName(), alias));
	}
}
