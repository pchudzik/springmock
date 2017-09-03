package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.MockConstants;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * Add {@link org.springframework.beans.factory.config.BeanDefinition BeanDefinitions} to the context for all the fields annotated
 * with {@link AutowiredMock @AutowiredMock}
 */
public class MockDefinitionsRegisteringProcessor implements BeanFactoryPostProcessor {
	public static final String BEAN_NAME = MockConstants.PACKAGE_PREFIX + "mockRegistrationContextPostProcessor";

	private final DoubleRegistry doubleRegistry;
	private final DoubleDefinitionsRegistrationContext doubleDefinitionsRegistrationContext;

	public MockDefinitionsRegisteringProcessor(DoubleRegistry doubleRegistry, DoubleDefinitionsRegistrationContext doubleDefinitionsRegistrationContext) {
		this.doubleRegistry = doubleRegistry;
		this.doubleDefinitionsRegistrationContext = doubleDefinitionsRegistrationContext;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
		doubleRegistry
				.mockSearch()
				.forEach(definition -> doubleDefinitionsRegistrationContext.registerMock(
						(BeanDefinitionRegistry) configurableListableBeanFactory,
						definition));
	}
}
