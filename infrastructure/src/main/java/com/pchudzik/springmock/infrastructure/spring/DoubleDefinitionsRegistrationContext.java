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
 * <p>
 * <p>It's required to track registered bean definitions to avoid for example creation spy of the spy</p>
 */
public class DoubleDefinitionsRegistrationContext {
	public static final String BEAN_NAME = MockConstants.PACKAGE_PREFIX + "doubleDefinitionsRegistrationContext";

	private static final Object NULL_OBJECT_TO_SPY_ON = null;

	private final Set<DoubleDefinition> doublesRegisteredInContext = new HashSet<>();

	public void registerMock(BeanDefinitionRegistry registry, DoubleDefinition mockDefinition) {
		registerBeanDefinition(
				mockDefinition,
				registry,
				buildBeanDefinition(CREATE_MOCK_FACTORY_METHOD, mockDefinition, mockDefinition));
	}

	public void registerSpy(BeanDefinitionRegistry registry, DoubleDefinition spyDefinition) {
		registerBeanDefinition(
				spyDefinition,
				registry,
				buildBeanDefinition(CREATE_SPY_FACTORY_METHOD, spyDefinition, NULL_OBJECT_TO_SPY_ON, spyDefinition));
	}

	public boolean isBeanDefinitionRegisteredForDouble(DoubleDefinition definition) {
		return doublesRegisteredInContext.contains(definition);
	}

	private void registerBeanDefinition(DoubleDefinition doubleDefinition, BeanDefinitionRegistry beanDefinitionRegistry, BeanDefinition beanDefinition) {
		beanDefinitionRegistry.registerBeanDefinition(doubleDefinition.getName(), beanDefinition);
		doubleDefinition.getAliases().forEach(alias -> beanDefinitionRegistry.registerAlias(doubleDefinition.getName(), alias));

		doublesRegisteredInContext.add(doubleDefinition);
	}

	private BeanDefinition buildBeanDefinition(String factoryMethod, DoubleDefinition doubleDefinition, Object... constructorArgs) {
		final BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder
				.rootBeanDefinition(doubleDefinition.getDoubleClass())
				.setFactoryMethodOnBean(factoryMethod, DOUBLE_FACTORY_BEAN_NAME);

		for (Object constructorArg : constructorArgs) {
			definitionBuilder.addConstructorArgValue(constructorArg);
		}

		final RootBeanDefinition beanDefinition = (RootBeanDefinition) definitionBuilder.getBeanDefinition();
		beanDefinition.setTargetType(doubleDefinition.getDoubleClass());

		return beanDefinition;
	}
}
