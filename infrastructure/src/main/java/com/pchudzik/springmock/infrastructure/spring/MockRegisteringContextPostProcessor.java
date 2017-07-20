package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.MockConstants;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.definition.MockDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * Add {@link org.springframework.beans.factory.config.BeanDefinition BeanDefinitions} to the context for all the fields annotated
 * with {@link AutowiredMock @AutowiredMock}
 */
public class MockRegisteringContextPostProcessor implements BeanFactoryPostProcessor {
	public static final String BEAN_NAME = MockConstants.PACKAGE_PREFIX + "mockRegistrationContextPostProcessor";

	private final DoubleRegistry doubleRegistry;

	public MockRegisteringContextPostProcessor(DoubleRegistry doubleRegistry) {
		this.doubleRegistry = doubleRegistry;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
		final BeanDefinitionRegistry definitionRegistry = (BeanDefinitionRegistry) configurableListableBeanFactory;

		doubleRegistry
				.getMocks()
				.forEach(mockDefinition -> registerMock(definitionRegistry, mockDefinition));
	}

	private void registerMock(BeanDefinitionRegistry definitionRegistry, MockDefinition mockDefinition) {
		definitionRegistry.registerBeanDefinition(
				mockDefinition.getName(),
				BeanDefinitionBuilder
						.rootBeanDefinition(mockDefinition.getDoubleClass())
						.setFactoryMethodOnBean(DoubleFactory.CREATE_MOCK_FACTORY_METHOD, DoubleFactory.DOUBLE_FACTORY_BEAN_NAME)
						.addConstructorArgValue(mockDefinition)
						.getBeanDefinition());
		mockDefinition.getAliases().forEach(alias -> definitionRegistry.registerAlias(mockDefinition.getName(), alias));
	}
}
