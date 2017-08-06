package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.MockConstants;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleSearch;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
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
		for (DoubleDefinition spy : spies) {
			final Optional<BeanDefinition> beanDefinition = findBeanDefinition(beanFactory, spy);
			if(!beanDefinition.isPresent()) {
				final BeanDefinitionRegistry definitionRegistry = (BeanDefinitionRegistry) beanFactory;
				definitionRegistry.registerBeanDefinition(spy.getName(), createSpyDefinition(spy));
				spy.getAliases().forEach(alias -> definitionRegistry.registerAlias(spy.getName(), alias));
			}
		}
	}

	private BeanDefinition createSpyDefinition(DoubleDefinition spy) {
		return BeanDefinitionBuilder
				.rootBeanDefinition(spy.getDoubleClass())
				.setFactoryMethodOnBean(DoubleFactory.CREATE_SPY_FACTORY_METHOD, DoubleFactory.DOUBLE_FACTORY_BEAN_NAME)
				.addConstructorArgValue(null)
				.addConstructorArgValue(spy)
				.getBeanDefinition();
	}

	private Optional<BeanDefinition> findBeanDefinition(ConfigurableListableBeanFactory beanFactory, DoubleDefinition spy) {
		final Optional<BeanDefinition> definition = Stream
				.concat(
						Stream.of(spy.getName()),
						spy.getAliases().stream())
				.map(name -> findBeanDefinition(beanFactory, spy.getDoubleClass(), name))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();

		if(!definition.isPresent() && beanFactory.getParentBeanFactory() != null) {
			return findBeanDefinition((ConfigurableListableBeanFactory)beanFactory.getParentBeanFactory(), spy);
		}

		return definition;
	}

	private Optional<BeanDefinition> findBeanDefinition(ConfigurableListableBeanFactory beanFactory, Class<?> doubleClass, String beanName) {
		try {
			return Optional.of(beanFactory.getBeanDefinition(beanName));
		} catch (NoSuchBeanDefinitionException ex) {
			final String[] beanNamesForType = beanFactory.getBeanNamesForType(doubleClass);
			if(beanNamesForType.length == 1) {
				return Optional.of(beanFactory.getBeanDefinition(beanNamesForType[0]));
			}
			return Optional.empty();
		}
	}
}
