package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;

import static com.pchudzik.springmock.infrastructure.definition.DoubleDefinitionMatchers.*;
import static com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistryTestParser.parseClass;
import static com.pchudzik.springmock.infrastructure.definition.registry.DoubleSearchMatchers.hasSize;
import static com.pchudzik.springmock.infrastructure.definition.registry.IterableHelper.getOnlyElement;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.ReflectionUtils.findField;

public class DoubleRegistryParserTest {
	private static final Annotation NO_CONFIGURATION = null;

	private DoubleConfigurationParser configurationParser = Mockito.mock(DoubleConfigurationParser.class);

	@Test
	public void should_find_all_mock_definitions_registered_in_class() {
		//given
		final String anyService1Name = "anyService1";
		final String anyService2Name = "anyService2";
		class MultipleAutowiredMocks {
			@AutowiredMock(name = anyService1Name)
			AnyService1 anyService1;

			@AutowiredMock(name = anyService2Name)
			AnyService2 anyService2;
		}

		//when
		final DoubleRegistry registry = parseClass(MultipleAutowiredMocks.class);

		//then
		assertThat(registry.mockSearch(), hasSize(2));
		assertThat(
				registry.mockSearch(),
				containsInAnyOrder(
						allOf(doubleWithName(anyService1Name), doubleForClass(AnyService1.class)),
						allOf(doubleWithName(anyService2Name), doubleForClass(AnyService2.class))));
	}

	@Test
	public void should_find_all_defined_aliases_for_double() {
		//given
		final String alias1 = "alias1";
		final String alias2 = "alias2";
		class RegisteredAliasesMocks {
			@AutowiredMock(alias = {alias1, alias2})
			AnyService1 anyService;
		}

		//when
		final DoubleRegistry doubleRegistry = parseClass(RegisteredAliasesMocks.class);

		//then
		assertThat(
				getOnlyElement(doubleRegistry.mockSearch()),
				doubleWithAliases(asList(alias1, alias2)));
	}

	@Test
	public void should_register_double_alias_from_field_name() {
		//given
		final String fieldName = "mockedObject";
		final String doubleName = "mock";
		class TestCase {
			@AutowiredMock(name = doubleName)
			Object mockedObject;
		}

		//when
		final DoubleRegistry doubleRegistry = parseClass(TestCase.class);

		//then
		assertThat(
				getOnlyElement(doubleRegistry.mockSearch()),
				allOf(
						doubleWithAliases(singletonList(fieldName)),
						doubleWithName(doubleName)));
	}

	@Test
	public void should_generate_mocked_bean_name_from_field_when_name_not_provided() {
		//given
		class MockWithoutNameSpec {
			static final String FIELD_NAME = "mockedFieldName";

			@AutowiredMock
			AnyService1 mockedFieldName;
		}

		//when
		final DoubleRegistry doubleRegistry = parseClass(MockWithoutNameSpec.class);

		//then
		assertThat(
				getOnlyElement(doubleRegistry.mockSearch()),
				doubleWithName(MockWithoutNameSpec.FIELD_NAME));
	}

	@Test
	public void should_find_all_spies_registered_in_class() {
		//given
		final String service1Name = "anyService1";
		final String service2Name = "anyService2";
		class MultipleRegisteredSpies {
			@AutowiredSpy(name = service1Name)
			AnyService1 anyService1;

			@AutowiredSpy(name = service2Name)
			AnyService2 anyService2;
		}

		//when
		final DoubleRegistry doubleRegistry = parseClass(MultipleRegisteredSpies.class);

		//then
		assertThat(doubleRegistry.spySearch(), hasSize(2));
		assertThat(
				doubleRegistry.spySearch(),
				containsInAnyOrder(
						allOf(doubleWithName(service1Name), doubleForClass(AnyService1.class)),
						allOf(doubleWithName(service2Name), doubleForClass(AnyService2.class))));
	}

	@Test
	public void should_generate_spy_bean_name_from_field_when_name_not_provided() {
		//given
		class SpyWithoutNameSpec {
			static final String FIELD_NAME = "spiedFieldName";

			@AutowiredSpy
			AnyService1 spiedFieldName;
		}

		//when
		final DoubleRegistry doubleRegistry = parseClass(SpyWithoutNameSpec.class);

		//then
		assertThat(
				getOnlyElement(doubleRegistry.spySearch()),
				doubleWithName(SpyWithoutNameSpec.FIELD_NAME));
	}

	@Test
	public void should_generate_mock_configuration_when_config_annotation_missing() throws NoSuchFieldException {
		//given
		class MockWithoutNameSpec {
			static final String FIELD_NAME = "mockedFieldName";
			@AutowiredMock
			AnyService1 mockedFieldName;
		}
		final Object configuration = new Object();
		Mockito
				.when(configurationParser.parseMockConfiguration(MockWithoutNameSpec.FIELD_NAME, NO_CONFIGURATION))
				.thenReturn(configuration);

		//when
		final DoubleRegistry doubleRegistry = parseClass(MockWithoutNameSpec.class, configurationParser);

		//then
		assertThat(
				doubleRegistry.mockSearch(),
				hasItem(doubleWithConfiguration(configuration)));
	}


	@Test
	public void should_generate_spy_configuration_when_config_annotation_missing() throws NoSuchFieldException {
		//given
		class SpyWithoutNameSpec {
			static final String FIELD_NAME = "spiedFieldName";

			@AutowiredSpy
			AnyService1 spiedFieldName;
		}
		final Object configuration = new Object();
		Mockito
				.when(configurationParser.parseSpyConfiguration(SpyWithoutNameSpec.FIELD_NAME, NO_CONFIGURATION))
				.thenReturn(configuration);

		//when
		final DoubleRegistry doubleRegistry = parseClass(SpyWithoutNameSpec.class, configurationParser);

		//then
		assertThat(
				doubleRegistry.spySearch(),
				hasItem(doubleWithConfiguration(configuration)));
	}

	@Test
	public void should_parse_mock_configuration_from_field() {
		//given
		final Object configuration = new Object();
		final String doubleName = "anyService";
		class MockWithConfiguration {
			@AutowiredMock(name = doubleName)
			@DoubleDefinitionTestConfiguration
			AnyService1 anyService;
		}
		final DoubleDefinitionTestConfiguration configurationAnnotation = findAnnotation(
				findField(MockWithConfiguration.class, doubleName),
				DoubleDefinitionTestConfiguration.class);
		Mockito
				.when(configurationParser.parseMockConfiguration(doubleName, configurationAnnotation))
				.thenReturn(configuration);

		//when
		final DoubleRegistry doubleRegistry = parseClass(MockWithConfiguration.class, configurationParser);

		//then
		assertThat(
				doubleRegistry.mockSearch(),
				hasItem(doubleWithConfiguration(configuration)));
	}

	@Test
	public void should_parse_spy_configuration_from_field() {
		//given
		final String doubleName = "anyService";
		class SpyWithConfiguration {
			@AutowiredSpy(name = doubleName)
			@DoubleDefinitionTestConfiguration
			AnyService1 anyService;
		}
		final Object configuration = Mockito.mock(Object.class);
		final DoubleDefinitionTestConfiguration configurationAnnotation = findAnnotation(
				findField(SpyWithConfiguration.class, doubleName),
				DoubleDefinitionTestConfiguration.class);
		Mockito
				.when(configurationParser.parseSpyConfiguration(doubleName, configurationAnnotation))
				.thenReturn(configuration);

		//when
		final DoubleRegistry doubleRegistry = parseClass(SpyWithConfiguration.class, configurationParser);

		//then
		assertThat(
				doubleRegistry.spySearch(),
				hasItem(doubleWithConfiguration(configuration)));
	}

	private interface AnyService1 {
	}

	private interface AnyService2 {
	}
}