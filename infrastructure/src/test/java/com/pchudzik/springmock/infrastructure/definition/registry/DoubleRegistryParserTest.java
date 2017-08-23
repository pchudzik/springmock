package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;

import static com.pchudzik.springmock.infrastructure.definition.DoubleDefinitionMatchers.*;
import static com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistryTestParser.parseClass;
import static com.pchudzik.springmock.infrastructure.definition.registry.IterableHelper.getFirstElement;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.ReflectionUtils.findField;

public class DoubleRegistryParserTest {
	private static final Annotation NO_CONFIGURATION = null;

	private static final String ANY_SERVICE1_NAME = "anyService1";
	private static final String SERVICE1_ALIAS_1 = "service1";
	private static final String SERVICE1_ALIAS_2 = "service2";

	private static final String ANY_SERVICE2_NAME = "anyService2";

	private DoubleConfigurationParser configurationParser = Mockito.mock(DoubleConfigurationParser.class);

	@Test
	public void should_find_all_mock_definitions_registered_in_class() {
		//when
		final DoubleRegistry registry = parseClass(MultipleAutowiredMocks.class);

		//then
		assertEquals(registry.getMocks().size(), 2);
		assertThat(
				registry.getMocks(),
				containsInAnyOrder(
						allOf(doubleWithName(ANY_SERVICE1_NAME), doubleForClass(AnyService1.class)),
						allOf(doubleWithName(ANY_SERVICE2_NAME), doubleForClass(AnyService2.class))
				));
	}

	@Test
	public void should_find_all_defined_aliases_for_bean() {
		//when
		final DoubleRegistry doubleRegistry = parseClass(RegisteredAliasesMocks.class);

		//then
		assertEquals(1, doubleRegistry.getMocks().size());
		assertThat(
				getFirstElement(doubleRegistry.getMocks()),
				doubleWithAliases(asList(SERVICE1_ALIAS_1, SERVICE1_ALIAS_2)));
	}

	@Test
	public void should_generate_mocked_bean_name_from_field_when_name_not_provided() {
		//when
		final DoubleRegistry doubleRegistry = parseClass(MockWithoutNameSpec.class);

		//then
		assertEquals(1, doubleRegistry.getMocks().size());
		assertThat(
				getFirstElement(doubleRegistry.getMocks()),
				doubleWithName(MockWithoutNameSpec.FIELD_NAME));
	}

	@Test
	public void should_find_all_spies_registered_in_class() {
		//when
		final DoubleRegistry doubleRegistry = parseClass(MultipleRegisteredSpies.class);

		//then
		assertEquals(2, doubleRegistry.getSpies().size());
		assertThat(
				doubleRegistry.getSpies(),
				containsInAnyOrder(
						allOf(doubleWithName(ANY_SERVICE1_NAME), doubleForClass(AnyService1.class)),
						allOf(doubleWithName(ANY_SERVICE2_NAME), doubleForClass(AnyService2.class))));
	}

	@Test
	public void should_generate_spy_bean_name_from_field_when_name_not_provided() {
		//when
		final DoubleRegistry doubleRegistry = parseClass(SpyWithoutNameSpec.class);

		//then
		assertEquals(1, doubleRegistry.getSpies().size());
		assertThat(
				getFirstElement(doubleRegistry.getSpies()),
				doubleWithName(SpyWithoutNameSpec.FIELD_NAME));
	}

	@Test
	public void should_generate_mock_configuration_when_config_annotation_missing() throws NoSuchFieldException {
		//given
		final Object configuration = new Object();
		Mockito
				.when(configurationParser.parseMockConfiguration(MockWithoutNameSpec.FIELD_NAME, NO_CONFIGURATION))
				.thenReturn(configuration);

		//when
		final DoubleRegistry doubleRegistry = parseClass(MockWithoutNameSpec.class, configurationParser);

		//then
		assertThat(
				doubleRegistry.getMocks(),
				hasItem(doubleWithConfiguration(configuration)));
	}


	@Test
	public void should_generate_spy_configuration_when_config_annotation_missing() throws NoSuchFieldException {
		//given
		final Object configuration = new Object();
		Mockito
				.when(configurationParser.parseSpyConfiguration(SpyWithoutNameSpec.FIELD_NAME, NO_CONFIGURATION))
				.thenReturn(configuration);

		//when
		final DoubleRegistry doubleRegistry = parseClass(SpyWithoutNameSpec.class, configurationParser);

		//then
		assertThat(
				doubleRegistry.getSpies(),
				hasItem(doubleWithConfiguration(configuration)));
	}

	@Test
	public void should_parse_mock_configuration_from_field() {
		//given
		final Object configuration = new Object();
		final DoubleDefinitionTestConfiguration configurationAnnotation = findAnnotation(
				findField(MockWithConfiguration.class, ANY_SERVICE1_NAME),
				DoubleDefinitionTestConfiguration.class);
		Mockito
				.when(configurationParser.parseMockConfiguration(ANY_SERVICE1_NAME, configurationAnnotation))
				.thenReturn(configuration);

		//when
		final DoubleRegistry doubleRegistry = parseClass(MockWithConfiguration.class, configurationParser);

		//then
		assertThat(
				doubleRegistry.getMocks(),
				hasItem(doubleWithConfiguration(configuration)));
	}

	@Test
	public void should_parse_spy_configuration_from_field() {
		//given
		final Object configuration = Mockito.mock(Object.class);
		final DoubleDefinitionTestConfiguration configurationAnnotation = findAnnotation(
				findField(SpyWithConfiguration.class, ANY_SERVICE1_NAME),
				DoubleDefinitionTestConfiguration.class);
		Mockito
				.when(configurationParser.parseSpyConfiguration(ANY_SERVICE1_NAME, configurationAnnotation))
				.thenReturn(configuration);

		//when
		final DoubleRegistry doubleRegistry = parseClass(SpyWithConfiguration.class, configurationParser);

		//then
		assertThat(
				doubleRegistry.getSpies(),
				hasItem(doubleWithConfiguration(configuration)));
	}

	private interface AnyService1 {
	}

	private interface AnyService2 {
	}

	private static class MockWithConfiguration {
		@AutowiredMock
		@DoubleDefinitionTestConfiguration
		AnyService1 anyService1;
	}

	private static class SpyWithConfiguration {
		@AutowiredSpy
		@DoubleDefinitionTestConfiguration
		AnyService1 anyService1;
	}

	private static class RegisteredAliasesMocks {
		public static final String FIELD_NAME = "anyService";

		@AutowiredMock(name = ANY_SERVICE1_NAME, alias = {SERVICE1_ALIAS_1, SERVICE1_ALIAS_2})
		AnyService1 anyService;
	}

	private static class MultipleAutowiredMocks {
		@AutowiredMock(name = ANY_SERVICE1_NAME)
		AnyService1 anyService1;

		@AutowiredMock(name = ANY_SERVICE2_NAME)
		AnyService2 anyService2;
	}

	private static class MultipleRegisteredSpies {
		@AutowiredSpy(name = ANY_SERVICE1_NAME)
		AnyService1 anyService1;

		@AutowiredSpy(name = ANY_SERVICE2_NAME)
		AnyService2 anyService2;
	}

	private static class MockWithoutNameSpec {
		public static final String FIELD_NAME = "mockedFieldName";

		@AutowiredMock
		AnyService1 mockedFieldName;
	}

	private static class SpyWithoutNameSpec {
		public static final String FIELD_NAME = "spiedFieldName";

		@AutowiredSpy
		AnyService1 spiedFieldName;
	}
}