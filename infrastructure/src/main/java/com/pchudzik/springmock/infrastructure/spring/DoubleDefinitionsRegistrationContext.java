package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.MockConstants;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.util.HashSet;
import java.util.Set;

import static com.pchudzik.springmock.infrastructure.DoubleFactory.*;

/**
 * <p>Registers and stores information about {@link DoubleDefinition doubleDefinitions} registered as {@link
 * BeanDefinition}.</p>
 *
 * <p>It's required to track registered bean definitions to avoid for example creation spy of the spy</p>
 */
public class DoubleDefinitionsRegistrationContext {
	public static final String BEAN_NAME = MockConstants.PACKAGE_PREFIX + "doubleDefinitionsRegistrationContext";

	private final Set<DoubleDefinition> doublesRegisteredInContext = new HashSet<>();

	public void registerMock(BeanDefinitionRegistry registry, DoubleDefinition mockDefinition) {
		registerBeanDefinition(
				mockDefinition,
				registry,
				BeanDefinitionBuilder
						.rootBeanDefinition(mockDefinition.getDoubleClass())
						.setFactoryMethodOnBean(CREATE_MOCK_FACTORY_METHOD, DOUBLE_FACTORY_BEAN_NAME)
						.addConstructorArgValue(mockDefinition)
						.getBeanDefinition());
	}

	public void registerSpy(BeanDefinitionRegistry registry, DoubleDefinition spyDefinition) {
		final RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder
				.rootBeanDefinition(spyDefinition.getDoubleClass())
				.setFactoryMethodOnBean(CREATE_SPY_FACTORY_METHOD, DOUBLE_FACTORY_BEAN_NAME)
				.addConstructorArgValue(null)
				.addConstructorArgValue(spyDefinition)
				.getBeanDefinition();

		beanDefinition.setTargetType(spyDefinition.getDoubleClass());

		registerBeanDefinition(
				spyDefinition,
				registry,
				beanDefinition);
	}

	public boolean isBeanDefinitionRegisteredForDouble(DoubleDefinition definition) {
		return doublesRegisteredInContext.contains(definition);
	}

	private void registerBeanDefinition(DoubleDefinition doubleDefinition, BeanDefinitionRegistry beanDefinitionRegistry, BeanDefinition beanDefinition) {
		beanDefinitionRegistry.registerBeanDefinition(doubleDefinition.getName(), beanDefinition);
		doubleDefinition.getAliases().forEach(alias -> beanDefinitionRegistry.registerAlias(doubleDefinition.getName(), alias));

		doublesRegisteredInContext.add(doubleDefinition);
	}
}
