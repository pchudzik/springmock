package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;

import static com.pchudzik.springmock.infrastructure.definition.DoubleDefinitionMatchers.doubleWithName;
import static com.pchudzik.springmock.infrastructure.definition.registry.IterableHelper.getFirstElement;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class DoubleRegistryParserNestedClassesParsingTest {
	@Test
	public void should_detect_spy_and_mock_registered_only_in_configuration_class() {
		//when
		final DoubleRegistry doubleRegistry = DoubleRegistryTestParser.parseClass(SpyAndMockOnlyInConfiguration_TestCase.class);

		//then
		assertEquals(1, doubleRegistry.getMocks().size());
		assertThat(
				getFirstElement(doubleRegistry.getMocks()),
				doubleWithName(SpyAndMockOnlyInConfiguration_TestCase.MOCK_FIELD_NAME));

		assertEquals(1, doubleRegistry.getSpies().size());
		assertThat(
				getFirstElement(doubleRegistry.getSpies()),
				doubleWithName(SpyAndMockOnlyInConfiguration_TestCase.SPY_FIELD_NAME));
	}

	@Test
	public void should_reuse_spy_and_mock_definitions_defined_in_config_and_test() {
		//when
		final DoubleRegistry doubleRegistry = DoubleRegistryTestParser.parseClass(SameSpyAndMockInConfigurationAndTest_TestCase.class);

		//then
		assertEquals(1, doubleRegistry.getMocks().size());
		assertThat(
				getFirstElement(doubleRegistry.getMocks()),
				doubleWithName(SameSpyAndMockInConfigurationAndTest_TestCase.MOCK_FIELD_NAME));

		assertEquals(1, doubleRegistry.getSpies().size());
		assertThat(
				getFirstElement(doubleRegistry.getSpies()),
				doubleWithName(SameSpyAndMockInConfigurationAndTest_TestCase.SPY_FIELD_NAME));
	}

	@Test
	public void should_create_new_spy_and_mock_definitions_defined_for_config_and_tests_definitions() {
		//when
		final DoubleRegistry doubleRegistry = DoubleRegistryTestParser.parseClass(DifferentSpyAndMockInConfigurationAndTest_TestCase.class);

		//then
		assertEquals(2, doubleRegistry.getMocks().size());
		assertThat(doubleRegistry.getMocks(),
				containsInAnyOrder(
						doubleWithName(DifferentSpyAndMockInConfigurationAndTest_TestCase.TEST_MOCK_NAME),
						doubleWithName(DifferentSpyAndMockInConfigurationAndTest_TestCase.CONFIG_MOCK_NAME)));

		assertEquals(2, doubleRegistry.getSpies().size());
		assertThat(doubleRegistry.getSpies(),
				containsInAnyOrder(
						doubleWithName(DifferentSpyAndMockInConfigurationAndTest_TestCase.TEST_SPY_NAME),
						doubleWithName(DifferentSpyAndMockInConfigurationAndTest_TestCase.CONFIG_SPY_NAME)));
	}

	private static class SpyAndMockOnlyInConfiguration_TestCase {
		public static final String SPY_FIELD_NAME = "spy";
		public static final String MOCK_FIELD_NAME = "mock";

		@Configuration
		static class Config {
			@AutowiredSpy
			Object spy;

			@AutowiredMock
			Object mock;
		}
	}

	private static class SameSpyAndMockInConfigurationAndTest_TestCase {
		public static final String SPY_FIELD_NAME = "spy";
		public static final String MOCK_FIELD_NAME = "mock";

		@AutowiredSpy
		Object spy;

		@AutowiredMock
		Object mock;

		@Configuration
		static class Config {
			@AutowiredSpy
			Object spy;

			@AutowiredMock
			Object mock;
		}
	}

	private static class DifferentSpyAndMockInConfigurationAndTest_TestCase {
		public static final String TEST_SPY_NAME = "testSpy";
		public static final String TEST_MOCK_NAME = "testMock";
		public static final String CONFIG_SPY_NAME = "configSpy";
		public static final String CONFIG_MOCK_NAME = "configMock";

		@AutowiredSpy
		Object testSpy;

		@AutowiredMock
		Object testMock;

		@Configuration
		static class Config {
			@AutowiredSpy
			Object configSpy;

			@AutowiredMock
			Object configMock;
		}
	}
}
