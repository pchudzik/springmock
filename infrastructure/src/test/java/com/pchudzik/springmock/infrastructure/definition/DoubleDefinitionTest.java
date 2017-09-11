package com.pchudzik.springmock.infrastructure.definition;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DoubleDefinitionTest {
	@Test(expected = IllegalArgumentException.class)
	public void should_throw_exception_when_configuration_of_unexpected_class() {
		//given
		final DoubleDefinition doubleDefinition = DoubleDefinition.builder()
				.name("mock")
				.doubleClass(Object.class)
				.doubleConfiguration(String.class)
				.build();

		//when
		doubleDefinition.getConfiguration(Double.class);
	}

	@Test
	public void should_return_empty_null_when_configuration_not_provided() {
		//given
		final DoubleDefinition withBuilderDefaultDefinition = DoubleDefinition.builder()
				.name("mock")
				.doubleClass(Object.class)
				.build();

		//expect
		assertNull(withBuilderDefaultDefinition.getConfiguration(Map.class));
	}

	@Test
	public void should_return_empty_when_no_configuration_present() {
		//given
		final DoubleDefinition withNullDefinition = DoubleDefinition.builder()
				.name("mock")
				.doubleClass(Object.class)
				.doubleConfiguration(null)
				.build();

		//expect
		assertNull(withNullDefinition.getConfiguration(Map.class));
	}

	@Test
	public void should_return_configuration_object() {
		//given
		final Map<String, String> configuration = new HashMap<>();
		configuration.put("key", "value 1234");
		final DoubleDefinition definition = DoubleDefinition.builder()
				.name("mock")
				.doubleClass(Object.class)
				.doubleConfiguration(configuration)
				.build();

		//expect
		assertNotNull(definition.getConfiguration(Map.class));
		assertEquals(
				configuration,
				definition.getConfiguration(Map.class));
	}

	@Test
	public void should_detect_definition_class() {
		//given
		class Parent {}
		class Child extends Parent {}

		//when
		final DoubleDefinition definition = DoubleDefinition.builder()
				.name("mock")
				.doubleClass(Child.class)
				.build();

		//then
		assertTrue(definition.hasClass(Object.class));
		assertTrue(definition.hasClass(Parent.class));
		assertTrue(definition.hasClass(Child.class));

		assertFalse(definition.hasClass(DoubleDefinition.class));
	}

}