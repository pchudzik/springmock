package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.MockConstants;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import com.pchudzik.springmock.infrastructure.spring.util.BeanDefinitionFinder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class SpyDefinitionRegisteringProcessor implements BeanFactoryPostProcessor {
	public static final String BEAN_NAME = MockConstants.PACKAGE_PREFIX + "spyRegisteringContextPostProcessor";

	private final DoubleRegistry doubleRegistry;
	private final DoubleDefinitionsRegistrationContext doubleDefinitionsRegistrationContext;

	public SpyDefinitionRegisteringProcessor(DoubleRegistry doubleRegistry, DoubleDefinitionsRegistrationContext doubleDefinitionsRegistrationContext) {
		this.doubleRegistry = doubleRegistry;
		this.doubleDefinitionsRegistrationContext = doubleDefinitionsRegistrationContext;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		final Collection<DoubleDefinition> spies = doubleRegistry.getSpies();
		for (DoubleDefinition spy : spies) {
			final Optional<BeanDefinition> beanDefinition = findBeanDefinition(beanFactory, spy);
			if (!beanDefinition.isPresent()) {
				doubleDefinitionsRegistrationContext.registerSpy((BeanDefinitionRegistry) beanFactory, spy);
			}
		}
	}

	private Optional<BeanDefinition> findBeanDefinition(ConfigurableListableBeanFactory beanFactory, DoubleDefinition spy) {
		final BeanDefinitionFinder definitionFinder = new BeanDefinitionFinder(beanFactory, doubleRegistry);
		final Optional<BeanDefinition> definition = Stream
				.concat(
						Stream.of(spy.getName()),
						spy.getAliases().stream())
				.map(name -> definitionFinder.tryToFindBeanDefinition(name, spy.getDoubleClass()))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();

		final boolean noDefinitionFound = !definition.isPresent();
		final boolean hasParentBeanFactory = beanFactory.getParentBeanFactory() != null;

		if (noDefinitionFound && hasParentBeanFactory) {
			return findBeanDefinition((ConfigurableListableBeanFactory) beanFactory.getParentBeanFactory(), spy);
		}

		return definition;
	}
}
