package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.MockConstants;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class DoubleRegisteringProcessor implements BeanFactoryPostProcessor {
	public static final String BEAN_NAME = MockConstants.PACKAGE_PREFIX + "doubleRegisteringProcessor";
	private final DoubleRegistry doubleRegistry;
	private final DoubleDefinitionsRegistrationContext definitionsRegistrationContext;

	DoubleRegisteringProcessor(DoubleRegistry doubleRegistry, DoubleDefinitionsRegistrationContext definitionsRegistrationContext) {
		this.doubleRegistry = doubleRegistry;
		this.definitionsRegistrationContext = definitionsRegistrationContext;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		final BeanDefinitionFinder beanDefinitionFinder = new BeanDefinitionFinder(beanFactory);

		doubleRegistry
				.mockSearch()
				.forEach(mockDefinition -> postProcessBeanFactoryForMock(beanDefinitionFinder, mockDefinition));

		doubleRegistry
				.spySearch()
				.forEach(spyDefinition -> postProcessBeanFactoryForSpy(beanDefinitionFinder, spyDefinition));

		beanDefinitionFinder.execute();
	}

	private void postProcessBeanFactoryForSpy(BeanDefinitionFinder beanDefinitionFinder, DoubleDefinition spyDefinition) {
		final String definitionNameFoundByName = beanDefinitionFinder.getSingleMatchByNameOrAlias(spyDefinition);
		if(definitionNameFoundByName != null) {
			return;
		}

		final String definitionNameFoundByClass = beanDefinitionFinder.getSingleMatchByClass(spyDefinition);
		if(definitionNameFoundByClass != null) {
			return;
		}

		beanDefinitionFinder.registerSpy(spyDefinition);
	}

	private void postProcessBeanFactoryForMock(BeanDefinitionFinder beanDefinitionFinder, DoubleDefinition doubleDefinition) {
		final Optional<String> beanDefinitionName = Stream
				.<Supplier<String>>of(
						() -> beanDefinitionFinder.getSingleMatchByNameOrAlias(doubleDefinition),
						() -> beanDefinitionFinder.getSingleMatchByClass(doubleDefinition))
				.map(Supplier::get)
				.filter(Objects::nonNull)
				.findFirst();

		if (beanDefinitionName.isPresent()) {
			beanDefinitionFinder.replaceBeanDefinitionWithMock(beanDefinitionName.get(), doubleDefinition);
		} else {
			beanDefinitionFinder.registerMock(doubleDefinition);
		}
	}

	private class BeanDefinitionFinder {
		private final ConfigurableListableBeanFactory beanFactory;
		private final Collection<Runnable> definitionsToRegister = new LinkedList<>();

		private BeanDefinitionFinder(ConfigurableListableBeanFactory beanFactory) {
			this.beanFactory = beanFactory;
		}

		public void execute() {
			definitionsToRegister.forEach(Runnable::run);
		}

		public String getSingleMatchByNameOrAlias(DoubleDefinition doubleDefinition) {
			return Stream
					.concat(
							Stream.of(doubleDefinition.getName()),
							doubleDefinition.getAliases().stream())
					.map(this::getBeanDefinition)
					.filter(Objects::nonNull)
					.findFirst()
					.orElse(null);
		}

		public String getSingleMatchByClass(DoubleDefinition doubleDefinition) {
			final List<String> allBeanDefinitions = getAllBeanDefinitionNames(doubleDefinition.getDoubleClass());
			if (allBeanDefinitions.size() == 1) {
				return allBeanDefinitions.get(0);
			}

			return null;
		}

		public void replaceBeanDefinitionWithMock(String definitionName, DoubleDefinition doubleDefinition) {
			definitionsToRegister.add(() -> definitionsRegistrationContext.registerMock((BeanDefinitionRegistry) beanFactory, definitionName, doubleDefinition));
		}

		public void registerMock(DoubleDefinition doubleDefinition) {
			definitionsToRegister.add(() -> definitionsRegistrationContext.registerMock((BeanDefinitionRegistry) beanFactory, doubleDefinition));
		}

		public void registerSpy(DoubleDefinition spyDefinition) {
			definitionsToRegister.add(() -> definitionsRegistrationContext.registerSpy((BeanDefinitionRegistry) beanFactory, spyDefinition));
		}

		private String getBeanDefinition(String name) {
			try {
				return beanFactory.getMergedBeanDefinition(name) != null
						? name
						: null;
			} catch (NoSuchBeanDefinitionException ex) {
				return null;
			}
		}

		private List<String> getAllBeanDefinitionNames(Class<?> beanClass) {
			return Stream
					.of(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, beanClass))
					.map(this::getBeanDefinition)
					.collect(toList());
		}
	}
}
