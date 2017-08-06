package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Context customizer which will register all required infrastructure to properly initialize mocks
 * and spies in tests
 */
public class MockContextCustomizer implements ContextCustomizer {
	protected final Class<? extends DoubleFactory> mockFactoryClass;
	protected final DoubleRegistry doubleRegistry;
	protected final Map<String, BeanDefinition> additionalDefinitions;

	public MockContextCustomizer(Class<? extends DoubleFactory> mockFactoryClass, DoubleRegistry doubleRegistry, Map<String, BeanDefinition> additionalDefinitions) {
		this.mockFactoryClass = mockFactoryClass;
		this.doubleRegistry = doubleRegistry;
		this.additionalDefinitions = new HashMap<>(additionalDefinitions);
	}

	/**
	 * <p>Will register mock factory and all additional beans required by springmock.</p>
	 *
	 * <p>It is possible to register and inject more beans passing additionalDefinitions to the constructor</p>
	 *
	 * @param configurableApplicationContext
	 * @param mergedContextConfiguration
	 */
	public final void customizeContext(ConfigurableApplicationContext configurableApplicationContext, MergedContextConfiguration mergedContextConfiguration) {
		final BeanDefinitionRegistry registry = (BeanDefinitionRegistry) configurableApplicationContext;

		registerDoubleRegistry(registry);
		registerDoubleFactory(registry);

		registerMockRegistrationPostProcessor(registry);
		registerMockClassResolver(registry);

		registerSpyRegistrationPostProcessor(registry);

		registerAdditionalBeanDefinitions(registry, additionalDefinitions);
	}

	/**
	 * Equals and hashCode are a must in order to propely cache created contexts by spring context cache layer
	 *
	 * @param o
	 * @return
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final MockContextCustomizer that = (MockContextCustomizer) o;
		return Objects.equals(mockFactoryClass, that.mockFactoryClass) &&
				Objects.equals(doubleRegistry, that.doubleRegistry) &&
				Objects.equals(additionalDefinitions, that.additionalDefinitions);
	}

	/**
	 * Equals and hashCode are a must in order to propely cache created contexts by spring context cache layer
	 *
	 * @return
	 */
	@Override
	public int hashCode() {
		return Objects.hash(mockFactoryClass, doubleRegistry, additionalDefinitions);
	}

	protected void registerAdditionalBeanDefinitions(BeanDefinitionRegistry registry, Map<String, BeanDefinition> additionalDefinitions) {
		additionalDefinitions
				.entrySet()
				.forEach(entry -> registry.registerBeanDefinition(entry.getKey(), entry.getValue()));
	}

	private void registerSpyRegistrationPostProcessor(BeanDefinitionRegistry registry) {
		registry.registerBeanDefinition(SpyReplacingContextPostProcessor.BEAN_NAME, BeanDefinitionBuilder
				.rootBeanDefinition(SpyReplacingContextPostProcessor.class)
				.getBeanDefinition());

		registry.registerBeanDefinition(SpyRegisteringWhenBeanMissingContextPostProcessor.BEAN_NAME, BeanDefinitionBuilder
				.rootBeanDefinition(SpyRegisteringWhenBeanMissingContextPostProcessor.class)
				.addConstructorArgValue(doubleRegistry)
				.getBeanDefinition());
	}

	private void registerMockClassResolver(BeanDefinitionRegistry registry) {
		registry.registerBeanDefinition(MockClassResolver.BEAN_NAME, BeanDefinitionBuilder
				.rootBeanDefinition(MockClassResolver.class)
				.addConstructorArgReference(DoubleRegistry.BEAN_NAME)
				.getBeanDefinition());
	}

	private void registerDoubleRegistry(BeanDefinitionRegistry registry) {
		registry.registerBeanDefinition(DoubleRegistryHolder.BEAN_NAME, BeanDefinitionBuilder
				.rootBeanDefinition(DoubleRegistryHolder.class)
				.addConstructorArgValue(doubleRegistry)
				.getBeanDefinition());

		registry.registerBeanDefinition(DoubleRegistry.BEAN_NAME, BeanDefinitionBuilder
				.rootBeanDefinition(DoubleRegistry.class)
				.setFactoryMethodOnBean(DoubleRegistryHolder.REGISTRY_FACTORY_METHOD, DoubleRegistryHolder.BEAN_NAME)
				.getBeanDefinition());
	}

	private void registerDoubleFactory(BeanDefinitionRegistry registry) {
		registry.registerBeanDefinition(DoubleFactory.DOUBLE_FACTORY_BEAN_NAME, BeanDefinitionBuilder
				.rootBeanDefinition(mockFactoryClass)
				.getBeanDefinition());
	}

	private void registerMockRegistrationPostProcessor(BeanDefinitionRegistry registry) {
		registry.registerBeanDefinition(MockRegisteringContextPostProcessor.BEAN_NAME, BeanDefinitionBuilder
				.rootBeanDefinition(MockRegisteringContextPostProcessor.class)
				.addConstructorArgReference(DoubleRegistry.BEAN_NAME)
				.getBeanDefinition());
	}
}
