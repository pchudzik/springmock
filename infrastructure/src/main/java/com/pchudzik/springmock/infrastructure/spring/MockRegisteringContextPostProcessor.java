package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.MockConstants;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import com.pchudzik.springmock.infrastructure.spring.util.BeanDefinitionRegistrationHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
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
		final BeanDefinitionRegistrationHelper registrationHelper = new BeanDefinitionRegistrationHelper((BeanDefinitionRegistry) configurableListableBeanFactory);

		doubleRegistry
				.getMocks()
				.forEach(registrationHelper::registerMock);
	}
}
