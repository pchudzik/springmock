package com.pchudzik.springmock.infrastructure.definition;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
	public void should_properly_return_empty_when_configuration_not_provided() {
		//given
		final DoubleDefinition withBuilderDefaultDefinition = DoubleDefinition.builder()
				.name("mock")
				.doubleClass(Object.class)
				.build();

		//expect
		assertEquals(
				Optional.empty(),
				withBuilderDefaultDefinition.getConfiguration(Map.class));
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
		assertEquals(
				Optional.empty(),
				withNullDefinition.getConfiguration(Map.class));
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
		assertTrue(definition.getConfiguration(Map.class).isPresent());
		assertEquals(
				configuration,
				definition.getConfiguration(Map.class).get());
	}

}