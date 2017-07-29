package com.pchudzik.springmock.infrastructure.spring.util;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;

import static com.pchudzik.springmock.infrastructure.spring.test.ApplicationContextCreator.buildAppContext;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ApplicationContextWalkerTest {
	@Test
	public void should_find_names_of_beans_from_complete_context_hierarchy() {
		//given
		final String childName = "child";
		final String middleName = "middle";
		final String parentName = "parent";
		final ApplicationContext childContext = buildAppContext(Stream.of(new SimpleEntry<>(childName, new Object())));
		final ApplicationContext middleContext = buildAppContext(childContext, Stream.of(new SimpleEntry<>(middleName, new Object())));
		final ApplicationContext parentContext = buildAppContext(middleContext, Stream.of(new SimpleEntry<>(parentName, new Object())));
		final ApplicationContextWalker walker = new ApplicationContextWalker(parentContext);

		//when
		final Collection<String> beanNames= walker.getBeanDefinitionNames();

		//then
		assertEquals(
				new HashSet<>(asList(childName, middleName, parentName)),
				new HashSet<>(beanNames));
	}

	@Test
	public void should_find_bean_definition_from_any_context_in_hierarchy() {
		//given
		final String childName = "child";
		final String middleName = "middle";
		final String parentName = "parent";
		final ApplicationContext childContext = buildAppContext(Stream.of(new SimpleEntry<>(childName, new Object())));
		final ApplicationContext middleContext = buildAppContext(childContext, Stream.of(new SimpleEntry<>(middleName, new Object())));
		final ApplicationContext parentContext = buildAppContext(middleContext, Stream.of(new SimpleEntry<>(parentName, new Object())));
		final ApplicationContextWalker walker = new ApplicationContextWalker(parentContext);

		//when
		final BeanDefinition childDefinition = walker.getBeanDefinition(childName);
		final BeanDefinition middleDefinition = walker.getBeanDefinition(middleName);
		final BeanDefinition parentDefinition = walker.getBeanDefinition(parentName);

		//then
		assertNotNull(childDefinition);
		assertNotNull(middleDefinition);
		assertNotNull(parentDefinition);
	}
}