package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.junit.Test;
import org.mockito.Mockito;

import static com.pchudzik.springmock.infrastructure.definition.DoubleDefinitionMatchers.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.util.ReflectionUtils.findField;

public class DoubleDefinitionRegistryFactoryTest {
	private static final String ANY_SERVICE1_NAME = "anyService1";
	private static final String SERVICE1_ALIAS_1 = "service1";
	private static final String SERVICE1_ALIAS_2 = "service2";

	private static final String ANY_SERVICE2_NAME = "anyService2";

	private DoubleConfigurationParser configurationParser = Mockito.mock(DoubleConfigurationParser.class);

	@Test
	public void should_find_all_mock_definitions_registered_in_class() {
		//when
		final DoubleRegistry registry = parseRegistry(MultipleAutowiredMocks.class);

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
		final DoubleRegistry doubleRegistry = parseRegistry(RegisteredAliasesMocks.class);

		//then
		assertEquals(1, doubleRegistry.getMocks().size());
		assertThat(
				getFirstElement(doubleRegistry.getMocks()),
				doubleWithAliases(asList(SERVICE1_ALIAS_1, SERVICE1_ALIAS_2)));
	}

	@Test
	public void should_generate_mocked_bean_name_from_field_when_name_not_provided() {
		//when
		final DoubleRegistry doubleRegistry = parseRegistry(MockWithoutNameSpec.class);

		//then
		assertEquals(1, doubleRegistry.getMocks().size());
		assertThat(
				getFirstElement(doubleRegistry.getMocks()),
				doubleWithName(MockWithoutNameSpec.FIELD_NAME));
	}

	@Test
	public void should_find_all_spies_registered_in_class() {
		//when
		final DoubleRegistry doubleRegistry = parseRegistry(MultipleRegisteredSpies.class);

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
		final DoubleRegistry doubleRegistry = parseRegistry(SpyWithoutNameSpec.class);

		//then
		assertEquals(1, doubleRegistry.getSpies().size());
		assertThat(
				getFirstElement(doubleRegistry.getSpies()),
				doubleWithName(SpyWithoutNameSpec.FIELD_NAME));
	}

	@Test
	public void should_parse_mock_configuration() throws NoSuchFieldException {
		//when
		parseRegistry(RegisteredAliasesMocks.class);

		//then
		Mockito
				.verify(configurationParser)
				.parseDoubleConfiguration(findField(RegisteredAliasesMocks.class, RegisteredAliasesMocks.FIELD_NAME));
	}


	@Test
	public void should_parse_spy_configuration() throws NoSuchFieldException {
		//when
		parseRegistry(SpyWithoutNameSpec.class);

		//then
		Mockito
				.verify(configurationParser)
				.parseDoubleConfiguration(findField(SpyWithoutNameSpec.class, SpyWithoutNameSpec.FIELD_NAME));
	}

	public DoubleRegistry parseRegistry(Class<?> clazz) {
		final DoubleDefinitionRegistryFactory definitionRegistryFactory = new DoubleDefinitionRegistryFactory(configurationParser);
		return definitionRegistryFactory.parse(clazz);
	}

	private interface AnyService1 {
	}

	private interface AnyService2 {
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

	private static <T> T getFirstElement(Iterable<T> iterable) {
		if (!iterable.iterator().hasNext()) {
			throw new IllegalStateException("Expected at leas one element");
		}

		return iterable.iterator().next();
	}
}