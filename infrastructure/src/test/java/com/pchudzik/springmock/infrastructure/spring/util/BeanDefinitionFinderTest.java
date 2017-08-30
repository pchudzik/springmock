package com.pchudzik.springmock.infrastructure.spring.util;

import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;

import java.util.Optional;
import java.util.stream.Stream;

import static com.pchudzik.springmock.infrastructure.definition.DoubleDefinitionTestFactory.doubleDefinition;
import static com.pchudzik.springmock.infrastructure.spring.test.ApplicationContextCreator.bean;
import static com.pchudzik.springmock.infrastructure.spring.test.ApplicationContextCreator.buildAppContext;
import static com.pchudzik.springmock.infrastructure.spring.test.ApplicationContextCreator.emptyDoubleRegistry;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BeanDefinitionFinderTest {
	@Test
	public void should_definition_by_class_only_when_single_definition_present() {
		//given
		final ApplicationContext appContext = buildAppContext(Stream.of(bean("string", "any bean")));
		final BeanDefinitionFinder definitionFinder = new BeanDefinitionFinder(appContext, emptyDoubleRegistry());

		//when
		final Optional<BeanDefinition> definition = definitionFinder.tryToFindSingleBeanDefinition(String.class);

		//then
		assertTrue(definition.isPresent());
	}

	@Test
	public void should_find_nothing_when_more_then_one_definition_of_class_present() {
		//given
		final ApplicationContext appContext = buildAppContext(Stream.of(
				bean("object1", new Object()),
				bean("object2", new Object())));
		final BeanDefinitionFinder definitionFinder = new BeanDefinitionFinder(appContext, emptyDoubleRegistry());

		//when
		final Optional<BeanDefinition> definition = definitionFinder.tryToFindSingleBeanDefinition(Object.class);

		//then
		assertFalse(definition.isPresent());
	}

	@Test
	public void should_find_bean_definition_by_name() {
		//given
		final String beanName = "object";
		final ApplicationContext appContext = buildAppContext(Stream.of(
				bean(beanName, new Object())));
		final BeanDefinitionFinder definitionFinder = new BeanDefinitionFinder(appContext, emptyDoubleRegistry());

		//when
		final Optional<BeanDefinition> definition = definitionFinder.tryToFindBeanDefinition(beanName);

		//then
		assertTrue(definition.isPresent());
	}

	@Test
	public void should_fallback_to_find_by_class_search_if_no_name_match() {
		//given
		final ApplicationContext appContext = buildAppContext(Stream.of(
				bean("string", "any bean")));
		final BeanDefinitionFinder definitionFinder = new BeanDefinitionFinder(appContext, emptyDoubleRegistry());

		//when
		final Optional<BeanDefinition> definition = definitionFinder.tryToFindBeanDefinition(
				"not matching name",
				String.class);

		//then
		assertTrue(definition.isPresent());
	}

	@Test
	public void should_ignore_existing_spies_when_searching_for_bean_by_class() {
		//given
		final String spyName = "spy";
		final DoubleRegistry doubleRegistry = new DoubleRegistry(
				emptyList(),
				singletonList(doubleDefinition(String.class, spyName)));
		final ApplicationContext appCtx = buildAppContext(Stream.of(
				bean(spyName, spyName + " - bean")
		));
		final BeanDefinitionFinder definitionFinder = new BeanDefinitionFinder(appCtx, doubleRegistry);

		//when
		final Optional<BeanDefinition> definition = definitionFinder.tryToFindBeanDefinition("any bean name", String.class);

		//then
		assertFalse(definition.isPresent());
	}

	@Test
	public void should_ignore_existing_mocks_when_searching_for_bean_by_class() {
		//given
		final String mockName = "mock";
		final DoubleRegistry doubleRegistry = new DoubleRegistry(
				singletonList(doubleDefinition(String.class, mockName)),
				emptyList());
		final ApplicationContext appCtx = buildAppContext(Stream.of(
				bean(mockName, mockName + " - bean")
		));
		final BeanDefinitionFinder definitionFinder = new BeanDefinitionFinder(appCtx, doubleRegistry);

		//when
		final Optional<BeanDefinition> definition = definitionFinder.tryToFindBeanDefinition("any bean name", String.class);

		//then
		assertFalse(definition.isPresent());
	}
}