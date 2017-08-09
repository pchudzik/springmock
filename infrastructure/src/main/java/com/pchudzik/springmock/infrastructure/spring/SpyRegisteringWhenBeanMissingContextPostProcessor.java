package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.MockConstants;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleSearch;
import com.pchudzik.springmock.infrastructure.spring.util.BeanDefinitionFinder;
import com.pchudzik.springmock.infrastructure.spring.util.BeanDefinitionRegistrationHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.Optional;
import java.util.stream.Stream;

public class SpyRegisteringWhenBeanMissingContextPostProcessor implements BeanFactoryPostProcessor {
	public static final String BEAN_NAME = MockConstants.PACKAGE_PREFIX + "spyRegisteringContextPostProcessor";

	private final DoubleRegistry doubleRegistry;

	public SpyRegisteringWhenBeanMissingContextPostProcessor(DoubleRegistry doubleRegistry) {
		this.doubleRegistry = doubleRegistry;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		final DoubleSearch spies = doubleRegistry.spySearch();
		final BeanDefinitionRegistrationHelper registrationHelper = new BeanDefinitionRegistrationHelper((BeanDefinitionRegistry) beanFactory);
		for (DoubleDefinition spy : spies) {
			final Optional<BeanDefinition> beanDefinition = findBeanDefinition(beanFactory, spy);
			if (!beanDefinition.isPresent()) {
				registrationHelper.registerSpy(spy);
			}
		}
	}

	private Optional<BeanDefinition> findBeanDefinition(ConfigurableListableBeanFactory beanFactory, DoubleDefinition spy) {
		final BeanDefinitionFinder definitionFinder = new BeanDefinitionFinder(beanFactory);
		final Optional<BeanDefinition> definition = Stream
				.concat(
						Stream.of(spy.getName()),
						spy.getAliases().stream())
				.map(name -> definitionFinder.tryToFindBeanDefinition(name, spy.getDoubleClass()))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();

		if (!definition.isPresent() && beanFactory.getParentBeanFactory() != null) {
			return findBeanDefinition((ConfigurableListableBeanFactory) beanFactory.getParentBeanFactory(), spy);
		}

		return definition;
	}
}
