package com.pchudzik.springmock.infrastructure.spring.util;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;

import java.util.AbstractMap.SimpleEntry;
import java.util.Optional;
import java.util.stream.Stream;

import static com.pchudzik.springmock.infrastructure.spring.test.ApplicationContextCreator.buildAppContext;
import static org.junit.Assert.*;

public class BeanDefinitionFinderTest {
	@Test
	public void should_definition_by_class_only_when_single_definition_present() {
		//given
		final ApplicationContext appContext = buildAppContext(Stream.of(
				new SimpleEntry<>("string", "any bean")));
		final BeanDefinitionFinder definitionFinder = new BeanDefinitionFinder(appContext);

		//when
		final Optional<BeanDefinition> definition = definitionFinder.tryToFindSingleBeanDefinition(String.class);

		//then
		assertTrue(definition.isPresent());
	}

	@Test
	public void should_find_nothing_when_more_then_one_definition_of_class_present() {
		//given
		final ApplicationContext appContext = buildAppContext(Stream.of(
				new SimpleEntry<>("object1", new Object()),
				new SimpleEntry<>("object2", new Object())));
		final BeanDefinitionFinder definitionFinder = new BeanDefinitionFinder(appContext);

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
				new SimpleEntry<>(beanName, new Object())));
		final BeanDefinitionFinder definitionFinder = new BeanDefinitionFinder(appContext);

		//when
		final Optional<BeanDefinition> definition = definitionFinder.tryToFindBeanDefinition(beanName);

		//then
		assertTrue(definition.isPresent());
	}

	@Test
	public void should_fallback_to_find_by_class_search_if_no_name_match() {
		//given
		final ApplicationContext appContext = buildAppContext(Stream.of(
				new SimpleEntry<>("string", "any bean")));
		final BeanDefinitionFinder definitionFinder = new BeanDefinitionFinder(appContext);

		//when
		final Optional<BeanDefinition> definition = definitionFinder.tryToFindBeanDefinition(
				"not matching name",
				String.class);

		//then
		assertTrue(definition.isPresent());
	}
}